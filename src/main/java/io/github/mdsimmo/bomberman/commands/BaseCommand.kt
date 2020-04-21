package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.commands.game.*
import io.github.mdsimmo.bomberman.commands.game.set.Set
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
                Set(this),
                GameCreate(this),
                GameInfo(this),
                GameJoin(this),
                GameLeave(this),
                GameDelete(this),
                RunStart(this),
                RunStop(this),
                GameList(this),
                GameReload(this)
        )
    }

    override fun name(): Message {
        return Message.of("bomberman")
    }

    override fun permission(): Permission {
        return Permission.PLAYER
    }

    override fun description(): Message {
        return context(Text.BOMBERMAN_DESCRIPTION).format()
    }

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        // Strip out options
        val (flagStrings, arguments) = args.partition {
            it.startsWith("-")
        }
        val flags = flagStrings.map {
            val separator = it.indexOf('=', 0)
            if (separator == -1) {
                Pair(it.substring(1), "")
            } else {
                // +1s are to skip "-" and "="
                Pair(it.substring(1, separator), it.substring(separator+1))
            }
        }.toMap()
        execute(sender, arguments, flags)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, typing: String, args: Array<String>): List<String> {
        val arguments = args.asList()
        val allOptions = options(sender, arguments).toMutableList()
        val currentlyCompleting = args.last()
        return allOptions.filter {
            StringUtil.startsWithIgnoreCase(it, currentlyCompleting)
        }
    }
}