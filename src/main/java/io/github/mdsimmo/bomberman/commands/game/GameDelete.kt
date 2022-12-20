package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmGameDeletedIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class GameDelete(parent: Cmd) : GameCommand(parent) {
    override fun name(): Formattable {
        return Text.DELETE_NAME
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        BmGameDeletedIntent.delete(game, true)
        Text.DELETE_SUCCESS.format(cmdContext().plus("game", game)).sendTo(sender)
        return true
    }

    override fun permission(): Permission {
        return Permissions.DELETE
    }

    override fun example(): Formattable {
        return Text.DELETE_EXAMPLE
    }

    override fun extra(): Formattable {
        return Text.DELETE_EXTRA
    }

    override fun description(): Formattable {
        return Text.DELETE_DESCRIPTION
    }

    override fun usage(): Formattable {
        return Text.DELETE_USAGE
    }
}