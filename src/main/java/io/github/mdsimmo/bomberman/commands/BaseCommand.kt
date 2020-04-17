package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.Bomberman
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
        val plugin: Bomberman = Bomberman.instance

        // TODO BaseCommand shouldn't self register
        plugin.getCommand("bomberman")!!.setExecutor(this)
        plugin.getCommand("bomberman")!!.tabCompleter = this

        addChildren(
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
        execute(sender, args.toList())
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