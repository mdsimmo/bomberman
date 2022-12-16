package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.messaging.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

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
        path += name().toString()
        return path
    }

    fun cmdContext(): Context {
        return Context(false).plus("command", this)
    }

    override fun format(args: List<Message>, context: Context): Message {
        return when (args.getOrElse(0) { "name" }.toString()) {
            "name" -> name().format(emptyList(), context.plus(cmdContext()))
            "path" -> Message.of(path())
            "usage" -> usage().format(emptyList(), context.plus(cmdContext()))
            "extra" -> extra().format(emptyList(), context.plus(cmdContext()))
            "example" -> example().format(emptyList(), context.plus(cmdContext()))
            "description" -> description().format(emptyList(), context.plus(cmdContext()))
            "flags" -> CollectionWrapper(flags(Bukkit.getConsoleSender())
                    .map { flag -> object: Formattable {
                        override fun format(args: List<Message>, context: Context): Message {
                            return when ((args.firstOrNull() ?: "name").toString()) {
                                "name" -> Message.of(flag)
                                "ext" -> flagExtension(flag).format(emptyList(), context.plus(cmdContext()))
                                "description" -> flagDescription(flag).format(emptyList(), context.plus(cmdContext()))
                                else -> throw RuntimeException("Unknown flag value '" + args[0] + "'")
                            }
                        }
                    } }).format(args.drop(1), context)
            else -> throw RuntimeException("Unknown command value '" + args[0] + "'")
        }
    }

}