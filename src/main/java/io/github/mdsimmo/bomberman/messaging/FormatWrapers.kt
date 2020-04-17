package io.github.mdsimmo.bomberman.messaging

import net.objecthunter.exp4j.ExpressionBuilder
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

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
            else -> Message.empty()
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
                        .ifEmpty { listOf(Message.empty()) }
                        .reduce { a, b -> a.append(separator).append(b) }
            }
            "length" -> Message.of(list.size)
            else -> throw IllegalArgumentException("Unknown list option: " + args[0])
        }
    }
}

class MapExpander : Formattable {
    override fun format(args: List<Message>): Message {
        val size = args.size
        require (size % 2 == 0) { "map needs an even amount of arguments" }
        var text: Message = Message.empty()
        var i = 0
        while (i < size) {
            val row = Text.MAP_FORMAT
                    .with("key", args[i])
                    .with("value", args[i + 1])
                    .format()
            text = text.append(row)
            i += 2
        }
        return text
    }
}

class ListExpander : Formattable {
    override fun format(args: List<Message>): Message {
        var text: Message = Message.empty()
        for (arg in args) {
            val row = Text.LIST_FORMAT
                    .with("value", arg)
                    .format()
            text = text.append(row)
        }
        return text
    }
}

class HeadingExpander : Formattable {
    override fun format(args: List<Message>): Message {
        require(args.size == 1) { "Header must have one argument" }
        return Text.HEADING_FORMAT
                .with("value", args[0])
                .format()
    }
}

class RawExpander : Formattable {
    override fun format(args: List<Message>): Message {
        require(args.isEmpty()) { "{raw} cannot be used with arguments" }
        return Message.rawFlag()
    }
}

class TitleExpander : Formattable {
    override fun format(args: List<Message>): Message {
        require(args.isNotEmpty()) { "{title} needs at least one argument" }
        val text = args[0]
        val subtitle = if (args.size >= 2) args[1] else Message.empty()
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
                if (equal(value, test.toString())) {
                    return args[i + 1]
                }
            } else {
                return test // default
            }
            i += 2
        }
        // no default supplied
        return Message.empty()
    }

    private fun equal(start: String, arg: String): Boolean {
        val parts = arg.split(",").toTypedArray()
        for (part in parts) {
            if (part.trim { it <= ' ' }.equals(start, ignoreCase = true)) return true
        }
        return false
    }
}

class Equation : Formattable {
    override fun format(args: List<Message>): Message {
        require (args.size == 1) { "Equation must have exactly one argument" }
        return try {
            val answer = ExpressionBuilder(args[0].toString()).build().evaluate()
            Message.of(BigDecimal.valueOf(answer).stripTrailingZeros().toPlainString())
        } catch (e: Exception) {
            throw RuntimeException("Expression has invalid numerical inputs: " + args[0], e)
        }
    }
}