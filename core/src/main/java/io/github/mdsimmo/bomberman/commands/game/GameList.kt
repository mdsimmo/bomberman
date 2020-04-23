package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.events.BmGameListIntent
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class GameList(parent: Cmd?) : Cmd(parent) {
    override fun name(): Message {
        return context(Text.GAMELIST_NAME).format()
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
        if (args.isNotEmpty())
            return false
        val games = BmGameListIntent.listGames()
        context(Text.GAMELIST_GAMES)
                .with("games", games)
                .sendTo(sender)
        return true
    }

    override fun permission(): Permission {
        return Permission.PLAYER
    }

    override fun example(): Message {
        return context(Text.GAMELIST_EXAMPLE).format()
    }

    override fun extra(): Message {
        return context(Text.GAMELIST_EXTRA).format()
    }

    override fun description(): Message {
        return context(Text.GAMELIST_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.GAMELIST_USAGE).format()
    }
}