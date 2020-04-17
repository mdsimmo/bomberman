package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.events.BmGameDeletedIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class GameDelete(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.DESTROY_NAME).format()
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        BmGameDeletedIntent.delete(game, true)
        context(Text.DESTROY_SUCCESS).with("game", game).sendTo(sender)
        return true
    }

    override fun permission(): Permission {
        return Permission.GAME_DICTATE
    }

    override fun example(): Message {
        return context(Text.DESTROY_EXAMPLE).format()
    }

    override fun extra(): Message {
        return context(Text.DESTROY_EXTRA).format()
    }

    override fun description(): Message {
        return context(Text.DESTROY_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.DESTROY_USAGE).format()
    }
}