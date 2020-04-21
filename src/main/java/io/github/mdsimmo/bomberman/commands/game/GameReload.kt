package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.events.BmGameBuildIntent
import io.github.mdsimmo.bomberman.events.BmGameDeletedIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender
import java.io.File

class GameReload(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.RELOAD_NAME).format()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        BmGameDeletedIntent.delete(game, false)
        val new = Game.loadGame(File(Bomberman.instance.settings.gameSaves(), "${game.name}.yml"))
        if (new == null) {
            Text.RELOAD_CANNOT_LOAD
                    .with("game", game)
                    .sendTo(sender)
        } else {
            BmGameBuildIntent.build(new)
            Text.RELOAD_SUCCESS
                    .with("game", game)
                    .sendTo(sender)
        }
        return true
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