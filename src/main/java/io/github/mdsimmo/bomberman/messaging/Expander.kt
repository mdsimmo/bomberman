package io.github.mdsimmo.bomberman.messaging

import org.bukkit.ChatColor
import java.util.concurrent.atomic.AtomicInteger

object Expander {
    private val functions = mutableMapOf(
            Pair("=", Equation()),
            Pair("#switch", Switch()),
            Pair("#title", TitleExpander()),
            Pair("#raw", RawExpander()),
            Pair("#", CustomPath()),
            Pair("#regex", RegexExpander()),
            Pair("#len", LengthExpander()),
            Pair("#sub", SubstringExpander()),
            Pair("#padl", PadLeftExpander()),
            Pair("#padr", PadRightExpander())
    ).also {
        for (color in ChatColor.values()) {
            it["#${color.name.toLowerCase()}"] = ColorWrapper(color)
        }
    }.toMap()

    /**
     * Expands all braces in text. The number of open braces must be balanced with close braces
     * @param text the text to expand
     * @param things Reference-able things
     * @return the expanded text
     */
    @JvmStatic
    fun expand(text: String, things: Map<String, Formattable>): Message {
        var expanded: Message = Message.empty
        val building = StringBuilder()
        var ignoreNextSpecial = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                c == '{' && !ignoreNextSpecial -> {
                    // Add the basic text we have
                    expanded = expanded.append(Message.of(building.toString()))
                    building.setLength(0)
                    // Expand and add the brace
                    val subtext =
                            toNext(text, '}', i)
                                    ?: throw IllegalArgumentException("Braces unmatched: '$text'")
                    val expandedBrace = expandBrace(subtext, things)
                    expanded = expanded.append(expandedBrace)
                    i += subtext.length - 1 // -1 because starting brace was already counted
                }
                c == '\\' && !ignoreNextSpecial -> {
                    ignoreNextSpecial = true
                }
                else -> {
                    building.append(c)
                    ignoreNextSpecial = false
                }
            }
            i++
        }
        // Add anything remaining
        expanded = expanded.append(Message.of(building.toString()))
        return expanded
    }

    /**
     * Expands a brace in a message.
     * Each sub brace will be expanded with the same arguments as this message
     * @param text the text to expands formatted as "{ key | arg1 | ... | argN }"
     * @return the expanded string
     */
    private fun expandBrace(text: String, things: Map<String, Formattable>): Message {
        if (text[0] != '{' || text[text.length - 1] != '}')
            throw RuntimeException("expandBrace() must start and end with a brace")

        // Find the brace key
        var keyString = toNext(text, '|', 1) ?: throw RuntimeException("Text bad: '$text'")

        // Get the arguments for the key (separated by '|')
        val i = AtomicInteger(keyString.length)
        val args = mutableListOf<Message>()
        generateSequence { toNext(text, '|', i.get()) }
                .forEach { subArg ->
                    args.add(Message.lazyExpand(subArg.substring(1, subArg.length - 1), things))
                    // -1 because the first '|' would get counted twice
                    i.addAndGet(subArg.length - 1)
                }
        // remove the '|' and any whitespace from key
        keyString = keyString
                .substring(0, keyString.length - 1)
                .trim()
                .toLowerCase()

        // Try and look up a key, function or chat color to format with
        val thing = things[keyString]
                ?: functions[keyString]
                ?: Message.error(text)
        return thing.format(args)
    }

    /**
     * Gets the substring of sequence from index to the next endingChar but takes into account brace skipping.
     * The returned string will include both the start and end characters. If a closing brace
     * is found before the wanted character, then the remaining to that brace is returned. If the end of sequece is
     * reached, then empty is returned
     */
    private fun toNext(sequence: String, endingChar: Char, startIndex: Int): String? {
        val size = sequence.length
        var openBracesFound = 0
        var ignoreNextSpecial = false
        for (i in startIndex + 1 until size) {
            if (ignoreNextSpecial) {
                ignoreNextSpecial = false
            } else {
                val c = sequence[i]
                when {
                    c == endingChar && openBracesFound == 0 ->
                        return sequence.substring(startIndex, i + 1)
                    c == '{' ->
                        openBracesFound++
                    c == '}' -> {
                        openBracesFound--
                        if (openBracesFound < 0)
                            return sequence.substring(startIndex, i + 1)
                    }
                    c == '\\' ->
                        ignoreNextSpecial = true
                }
            }
        }
        return null
    }
}