package io.github.mdsimmo.bomberman.messaging

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import net.objecthunter.exp4j.operator.Operator
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import java.lang.RuntimeException
import java.math.BigDecimal
import kotlin.math.roundToLong
import kotlin.random.Random


class StringWrapper(val text: String) : Formattable {

    override fun format(args: List<Message>, elevated: Boolean): Message {
        return Message.of(text)
    }

}

class ItemWrapper(private val item: ItemStack) : Formattable {

    override fun format(args: List<Message>, elevated: Boolean): Message {
        return if (args.isEmpty()) {
            Text.ITEM_FORMAT
                    .with("item", this)
                    .format()
        } else when (args[0].toString()) {
            "amount" -> Message.of(item.amount)
            "type" -> Message.of(item.type.key.toString())
            else -> Message.empty
        }
    }

}

class SenderWrapper(private val sender: CommandSender) : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        return when (args.getOrNull(0)?.toString()?.lowercase() ?: "name") {
            "name" -> Message.of(sender.name)
            "msg" -> {
                require(args.size >= 2) { "Sending a message requires a second argument" }
                args[1].sendTo(sender)
                Message.empty
            }
            "exec" -> {
                require(args.size >= 2) { "Exec requires a second argument" }
                val cmd = args[1].toString()
                Bukkit.getServer().dispatchCommand(sender, cmd)
                Message.empty
            }
            else -> {
                throw IllegalArgumentException("Unknown CommandSender option: ${args[0]}")
            }
        }
    }
}

class ColorWrapper(private val color: ChatColor) : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        require (args.size == 1) { "Colors format is {<color>|text}" }
        return args[0].color(color)
    }
}

class CollectionWrapper<T : Formattable>(private val list: Collection<T>) : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        return when (args.getOrNull(0)?.toString() ?: "length") {
            "foreach" -> {
                // Join all elements by applying arg[1] to each item separated by arg[2]
                val mapper = args.getOrNull(1)?.toString() ?: "({index}: {it})"
                val separator = args.getOrNull(2) ?: Message.of(" ")
                list
                        .mapIndexed {i, item ->
                            SimpleContext(mapper, elevated)
                                    .with("it", item)
                                    .with("index", Message.of(i))
                                    .format()
                        }
                        .ifEmpty { listOf(Message.empty) }
                        .reduce { a, b -> a.append(separator).append(b) }
            }
            "sort" -> {
                val mapper = args.getOrNull(1)?.toString()
                val sorted = if (mapper == null) {
                    list.sortedBy {
                        it.toString()
                    }
                } else {
                    list.withIndex().sortedBy {
                        SimpleContext(mapper,  elevated)
                                .with("it", it.value)
                                .with("index", Message.of(it.index))
                                .format()
                                .toString()
                    }.map { it.value }
                }
                return CollectionWrapper(sorted).format(args.drop(2), elevated)
            }
            "length" -> Message.of(list.size)
            "filter" -> {
                val filter = args.getOrNull(1)?.toString() ?: throw IllegalArgumentException("'filter' must have second argument")
                val filtered =
                    list.withIndex().filter {
                        SimpleContext(filter, elevated)
                            .with("it", it.value)
                            .with("index", Message.of(it.index))
                            .format()
                            .toString()
                            .isNotBlank()
                    }.map { it.value }
                return CollectionWrapper(filtered).format(args.drop(2), elevated)
            }
            "get" -> {
                val (value, criteria, results) = when (args.size) {
                    0, 1 ->  throw RuntimeException("Should be impossible")
                    2 -> Triple(args[1].toString(), "{it}","{it}")
                    3 -> Triple(args[1].toString(), "{it}", args[2].toString())
                    4 -> Triple(args[1].toString(), args[2].toString(), args[3].toString())
                    else -> throw IllegalArgumentException("'=' must have 1-3 arguments afterwards")
                }
                val match = list.withIndex().firstOrNull() {
                    val crit = SimpleContext(criteria, elevated)
                        .with("it", it.value)
                        .with("index", Message.of(it.index))
                        .format()
                        .toString()
                    crit == value
                }?.value
                return if (match == null) {
                    Message.empty
                } else {
                    SimpleContext(results, elevated)
                        .with("it", match)
                        .format()
                }
            }

            else -> throw IllegalArgumentException("Unknown list option: " + args[0])
        }
    }
}

class RawExpander : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        require(args.isEmpty()) { "Raw format is {#raw}" }
        return Message.rawFlag
    }
}

class TitleExpander : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        require(args.isNotEmpty()) { "Title format is {#title|title|subtitle=''|fadeIn=0|stay=20|fadeOut=0}" }
        val text = args[0]
        val subtitle = if (args.size >= 2) args[1] else Message.empty
        val fadein = if (args.size >= 3) args[2].toString().toInt() else 0
        val duration = if (args.size >= 4) args[3].toString().toInt() else 20
        val fadeout = if (args.size >= 5) args[4].toString().toInt() else 0
        return Message.title(text, subtitle, fadein, duration, fadeout)
    }
}

class Switch : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        val size = args.size
        val value = args[0].toString()
        var i = 1
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

class Execute : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        require(args.size == 1) { "Execute format is {#exec|command}" }
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), args[0].toString())
        return Message.empty
    }
}

class Equation : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        require (args.size == 1) { "Equation format is {#=|equation}" }
        return try {
            val answer = ExpressionBuilder(args[0].toString())
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
            Message.error("{${args[0]}}")
        }
    }
}
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
    override fun format(args: List<Message>, elevated: Boolean): Message {
        require(args.isNotEmpty()) { "Custom format is {#|path|args...}" }
        val text = Text.getSection(args[0].toString())
        return args.drop(1)
                .foldIndexed(text) { i, acc, iArg ->
                    acc.with("arg$i", iArg)
                }
                .format()
    }
}

class RegexExpander : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        require(args.size == 3) { "Regex format is {#regex|text|pattern|replace}" }
        val text = args[0].toString()
        val pattern = args[1].toString()
        val replace = args[2].toString()
        return Message.of(text.replace(Regex(pattern), replace))
    }
}

class LengthExpander : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        require(args.size == 1) { "Length format is {#len|text}" }
        return Message.of(args[0].toString().length)
    }
}

class SubstringExpander : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        require(args.size == 2 || args.size == 3) { "Substring format is {#sub|text|start|length}"}
        val text = args[0].toString()
        var start = args[1].toString().toInt().let {
            if (it < 0)
                text.length - -it
            else
                it
        }
        var end = args.getOrElse(2) { text.length }.toString().toInt().let {
            if (it < 0)
                text.length - -it
            else
                start + it
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

        return Message.of(text.substring(start, end))
    }
}

interface PadExpander : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        require(args.size >= 2) { "Pad format is {#pad|text|length|padtext=' '}"}
        val length = args[1].toString().toInt()
        val padText = args.getOrNull(2)?.toString()?.ifEmpty{ " " } ?: " "

        val result = StringBuilder(startText(args[0].toString()))
        val endText = endText(args[0].toString())
        var padTextIndexIterator = padText.iterator()
        while (result.length < length - endText.length) {
            if (!padTextIndexIterator.hasNext()) {
                padTextIndexIterator = padText.iterator()
            }
            result.append(padTextIndexIterator.next())
        }
        result.append(endText)

        return Message.of(result.toString())
    }

    fun startText(text: String): String

    fun endText(text: String): String
}

class PadLeftExpander : PadExpander{
    override fun startText(text: String) = ""
    override fun endText(text: String) = text
}

class PadRightExpander : PadExpander {
    override fun startText(text: String) = text
    override fun endText(text: String) = ""
}

class RandomExpander : Formattable {
    override fun format(args: List<Message>, elevated: Boolean): Message {
        return when (args.size) {
            0 -> Message.of(Random.nextDouble().toString())
            1 -> {
                return args[0].toString().toDoubleOrNull()?.let {  max ->
                    Message.of(Random.nextDouble(max).toString())
                } ?: Message.error("Number expected. Got '${args[0]}'")
            }
            2 -> {
                return args[0].toString().toDoubleOrNull()?.let {  min ->
                    args[1].toString().toDoubleOrNull()?.let { max ->
                        Message.of(Random.nextDouble(min, max).toString())
                    }
                } ?: Message.error("Number expected. Got '${args[0]}', '${args[1]}'")
            }
            else -> throw IllegalArgumentException("Rand can have 0-2 arguments")
        }
    }
}