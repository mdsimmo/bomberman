package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.messaging.*
import org.bukkit.command.CommandSender

// TODO Cmd shouldn't require knowing its parent
abstract class Cmd(protected var parent: Cmd?) : Formattable {
    enum class Permission(val permission: String) {
        PLAYER("bomberman.player"),
        GAME_OPERATE("bomberman.operator"),
        GAME_DICTATE("bomberman.dictator");

        fun isAllowedBy(sender: CommandSender): Boolean {
            return sender.hasPermission(permission)
        }

    }

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
        if (isAllowedBy(sender)) {
            if (!run(sender, args, flags)) {
                incorrectUsage(sender, args)
                help(sender)
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
    open fun help(sender: CommandSender) {
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
     * gets if the given sender has permission to run this command
     *
     * @param sender the sender
     * @return true if they have permission
     */
    fun isAllowedBy(sender: CommandSender): Boolean {
        return permission().isAllowedBy(sender)
    }

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
            else -> throw RuntimeException("Unknown value " + args[0])
        }
    }

}