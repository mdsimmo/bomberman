package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class GameInfo(parent: Cmd) : GameCommand(parent) {
    override fun name(): Formattable {
        return Text.INFO_NAME
    }

    override fun permission(): Permission {
        return Permissions.INFO
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        Text.INFO_DETAILS.format(cmdContext().plus("game", game)).sendTo(sender)
        return true
    }

    override fun extra(): Formattable {
        return Text.INFO_EXTRA
    }

    override fun example(): Formattable {
        return Text.INFO_EXAMPLE
    }

    override fun description(): Formattable {
        return Text.INFO_DESCRIPTION
    }

    override fun usage(): Formattable {
        return Text.INFO_USAGE
    }
}