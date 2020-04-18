package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class GameReload(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.RELOAD_NAME).format()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        // TODO arena rebuilt twice if running
        sender.sendMessage("Not implemented yet")
        return true
        /*val newGame = GameRegistry.reload(game)
        BmGameBuildIntent.build(newGame)
        Text.RELOAD_SUCCESS
                .with("game", newGame)
                .sendTo(sender)
        return true*/
    }

    override fun permission(): Permission {
        return Permission.GAME_DICTATE
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun extra(): Message {
        return context(Text.RELOAD_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.RELOAD_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.RELOAD_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.RELOAD_USAGE).format()
    }
}