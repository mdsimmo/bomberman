package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.commands.game.*
import io.github.mdsimmo.bomberman.messaging.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.util.StringUtil
import java.util.ArrayList

class BaseCommand : Cmd, TabCompleter, CommandExecutor {

    companion object {
        private const val F_HELP = "?"
    }

    private val children: MutableList<Cmd> = ArrayList()

    /**
     * Adds some child commands. Should be called from the constructor of every implementation
     *
     * @param children the child commands
     */
    fun addChildren(vararg children: Cmd) {
        this.children.addAll(listOf(*children))
    }

    constructor() : this(true)

    // For testing, may not want children
    constructor(addChildren: Boolean) : super(null) {
        if (addChildren) {
            addChildren(
                // DevInfo(this),
                Configure(this),
                GameCreate(this),
                GameInfo(this),
                GameJoin(this),
                GameLeave(this),
                GameDelete(this),
                RunStart(this),
                RunStop(this),
                GameList(this),
                GameReload(this),
                UndoBuild(this)
            )
        }
    }

    override fun name(): Formattable {
        return Message.of("bomberman")
    }

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        val (arguments, flags) = separateFlags(args)
        run(sender, arguments, flags)
        return true
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
        if (args.isEmpty()) {
            Text.COMMAND_GROUP_HELP.format(cmdContext()).sendTo(sender)
            return true
        }

        // Find the referenced command
        val child = children.firstOrNull { c -> c.name().format(emptyList(), c.cmdContext()).toString().equals(args[0], ignoreCase = true) }
        if (child == null) {
            Text.UNKNOWN_COMMAND.format(cmdContext()
                .plus("attempt", args[0]))
                .sendTo(sender)
            return true
        }

        // Check for permissions
        if (!child.permission().isAllowedBy(sender)) {
            Text.DENY_PERMISSION.format(child.cmdContext()).sendTo(sender)
            return true
        }

        // Send help if requested
        if (flags.containsKey(F_HELP)) {
            Text.COMMAND_HELP.format(child.cmdContext()).sendTo(sender)
            return true
        }

        // Execute the command
        val result = child.run(sender, args.drop(1), flags)

        // Show help if usage was incorrect
        if (!result) {
            Text.INCORRECT_USAGE.format(child.cmdContext()
                .plus("attempt", CollectionWrapper(
                    args.drop(1).map { Message.of(it) }
                )))
                .sendTo(sender)
        }

        return result
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        val (arguments, _) = separateFlags(args)
        val currentlyTyping = args.last() // Will always have one.

        // Find referenced child command
        val cmd = children.firstOrNull { c -> c.name().format(emptyList(), c.cmdContext()).toString().equals(arguments.firstOrNull(), ignoreCase = true) } ?: this
        if (!cmd.permission().isAllowedBy(sender))
            return emptyList()

        val allOptions = if (currentlyTyping.startsWith("-")) {
            val splitIndex = currentlyTyping.indexOf('=')
            if (splitIndex == -1) {
                cmd.flags(sender)
                    .map { "-$it" }
                    .plus("-$F_HELP")
            } else {
                val key = currentlyTyping.substring(1, splitIndex)
                cmd.flagOptions(key)
                    .map { "-$key=$it" }
            }
        } else {
            cmd.options(sender, arguments.drop(1))
        }
        return allOptions.filter {
            StringUtil.startsWithIgnoreCase(it, currentlyTyping)
        }.toList()
    }

    private fun separateFlags(args: Array<String>): Pair<List<String>, Map<String, String>> {
        // Strip out options
        val (flagStrings, arguments) = args.partition {
            it.startsWith("-")
        }
        val flags = flagStrings.associate {
            val separator = it.indexOf('=', 0)
            if (separator == -1) {
                Pair(it.substring(1), "")
            } else {
                // +1s are to skip "-" and "="
                Pair(it.substring(1, separator), it.substring(separator + 1))
            }
        }
        return Pair(arguments, flags)
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return if (args.size <= 1)
            children
                .filter { it.permission().isAllowedBy(sender) }
                .map { it.name().format(emptyList(), it.cmdContext()).toString() }
        else
            emptyList()
    }

    override fun permission(): Permission {
        return Permissions.BASE
    }

    override fun description(): Formattable {
        return Text.BOMBERMAN_DESCRIPTION
    }

    override fun extra(): Formattable {
        return Text.COMMAND_GROUP_EXTRA
    }

    override fun example(): Formattable {
        return Text.COMMAND_GROUP_EXAMPLE
    }

    override fun usage(): Formattable {
        return Text.COMMAND_GROUP_USAGE
    }

    override fun format(args: List<Message>, context: Context): Message {
        return if (args.getOrNull(0).toString().equals("children", ignoreCase = true)) {
            CollectionWrapper(children).format(args.drop(1), context)
        } else {
            super.format(args, context)
        }
    }
}