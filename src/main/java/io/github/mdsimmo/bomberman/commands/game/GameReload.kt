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
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender
import java.io.IOException

class GameReload(parent: Cmd) : GameCommand(parent) {
    override fun name(): Formattable {
        return Text.RELOAD_NAME
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        BmGameDeletedIntent.delete(game, false)
        try {
            val newGame = GameSave.loadGame(Bomberman.instance.gameSaves().resolve(GameSave.sanitize("${game.name}.game.zip")))
            BmGameBuildIntent.build(newGame)
            Text.RELOAD_SUCCESS.format(cmdContext()
                .plus("game", newGame))
                .sendTo(sender)
        } catch (e: IOException) {
            Text.RELOAD_CANNOT_LOAD.format(cmdContext()
                .plus("game", game.name))
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

    override fun extra(): Formattable {
        return Text.RELOAD_EXTRA
    }

    override fun example(): Formattable {
        return Text.RELOAD_EXAMPLE
    }

    override fun description(): Formattable {
        return Text.RELOAD_DESCRIPTION
    }

    override fun usage(): Formattable {
        return Text.RELOAD_USAGE
    }
}