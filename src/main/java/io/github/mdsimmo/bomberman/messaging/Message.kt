package io.github.mdsimmo.bomberman.messaging

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.IllegalArgumentException
import java.util.*
import javax.annotation.CheckReturnValue

/**
 * A message is an immutable coloured/formatted expanded string
 */
@CheckReturnValue
class Message private constructor(private val contents: TreeNode) : Formattable {

    // TODO the internals of this class are an utter mess

    companion object {
        fun of(text: String): Message {
            return Message(StringNode(text))
        }

        fun of(num: Number): Message {
            return of(num.toString())
        }

        val empty = of("")

        fun title(text: Message, subtitle: Message, fadeIn: Int, duration: Int, fadeOut: Int): Message {
            return Message(TitleNode(text, subtitle, fadeIn, duration, fadeOut))
        }

        val rawFlag = Message(RawNode())

        fun error(s: String): Message {
            return of(s).color(ChatColor.RED)
        }

        fun lazyExpand(text: String, context: Context): Message {
            return Message(LazyNode(text, context))
        }

        fun reference(item: Formattable, context: Context): Message {
            return Message(ReferenceNode(item, context))
        }
    }

    private data class Style constructor(
            val color: ChatColor,
            val formats: Set<ChatColor>
    )

    private class Cursor {
        val content = StringBuilder()
        val colorStack: Deque<Style> = ArrayDeque<Style>().apply {
            add(Style(ChatColor.RESET, setOf()))
        }

        fun addStyle(color: ChatColor) { // Get what the current style is (should always be one on the stack)
            val currentStyle = colorStack.last
            // Determine what the color change will make the new style be
            val newStyle = when {
                color.isColor -> Style(color, currentStyle.formats)
                color.isFormat -> {
                    // LinkedHashSet is used here to ensure consistent order for unit tests
                    val newFormats = LinkedHashSet(currentStyle.formats)
                    newFormats.add(color)
                    Style(currentStyle.color, newFormats)
                }
                else -> /* color == RESET */ {
                    Style(ChatColor.RESET, setOf())
                }
            }
            // Change to the new style
            colorStack.addLast(newStyle)
            appendConversionString(currentStyle, newStyle)
        }

        private fun appendConversionString(from: Style, to: Style) { // Minecraft ChatColor rules:
            //  * Adding a color/reset format will remove all previous formats
            //  * Adding a format will keep existing formats and colors
            // LinkedHashSet is used here to ensure consistent order for unit tests
            val formatsRemoved = LinkedHashSet(from.formats)
            formatsRemoved.removeAll(to.formats)

            val formatsAdded = LinkedHashSet(to.formats)
            formatsAdded.removeAll(from.formats)

            if (from.color != to.color || formatsRemoved.isNotEmpty()) {
                // A complete reapplication is needed
                content.append(to.color)
                to.formats.forEach { obj ->
                    content.append(obj)
                }
            } else if (formatsAdded.isNotEmpty()) {
                // Can just apply the new formats directly
                formatsAdded.forEach { obj ->
                    content.append(obj)
                }
            }
        }

        fun write(text: String) {
            content.append(text)
        }

        fun popColor() {
            val from = colorStack.removeLast()
            val to = colorStack.last
            appendConversionString(from, to)
        }

        override fun toString(): String {
            return content.toString()
        }
    }

    private data class Title(
            val title: String,
            val subtitle: String,
            val fadeIn: Int,
            val stay: Int,
            val fadeOut: Int
    )

    private interface TreeNode {
        fun expand(cursor: Cursor)
        // TODO handling of {raw} and {title} is very messy
        val isRaw: Boolean

        fun expandTitle(): Title?

        fun applyModifier(arg: Message): Formattable?
    }

    private class StringNode(val text: String) : TreeNode {
        override fun expand(cursor: Cursor) {
            cursor.write(text)
        }

        override val isRaw: Boolean
            get() = false

        override fun expandTitle(): Title? {
            return null
        }

        override fun applyModifier(arg: Message): Formattable? {
            return null
        }

    }

    private class Joined private constructor(parts: List<TreeNode>) : TreeNode {
        val parts: List<TreeNode>

        constructor(a: TreeNode, b: TreeNode) : this(listOf(a, b))

        init {
            this.parts = ArrayList()
            // Flatten multiple joined nodes
            for (part in parts) {
                if (part is Joined) {
                    this.parts.addAll(part.parts)
                } else if (part !== empty.contents) {
                    this.parts.add(part)
                }
            }
        }

        override fun expand(cursor: Cursor) {
            for (part in parts) {
                part.expand(cursor)
            }
        }

        override val isRaw: Boolean
            get() = parts.any { it.isRaw }

        override fun expandTitle(): Title? {
            return parts.firstNotNullOfOrNull { it.expandTitle() }
        }

        override fun applyModifier(arg: Message): Formattable? {
            return parts.firstNotNullOfOrNull { it.applyModifier(arg) }
        }

    }

    private class Colored(val content: TreeNode, val color: ChatColor) : TreeNode {
        override fun expand(cursor: Cursor) {
            cursor.addStyle(color)
            content.expand(cursor)
            cursor.popColor()
        }

        override val isRaw: Boolean
            get() = content.isRaw

        override fun expandTitle(): Title? {
            return null
        }

        override fun applyModifier(arg: Message): Formattable? {
            return null
        }
    }

    // TODO Raw/Title formatting is really strange
    private class RawNode : TreeNode {
        override fun expand(cursor: Cursor) {}
        override val isRaw: Boolean
            get() = true

        override fun expandTitle(): Title? {
            return null
        }

        override fun applyModifier(arg: Message): Formattable? {
            return null
        }
    }

    private class TitleNode (
            private val title: Message,
            private val subtitle: Message,
            private val fadeIn: Int,
            private val stay: Int,
            private val fadeOut: Int)
        : TreeNode {

        override fun expand(cursor: Cursor) {}

        override val isRaw: Boolean
            get() = false

        override fun expandTitle(): Title {
            val titleCursor = Cursor()
            title.contents.expand(titleCursor)
            val titleString = titleCursor.toString()
            val subtitleCursor = Cursor()
            subtitle.contents.expand(subtitleCursor)
            val subtitleString = subtitleCursor.toString()
            return Title(titleString, subtitleString, fadeIn, stay, fadeOut)
        }

        override fun applyModifier(arg: Message): Formattable? {
            return null
        }
    }

    private class LazyNode (val text: String, val context: Context) : TreeNode {
        val content: TreeNode by lazy {
            Expander.expand(text, context).contents
        }

        override fun expand(cursor: Cursor) {
            content.expand(cursor)
        }

        override val isRaw: Boolean
            get() = content.isRaw

        override fun expandTitle(): Title? {
            return content.expandTitle()
        }

        override fun applyModifier(arg: Message): Formattable? {
            return content.applyModifier(arg)
        }
    }

    private class ReferenceNode (val item: Formattable, context: Context) : TreeNode {
        val content: TreeNode by lazy {
            item.format(context).contents
        }

        override fun expand(cursor: Cursor) {
            content.expand(cursor)
        }

        override val isRaw: Boolean
            get() = content.isRaw

        override fun expandTitle(): Title? {
            return content.expandTitle()
        }

        override fun applyModifier(arg: Message): Formattable {
            return item.applyModifier(arg)
        }
    }

    fun color(color: ChatColor): Message {
        return Message(Colored(contents, color))
    }

    fun append(text: Message): Message {
        return Message(Joined(contents, text.contents))
    }

    override fun format(context: Context): Message {
        return this
    }

    override fun applyModifier(arg: Message): Formattable {
        return contents.applyModifier(arg) ?: throw IllegalArgumentException("Cannot accept extra arguments")
    }

    fun sendTo(sender: CommandSender) {
        try {
            val sendContents: TreeNode = if (contents.isRaw) {
                contents
            } else {
                // Apply the wrapper format, but not to empty values
                Switch()
                    .applyModifier(this)
                    .applyModifier(empty)
                    .applyModifier(empty)
                    .applyModifier(Text.MESSAGE_FORMAT
                        .format(Context(false).plus("message", this))
                    )
                    .format(Context(false))
                    .contents
            }
            val cursor = Cursor()
            sendContents.expand(cursor)
            if (cursor.toString().isNotBlank()) {
                // filter out empty lines
                val text = cursor.toString()
                    .split('\n', '\r')
                    .filter { it.isNotBlank() }
                    .reduce{a, b -> a + "\n" + b}
                sender.sendMessage(text)
            }
            // Handle possible title
            if (sender is Player) {
                contents.expandTitle()?.also { title ->
                    sender.sendTitle(title.title, title.subtitle, title.fadeIn, title.stay, title.fadeOut)
                }
            }
        } catch (e: RuntimeException) {
            sender.sendMessage(ChatColor.RED.toString() + "Message format invalid")
        }
    }

    override fun toString(): String {
        val cursor = Cursor()
        contents.expand(cursor)
        return cursor.toString()
    }

}