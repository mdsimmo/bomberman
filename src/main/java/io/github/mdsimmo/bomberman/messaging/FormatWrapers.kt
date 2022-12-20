package io.github.mdsimmo.bomberman.messaging

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.roundToLong
import kotlin.random.Random


class ItemWrapper(private val item: ItemStack) : Formattable {

    override fun format(context: Context): Message {
        return Text.ITEM_FORMAT.format(context.newScope().plus("item", this))
    }

    override fun applyModifier(arg: Message): Formattable {
        return when (arg.toString().lowercase()) {
            "amount" -> Message.of(item.amount)
            "type" -> Message.of(item.type.key.toString())
            else -> Message.empty
        }
    }

}

class SenderWrapper(private val sender: CommandSender) : PartialWrapper({

    PartialDefault("name") { arg ->
            when (arg.toString().lowercase()) {
                "name" -> Message.of(sender.name)
                "msg" -> {
                    PartialRequired { arg2 ->
                            arg2.sendTo(sender)
                            Message.empty
                        }
                }

                "exec" -> {
                    PartialRequired { arg2 ->
                        PartialContext { context ->
                            if (context.elevated) {
                                val cmd = arg2.toString()
                                Bukkit.getServer().dispatchCommand(sender, cmd)
                            }
                            Message.empty
                        }
                    }
                }
                else -> {
                    throw IllegalArgumentException("Unknown CommandSender option: $arg")
                }
            }
        }
})

class ColorWrapper(private val color: ChatColor) : PartialWrapper ({
    PartialRequired { arg ->
        arg.color(color)
    }
})

class CollectionWrapper<T : Formattable>(private val list: Collection<T>) : PartialWrapper({

    PartialDefault("length") { argProperty: Message ->
        when (argProperty.toString().lowercase()) {
            "length" -> Message.of(list.size)
            "foreach" -> {
                PartialDefault("({index}: {it})") { argMapper ->
                    PartialDefault(" ") { argSeparator ->
                        PartialContext { context ->
                            val mapper = argMapper.toString()
                            list
                                .mapIndexed { i, item ->
                                    Expander.expand(
                                        mapper, context
                                            .plus("it", item)
                                            .plus("index", i)
                                    )
                                }
                                .ifEmpty { listOf(Message.empty) }
                                .reduce { a, b -> a.append(argSeparator).append(b) }
                        }
                    }
                }
            }
            "sort" -> {
                PartialDefault("{it}") { argMapper ->
                    PartialContext { context ->
                        val sorted = list.withIndex().sortedBy {
                            Expander.expand(
                                argMapper.toString(), context
                                    .plus("it", it.value)
                                    .plus("index", Message.of(it.index))
                            ).toString()
                        }
                            .map { it.value }
                        CollectionWrapper(sorted)
                    }
                }
            }
            "filter" -> {
                PartialRequired { argFilter ->
                    PartialContext { context ->
                        val filtered =
                            list.withIndex().filter {
                                Expander.expand(
                                    argFilter.toString(), context
                                        .plus("it", it.value)
                                        .plus("index", Message.of(it.index))
                                )
                                    .toString()
                                    .isNotBlank()
                            }.map { it.value }
                        CollectionWrapper(filtered)
                    }
                }
            }
            else -> throw IllegalArgumentException("Unknown list option: $argProperty")
        }
    }
})

class TitleExpander : PartialWrapper ({
    PartialRequired { title ->
        PartialDefault(Message.empty) { subtitle ->
            PartialDefault(Message.of(0)) { argFadeIn ->
                PartialDefault(Message.of(20)) { argDuration ->
                    PartialDefault(Message.of(0)) { argFadeOut ->
                        val fadeIn = argFadeIn.toString().toInt()
                        val duration = argDuration.toString().toInt()
                        val fadeOut = argFadeOut.toString().toInt()
                        Message.title(title, subtitle, fadeIn, duration, fadeOut)
                    }
                }
            }
        }
    }
})

class Switch : Formattable {
    private class ArgsBuilder(val value: String, val args: List<Message>) : Formattable {
        override fun applyModifier(arg: Message): Formattable {
            return ArgsBuilder(value, args + arg)
        }

        override fun format(context: Context): Message {
            val size = args.size
            var i = 0
            while (i < size) {
                val test = args[i]
                if (i+1 < args.size) {
                    if (value == test.toString()) {
                        return args[i + 1]
                    }
                } else {
                    return test // default
                }
                i += 2
            }
            // no default supplied
            return Message.empty
        }
    }

    override fun applyModifier(arg: Message): Formattable {
        return ArgsBuilder(arg.toString(), emptyList())
    }

    override fun format(context: Context): Message {
        throw IllegalArgumentException("At least one argument is required")
    }
}

class Execute : PartialWrapper ({
    PartialRequired { command ->
        PartialContext { context ->
            if (context.elevated)
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.toString())
            Message.empty
        }
    }
})

class Equation : PartialWrapper ({

    PartialRequired { argEquation ->
        try {
            val answer = ExpressionBuilder(argEquation.toString())
                    .operator(greater)
                    .operator(lesser)
                    .operator(greaterEqual)
                    .operator(lesserEqual)
                    .operator(equal)
                    .operator(notEqual)
                    .operator(and)
                    .operator(or)
                    .operator(not)
                    .function(round)
                    .build()
                    .evaluate()
            Message.of(BigDecimal.valueOf(answer)
                    .stripTrailingZeros()
                    .toPlainString())
        } catch (e: Exception) {
            Message.error("{$argEquation}")
        }
    }
})
const val epsilon =  0.00000001

const val PRECEDENCE_NOT = Operator.PRECEDENCE_UNARY_MINUS
const val PRECEDENCE_AND = Operator.PRECEDENCE_ADDITION - 10
const val PRECEDENCE_OR = Operator.PRECEDENCE_ADDITION - 20
const val PRECEDENCE_COMPARE = Operator.PRECEDENCE_ADDITION - 100
const val PRECEDENCE_EQUAL = Operator.PRECEDENCE_ADDITION - 1000

private val not : Operator =
        object : Operator("!", 1, true, PRECEDENCE_NOT) {
            override fun apply(vararg args: Double): Double {
                return if ((args[0] > -epsilon) && (args[0] < epsilon)) {
                    1.0
                } else {
                    0.0
                }
            }
        }

private val or : Operator =
        object : Operator("$", 2, true, PRECEDENCE_OR) {
            override fun apply(vararg args: Double): Double {
                return if (((args[0] < -epsilon) || (args[0] > epsilon)) or ((args[1] < -epsilon) || (args[1] > epsilon))) {
                    1.0
                } else {
                    0.0
                }
            }
        }

private val and : Operator =
        object : Operator("&", 2, true, PRECEDENCE_AND) {
            override fun apply(vararg args: Double): Double {
                return if (((args[0] < -epsilon) || (args[0] > epsilon)) and ((args[1] < -epsilon) || (args[1] > epsilon))) {
                    1.0
                } else {
                    0.0
                }
            }
        }

private val greater: Operator =
        object : Operator(">", 2, true, PRECEDENCE_COMPARE) {
            override fun apply(vararg args: Double): Double {
                return if (args[0] > args[1] + epsilon) {
                    1.0
                } else {
                    0.0
                }
            }
        }
private val lesser: Operator =
        object : Operator("<", 2, true, PRECEDENCE_COMPARE) {
            override fun apply(vararg args: Double): Double {
                return if (args[0] + epsilon < args[1]) {
                    1.0
                } else {
                    0.0
                }
            }
        }
private val greaterEqual: Operator =
        object : Operator(">=", 2, true, PRECEDENCE_COMPARE) {
            override fun apply(vararg args: Double): Double {
                return if (args[0] + epsilon >= args[1]) {
                    1.0
                } else {
                    0.0
                }
            }
        }
private val lesserEqual: Operator =
        object : Operator("<=", 2, true, PRECEDENCE_COMPARE) {
            override fun apply(vararg args: Double): Double {
                return if (args[0] <= args[1] + epsilon) {
                    1.0
                } else {
                    0.0
                }
            }
        }

private val equal : Operator =
        object : Operator("==", 2, true, PRECEDENCE_EQUAL) {
            override fun apply(vararg args: Double): Double {
                return if ((args[0] > args[1] - epsilon) && (args[0] < args[1] + epsilon)) {
                    1.0
                } else {
                    0.0
                }
            }
        }

private val notEqual : Operator =
        object : Operator("!=", 2, true, PRECEDENCE_EQUAL) {
            override fun apply(vararg args: Double): Double {
                return if ((args[0] < args[1] - epsilon) || (args[0] > args[1] + epsilon)) {
                    1.0
                } else {
                    0.0
                }
            }
        }

private val round : Function =
        object : Function("round", 1) {
            override fun apply(vararg args: Double): Double {
                return args[0].roundToLong().toDouble()
            }
        }

class CustomPath : Formattable {

    private class ArgsBuilder(val function: String, val args: List<Message>) : Formattable {
        override fun applyModifier(arg: Message): Formattable {
            return ArgsBuilder(function, args + arg)
        }

        override fun format(context: Context): Message {
            val text = context.getFunction(function) ?: return Message.error("{#|$function}")

            val callContext = args.foldIndexed(context.newScope()) { i: Int, ctx, msg ->
                ctx.plus("arg$i", msg)
            }

            return Expander.expand(text, callContext)
        }
    }

    override fun applyModifier(arg: Message): Formattable {
        return ArgsBuilder(arg.toString(), emptyList())
    }

    override fun format(context: Context): Message {
        throw IllegalArgumentException("Second argument required")
    }
}

class RegexExpander : PartialWrapper ({

    PartialRequired { argText ->
        PartialRequired { argPattern ->
            PartialRequired { argReplace ->
                val text = argText.toString()
                val pattern = argPattern.toString()
                val replace = argReplace.toString()
                Message.of(text.replace(Regex(pattern), replace))
            }
        }
    }
})

class LengthExpander : PartialWrapper ({
    PartialRequired { arg ->
        Message.of(arg.toString().length)
    }
})

class SubstringExpander : PartialWrapper ({

    PartialRequired { argText ->
        PartialRequired { argStart ->
            PartialDefault(Message.of(Int.MAX_VALUE)) { argLength ->
                val text = argText.toString()
                var start = argStart.toString().toInt().let {
                    if (it < 0)
                        text.length - it.absoluteValue
                    else
                        it
                }
                val endString = argLength.toString()
                var end = endString.toInt().let {
                    if (it < 0)
                        text.length - it.absoluteValue
                    else
                        start + min(it, text.length) // min to prevent overflow on Int.MAX_VALUE
                }

                // clip if outside of bounds
                if (start < 0)
                    start = 0
                if (start > text.length)
                    start = text.length
                if (end < 0)
                    end = 0
                if (end > text.length)
                    end = text.length
                if (end < start) {
                    end = start
                }

                Message.of(text.substring(start, end))
            }
        }
    }
})

open class PadExpander(internal val startText: (String) -> String, internal val endText: (String) -> String) : PartialWrapper ({

    PartialRequired { argText ->
        PartialRequired { argLength ->
            PartialDefault(" ") { argPadText ->
                val length = argLength.toString().toInt()
                val padText = argPadText.toString().ifEmpty{ " " }

                val result = StringBuilder(startText(argText.toString()))
                val endString = endText(argText.toString())
                var padTextIndexIterator = padText.iterator()
                while (result.length < length - endString.length) {
                    if (!padTextIndexIterator.hasNext()) {
                        padTextIndexIterator = padText.iterator()
                    }
                    result.append(padTextIndexIterator.next())
                }
                result.append(endString)

                Message.of(result.toString())
            }
        }
    }
})

class PadLeftExpander : PadExpander({ "" }, { it })

class PadRightExpander : PadExpander({ it }, { "" })

class RandomExpander : PartialWrapper ({

    PartialDefault(Message.of(1)) { max ->
        PartialDefault(Message.of(0)) { min ->
            Message.of(Random.nextDouble(min.toString().toDouble(), max.toString().toDouble()).toString())
        }
    }
})