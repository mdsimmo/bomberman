package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.commands.game.*
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.util.StringUtil

class BaseCommand : CommandGroup(null), TabCompleter, CommandExecutor {

    init {
        addChildren(
                DevInfo(this),
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

    override fun name(): Message {
        return Message.of("bomberman")
    }

    override fun permission(): Permission {
        return Permissions.BASE
    }

    override fun description(): Message {
        return context(Text.BOMBERMAN_DESCRIPTION).format()
    }

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        val (arguments, flags) = separateFlags(args)
        if (flags.containsKey("?")) {
            help(sender, arguments, flags)
        } else {
            execute(sender, arguments, flags)
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        val (arguments, flags) = separateFlags(args)
        val currentlyTyping = args.last() // Will always have one.
        val allOptions = if (currentlyTyping.startsWith("-")) {
            val splitIndex = currentlyTyping.indexOf('=')
            if (splitIndex == -1) {
                flags(sender, arguments, flags).map { "-$it" } + "-?"
            } else {
                val key = currentlyTyping.substring(1, splitIndex)
                flagOptions(sender, key, arguments, flags)
                        .map { "-$key=$it" }
            }
        } else {
            options(sender, arguments)
        }
        return allOptions.filter {
            StringUtil.startsWithIgnoreCase(it, currentlyTyping)
        }.toList()
    }

    private fun separateFlags(args: Array<String>) : Pair<List<String>, Map<String, String>> {
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
                Pair(it.substring(1, separator), it.substring(separator+1))
            }
        }
        return Pair(arguments, flags)
    }
}