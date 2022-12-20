package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.messaging.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import kotlin.IllegalArgumentException

// TODO Cmd shouldn't require knowing its parent
abstract class Cmd(protected var parent: Cmd?) : Formattable {

    /**
     * Gets the commands name.
     * This should not include the path (eg, "fare" instead of "bm.game.set.fare")
     * Do not put any spaces
     *
     * @return the name
     */
    abstract fun name(): Formattable

    /**
     * Gets a list of possible values to return.
     *
     * @param args the current arguments typed
     * @return the options
     */
    abstract fun options(sender: CommandSender, args: List<String>): List<String>

    open fun flags(sender: CommandSender): Set<String> = emptySet()

    open fun flagOptions(flag: String): Set<String> = emptySet()

    open fun flagExtension(flag: String): Formattable  = Message.empty

    open fun flagDescription(flag: String): Formattable = Message.empty

    /**
     * Execute the command
     *
     * @param sender the sender
     * @param args   the arguments
     * @return true if correctly typed. False will display info
     */
    abstract fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean

    abstract fun extra(): Formattable
    abstract fun example(): Formattable
    abstract fun description(): Formattable
    abstract fun usage(): Formattable

    /**
     * @return the permission needed to run this command
     */
    abstract fun permission(): Permission

    /**
     * gets the path to the command
     *
     * @param separator what to separate parent/child commands by
     * @return the path
     */
    private fun path(separator: String = " "): String {
        var path = ""
        parent?.also {
            path += it.path(separator) + separator
        }
        path += name().format(Context())
        return path
    }

    fun cmdContext(): Context {
        return Context(false).plus("command", this)
    }

    override fun format(context: Context): Message {
        return applyModifier(Message.of("name")).format(context)
    }

    override fun applyModifier(arg: Message): Formattable {
        return when (arg.toString().lowercase()) {
            "name" -> name()
            "path" -> Message.of(path())
            "usage" -> usage()
            "extra" -> extra()
            "example" -> example()
            "description" -> description()
            "flags" -> CollectionWrapper(flags(Bukkit.getConsoleSender())
                    .map { flag -> object: Formattable {
                        override fun applyModifier(arg: Message): Formattable {
                            return when (arg.toString().lowercase()) {
                                "name" -> Message.of(flag)
                                "ext" -> flagExtension(flag)
                                "description" -> flagDescription(flag)
                                "permission" -> Message.of(permission().value())
                                else -> throw IllegalArgumentException("Unknown flag value '$arg'`")
                            }
                        }

                        override fun format(context: Context): Message {
                            return applyModifier(Message.of("name")).format(context)
                        }
                    } })
            else -> throw IllegalArgumentException("Unknown command value '$arg'")
        }
    }

}