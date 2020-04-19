package io.github.mdsimmo.bomberman.messaging

import net.objecthunter.exp4j.ExpressionBuilder
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import kotlin.text.StringBuilder

class StringWrapper(val text: String) : Formattable {

    override fun format(args: List<Message>): Message {
        return Message.of(text)
    }

}

class ItemWrapper(private val item: ItemStack) : Formattable {

    override fun format(args: List<Message>): Message {
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
    override fun format(args: List<Message>): Message {
        return Message.of(sender.name)
    }
}

class ColorWrapper(private val color: ChatColor) : Formattable {
    override fun format(args: List<Message>): Message {
        if (args.size != 1)
            throw RuntimeException("Colors must have exactly one argument. Given " + args.size)
        return args[0].color(color)
    }
}

class CollectionWrapper<T : Formattable>(private val list: Collection<T>) : Formattable {
    override fun format(args: List<Message>): Message {
        return when (args.getOrNull(0)?.toString() ?: "foreach") {
            "foreach" -> {
                // Join all elements by applying arg[1] to each item separated by arg[2]
                val mapper = args.getOrNull(1)?.toString() ?: "{value}"
                val separator = args.getOrNull(2) ?: Message.of(", ")
                list
                        .mapIndexed {i, item ->
                            Expander.expand(mapper, mapOf(
                                    Pair("value", item),
                                    Pair("i", Message.of(i))
                            ))
                        }
                        .ifEmpty { listOf(Message.empty) }
                        .reduce { a, b -> a.append(separator).append(b) }
            }
            "length" -> Message.of(list.size)
            else -> throw IllegalArgumentException("Unknown list option: " + args[0])
        }
    }
}

class RawExpander : Formattable {
    override fun format(args: List<Message>): Message {
        require(args.isEmpty()) { "{raw} cannot be used with arguments" }
        return Message.rawFlag
    }
}

class TitleExpander : Formattable {
    override fun format(args: List<Message>): Message {
        require(args.isNotEmpty()) { "{title} needs at least one argument" }
        val text = args[0]
        val subtitle = if (args.size >= 2) args[1] else Message.empty
        val fadein = if (args.size >= 3) args[2].toString().toInt() else 0
        val duration = if (args.size >= 4) args[3].toString().toInt() else 20
        val fadeout = if (args.size >= 5) args[4].toString().toInt() else 0
        return Message.title(text, subtitle, fadein, duration, fadeout)
    }
}

class Switch : Formattable {
    override fun format(args: List<Message>): Message {
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

class Equation : Formattable {
    override fun format(args: List<Message>): Message {
        require (args.size == 1) { "Equation must have exactly one argument" }
        return try {
            val answer = ExpressionBuilder(args[0].toString()).build().evaluate()
            Message.of(BigDecimal.valueOf(answer).stripTrailingZeros().toPlainString())
        } catch (e: Exception) {
            Message.error("{${args[0]}}")
        }
    }
}

class CustomPath : Formattable {
    override fun format(args: List<Message>): Message {
        require(args.isNotEmpty()) { "Custom message needs path" }
        val text = Text.getSection(args[0].toString())
        return args.drop(1)
                .foldIndexed(text) { i, acc, iArg ->
                    acc.with("arg$i", iArg)
                }
                .format()
    }
}

class RegexExpander : Formattable {
    override fun format(args: List<Message>): Message {
        require(args.size == 3) { "Regex format is {regex|text|pattern|replace}" }
        val text = args[0].toString()
        val pattern = args[1].toString()
        val replace = args[2].toString()
        println(pattern)
        return Message.of(text.replace(Regex(pattern), replace))
    }
}

class LengthExpander : Formattable {
    override fun format(args: List<Message>): Message {
        require(args.size == 1) { "Length format is {len|text}" }
        return Message.of(args[0].toString().length)
    }
}

class SubstringExpander : Formattable {
    override fun format(args: List<Message>): Message {
        require(args.size == 2 || args.size == 3) { "Substring format is {sub|text|start|length}"}
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
    override fun format(args: List<Message>): Message {
        require(args.size > 2) { "Pad format is {pad|text|length|padtext=' '}"}
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