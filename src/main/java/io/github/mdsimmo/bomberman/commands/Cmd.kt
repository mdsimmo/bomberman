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
    abstract fun name(): Message

    /**
     * Gets a list of possible values to return.
     *
     * @param args the current arguments typed
     * @return the options
     */
    abstract fun options(sender: CommandSender, args: List<String>): List<String>

    open fun flags(sender: CommandSender, args: List<String>, flags: Map<String, String>): Set<String> = emptySet()

    open fun flagOptions(sender: CommandSender, flag: String, args: List<String>, flags: Map<String, String>): Set<String> = emptySet()

    /**
     * Execute the command
     *
     * @param sender the sender
     * @param args   the arguments
     * @return true if correctly typed. False will display info
     */
    abstract fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean

    /**
     * Execute the command. Checks for permissions
     * @return true if success. false to show usage
     */
    fun execute(sender: CommandSender, args: List<String>, flags: Map<String, String>) {
        if (permission().isAllowedBy(sender)) {
            if (!run(sender, args, flags)) {
                incorrectUsage(sender, args)
                help(sender, args, flags)
            }
        } else {
            context(Text.DENY_PERMISSION).sendTo(sender)
        }
    }

    /**
     * Sends help to the sender
     *
     * @param sender the player to help
     */
    open fun help(sender: CommandSender, args: List<String>, flags: Map<String, String>) {
        context(Text.COMMAND_HELP).sendTo(sender)
    }

    abstract fun extra(): Message
    abstract fun example(): Message
    abstract fun description(): Message
    abstract fun usage(): Message

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

    fun incorrectUsage(sender: CommandSender, args: List<String>) {
        context(Text.INCORRECT_USAGE)
                .with("attempt", CollectionWrapper<Message>(
                        args.map { Message.of(it) }
                ))
                .sendTo(sender)
    }

    fun context(text: Contexted): Contexted {
        return text.with("command", this)
    }

    override fun format(args: List<Message>): Message {
        return when (args.getOrElse(0) { "name" }.toString()) {
            "name" -> name()
            "path" -> Message.of(path())
            "usage" -> usage()
            "extra" -> extra()
            "example" -> example()
            "description" -> description()
            "flags" -> CollectionWrapper(flags(Bukkit.getConsoleSender(), listOf(), mapOf())
                    .map { flag -> object: Formattable {
                        override fun format(args: List<Message>): Message {
                            return when ((args.firstOrNull() ?: "name").toString()) {
                                "name" -> Message.of(flag)
                                "ext" -> flagExtension(flag)
                                "description" -> flagDescription(flag)
                                else -> throw RuntimeException("Unknown flag value '" + args[0] + "'")
                            }
                        }
                    } }).format(args.drop(1))
            else -> throw RuntimeException("Unknown command value '" + args[0] + "'")
        }
    }

    open fun flagExtension(flag: String): Message {
        return Message.empty
    }

    open fun flagDescription(flag: String): Message {
        return Message.empty
    }

}