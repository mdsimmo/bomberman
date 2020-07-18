package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GLCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmRunStoppedIntent
import io.github.mdsimmo.bomberman.game.GL
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class RunStop(parent: Cmd) : GLCommand(parent) {
    override fun name(): Message {
        return context(Text.STOP_NAME).format()
    }

    override fun glRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, gl: GL): Boolean {
        if (args.isNotEmpty())
            return false

        if (gl !is Game) {
            // TODO Stop players from stopping a lobby
            context(Text.UNIMPLEMENTED).sendTo(sender)
            return true
        }

        val e = BmRunStoppedIntent.stopGame(gl)
        if (!e.isCancelled) Text.STOP_SUCCESS
                .with("game", gl)
                .sendTo(sender) else {
            (e.cancelledReason()
                    ?: Text.COMMAND_CANCELLED
                                .with("command", this)
                                .format())
                    .sendTo(sender)
        }
        return true
    }

    override fun permission(): Permission {
        return Permissions.STOP
    }

    override fun extra(): Message {
        return context(Text.STOP_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.STOP_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.STOP_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.STOP_USAGE).format()
    }
}