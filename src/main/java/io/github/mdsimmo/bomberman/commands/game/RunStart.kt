package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GLCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmRunStartCountDownIntent
import io.github.mdsimmo.bomberman.game.GL
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class RunStart(parent: Cmd) : GLCommand(parent) {
    override fun name(): Message {
        return context(Text.START_NAME).format()
    }

    override fun flags(sender: CommandSender, args: List<String>, flags: Map<String, String>): Set<String> {
        return setOf("d", "o")
    }

    override fun flagExtension(flag: String): Message {
        return when (flag) {
            "d" -> context(Text.START_FLAG_DELAY_EXT).format()
            else -> Message.empty
        }
    }

    override fun flagDescription(flag: String): Message {
        return when (flag) {
            "d" -> context(Text.START_FLAG_DELAY_DESC).format()
            "o" -> context(Text.START_FLAG_OVERRIDE_DESC).format()
            else -> Message.empty
        }
    }

    override fun glRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, gl: GL): Boolean {
        if (args.isNotEmpty()) {
            return false
        }

        if (gl !is Game) {
            // TODO don't let players try and start a lobby
            context(Text.UNIMPLEMENTED).sendTo(sender)
            return true
        }

        // get the delay
        val delay = when (val s = flags["d"] ?: flags["delay"]) {
            null -> 3
            else -> {
                val i = s.toIntOrNull()
                if ((i == null) || (i < 0)) {
                    Text.INVALID_NUMBER.with("number", s).sendTo(sender)
                    return true
                } else {
                    i
                }
            }
        }

        val e = BmRunStartCountDownIntent.startGame(gl, delay, flags.containsKey("o"))
        if (e.isCancelled) {
            (e.cancelledReason
                    ?: Text.COMMAND_CANCELLED
                            .with("gl", gl)
                            .format()
                    )
                    .sendTo(sender)
        } else {
                Text.GAME_START_SUCCESS.with("game", gl).sendTo(sender)
        }
        return true
    }

    override fun permission(): Permission {
        return Permissions.START
    }

    override fun extra(): Message {
        return context(Text.START_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.START_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.START_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.START_USAGE).format()
    }
}