package io.github.mdsimmo.bomberman.messaging

import org.bukkit.ChatColor
import java.util.concurrent.atomic.AtomicInteger

object Expander {
    private val functions = mutableMapOf(
            Pair("=", Equation()),
            Pair("#switch", Switch()),
            Pair("#title", TitleExpander()),
            Pair("#raw", Message.rawFlag),
            Pair("#", CustomPath()),
            Pair("#regex", RegexExpander()),
            Pair("#len", LengthExpander()),
            Pair("#sub", SubstringExpander()),
            Pair("#padl", PadLeftExpander()),
            Pair("#padr", PadRightExpander()),
            Pair("#exec", Execute()), // TODO remove me
            Pair("#rand", RandomExpander())
    ).also {
        for (color in ChatColor.values()) {
            it["#${color.name.lowercase()}"] = ColorWrapper(color)
        }
    }.toMap()

    /**
     * Expands all braces in text. The number of open braces must be balanced with close braces
     * @param text the text to expand
     * @param context Reference-able things
     * @return the expanded text
     */
    @JvmStatic
    fun expand(text: String, context: Context): Message {
        var expanded: Message = Message.empty
        val building = StringBuilder()
        var ignoreNextSpecial = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                c == '{' && !ignoreNextSpecial -> {
                    // Add the basic text we have
                    if (building.isNotEmpty()) {
                        expanded = expanded.append(Message.of(building.toString()))
                        building.setLength(0)
                    }

                    // Get raw text inside brace
                    val subtext = toNext(text, '}', i)
                                    ?: throw IllegalArgumentException("Braces unmatched: '$text'")
                    val expandedBrace = if ( subtext.startsWith("{!") ) {
                        // Message not to be expanded - just remove the exclaim
                        Message.of(subtext.replaceFirst("!", ""))
                    } else {
                        // Expand the brace
                        expandBrace(subtext, context)
                    }
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
        if (building.isNotEmpty()) {
            expanded = expanded.append(Message.of(building.toString()))
        }
        return expanded
    }

    /**
     * Expands a brace in a message.
     * Each sub brace will be expanded with the same arguments as this message
     * @param text the text to expands formatted as "{ key | arg1 | ... | argN }"
     * @return the expanded string
     */
    private fun expandBrace(text: String, context: Context): Message {
        if (text[0] != '{' || text[text.length - 1] != '}')
            throw RuntimeException("expandBrace() must start and end with a brace")

        // Find the brace key
        var keyString = toNext(text, '|', 1) ?: throw RuntimeException("Text bad: '$text'")

        // Get the arguments for the key (separated by '|')
        val i = AtomicInteger(keyString.length)
        val args = mutableListOf<Message>()
        generateSequence { toNext(text, '|', i.get()) }
                .forEach { subArg ->
                    args.add(Message.lazyExpand(subArg.substring(1, subArg.length - 1), context))
                    // -1 because the first '|' would get counted twice
                    i.addAndGet(subArg.length - 1)
                }
        // remove the '|' and any whitespace from key
        keyString = keyString
                .substring(0, keyString.length - 1)
                .trim()
                .lowercase()

        // TODO remove {#exec|...}
        if (keyString == "#exec" && !context.elevated) {
            return Message.empty
        }

        // Check if item is a reference request
        val reference = keyString.startsWith("@")
        if (reference) {
            keyString = keyString.substring(1, keyString.length) // remove the "@"
        }

        // Try and look up an object or function to format with
        val thing = context[keyString]
                ?: functions[keyString]
                ?: return Message.error(text)

        val modified = args.fold(thing) { partial, nextArg ->
            val result = partial.applyModifier(nextArg)
            result
        }

        return if (reference) {
            Message.reference(modified, context)
        } else {
            return modified.format(context)
        }
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