package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmGameListIntent
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class GameList(parent: Cmd?) : Cmd(parent) {
    override fun name(): Formattable {
        return Text.GAMELIST_NAME
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
        if (args.isNotEmpty())
            return false
        val games = BmGameListIntent.listGames()
        Text.GAMELIST_GAMES.format(cmdContext()
                .plus("games", games))
                .sendTo(sender)
        return true
    }

    override fun permission(): Permission {
        return Permissions.LIST
    }

    override fun example(): Formattable {
        return Text.GAMELIST_EXAMPLE
    }

    override fun extra(): Formattable {
        return Text.GAMELIST_EXTRA
    }

    override fun description(): Formattable {
        return Text.GAMELIST_DESCRIPTION
    }

    override fun usage(): Formattable {
        return Text.GAMELIST_USAGE
    }
}