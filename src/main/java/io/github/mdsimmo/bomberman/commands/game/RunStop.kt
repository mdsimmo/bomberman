package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmRunStoppedIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class RunStop(parent: Cmd) : GameCommand(parent) {
    override fun name(): Formattable {
        return Text.STOP_NAME
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        val e = BmRunStoppedIntent.stopGame(game)
        if (!e.isCancelled) Text.STOP_SUCCESS.format(cmdContext()
                    .plus("game", game))
                .sendTo(sender) else {
            (e.cancelledReason()
                    ?: Text.COMMAND_CANCELLED.format(cmdContext()
                                .plus("command", this)))
                .sendTo(sender)
        }
        return true
    }

    override fun permission(): Permission {
        return Permissions.STOP
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun extra(): Formattable {
        return Text.STOP_EXTRA
    }

    override fun example(): Formattable {
        return Text.STOP_EXAMPLE
    }

    override fun description(): Formattable {
        return Text.STOP_DESCRIPTION
    }

    override fun usage(): Formattable {
        return Text.STOP_USAGE
    }
}