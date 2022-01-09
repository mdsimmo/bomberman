package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmGameBuildIntent
import io.github.mdsimmo.bomberman.events.BmGameDeletedIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.game.GameSave
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender
import java.io.IOException

class GameReload(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.RELOAD_NAME).format()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        BmGameDeletedIntent.delete(game, false)
        try {
            // TODO game name may not match the actual file as it can change with special characters
            val newGame = GameSave.loadGame(Bomberman.instance.settings.gameSaves().resolve("${game.name}.game.zip"))
            BmGameBuildIntent.build(newGame)
            Text.RELOAD_SUCCESS
                .with("game", newGame)
                .sendTo(sender)
        } catch (e: IOException) {
            Text.RELOAD_CANNOT_LOAD
                .with("game", game.name)
                .sendTo(sender)
        }
        return true
    }

    override fun permission(): Permission {
        return Permissions.RELOAD
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