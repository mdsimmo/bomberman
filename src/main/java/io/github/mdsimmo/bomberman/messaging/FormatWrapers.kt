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

class SenderWrapper(private val sender: CommandSender) : Formattable by (
        DefaultArg("name") { arg ->
            when (arg.toString().lowercase()) {
                "name" -> Message.of(sender.name)
                "msg" -> {
                    RequiredArg { arg2 ->
                        arg2.sendTo(sender)
                        Message.empty
                    }
                }

                "exec" -> {
                    RequiredArg { arg2 ->
                        ContextArg { context ->
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
        })


class ColorWrapper(private val color: ChatColor) : Formattable by (
    RequiredArg { arg ->
        arg.color(color)
    })

class CollectionWrapper<T : Formattable>(private val list: Collection<T>) : Formattable by (

    DefaultArg("length") { argProperty: Message ->
        when (argProperty.toString().lowercase()) {
            "length" -> Message.of(list.size)
            "foreach" -> {
                DefaultArg("({index}: {it})") { argMapper ->
                    DefaultArg(" ") { argSeparator ->
                        ContextArg { context ->
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
                DefaultArg("{it}") { argMapper ->
                    ContextArg { context ->
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
                RequiredArg { argFilter ->
                    ContextArg { context ->
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
    })

class TitleExpander : Formattable by (
    RequiredArg { title ->
        DefaultArg(Message.empty) { subtitle ->
            DefaultArg(Message.of(0)) { argFadeIn ->
                DefaultArg(Message.of(20)) { argDuration ->
                    DefaultArg(Message.of(0)) { argFadeOut ->
                        val fadeIn = argFadeIn.toString().toInt()
                        val duration = argDuration.toString().toInt()
                        val fadeOut = argFadeOut.toString().toInt()
                        Message.title(title, subtitle, fadeIn, duration, fadeOut)
                    }
                }
            }
        }
    })

class Switch : Formattable by (

    RequiredArg { valueArg ->
        AdditionalArgs { args ->
            val value = valueArg.toString()
            val size = args.size
            var i = 0
            while (i < size) {
                val test = args[i]
                if (i+1 < args.size) {
                    if (value == test.toString()) {
                        return@AdditionalArgs args[i + 1]
                    }
                } else {
                    return@AdditionalArgs test // default
                }
                i += 2
            }
            // no default supplied
            Message.empty
        }
    })

class Execute : Formattable by (
    RequiredArg { command ->
        ContextArg { context ->
            if (context.elevated)
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.toString())
            Message.empty
        }
    })

class Equation : Formattable by (

    RequiredArg { argEquation ->
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

class CustomPath : Formattable by (

    RequiredArg { functionArg ->
        AdditionalArgs { args ->
            ContextArg { context ->
                val function = functionArg.toString()
                val text = context.getFunction(function)
                    ?: return@ContextArg Message.error("{#|$function}")

                val callContext = args.foldIndexed(context.newScope()) { i: Int, ctx, msg ->
                    ctx.plus("arg$i", msg)
                }

                Expander.expand(text, callContext)
            }
        }
    })

class RegexExpander : Formattable by (

    RequiredArg { argText ->
        RequiredArg { argPattern ->
            RequiredArg { argReplace ->
                val text = argText.toString()
                val pattern = argPattern.toString()
                val replace = argReplace.toString()
                Message.of(text.replace(Regex(pattern), replace))
            }
        }
    })

class LengthExpander : Formattable by (
    RequiredArg { arg ->
        Message.of(arg.toString().length)
    })

class SubstringExpander : Formattable by (

    RequiredArg { argText ->
        RequiredArg { argStart ->
            DefaultArg(Message.of(Int.MAX_VALUE)) { argLength ->
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
    })

open class PadExpander(internal val startText: (String) -> String, internal val endText: (String) -> String) : Formattable by (

    RequiredArg { argText ->
        RequiredArg { argLength ->
            DefaultArg(" ") { argPadText ->
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
    })

class PadLeftExpander : PadExpander({ "" }, { it })

class PadRightExpander : PadExpander({ it }, { "" })

class RandomExpander : Formattable by (

    DefaultArg(Message.of(1)) { max ->
        DefaultArg(Message.of(0)) { min ->
            Message.of(Random.nextDouble(min.toString().toDouble(), max.toString().toDouble()).toString())
        }
    })