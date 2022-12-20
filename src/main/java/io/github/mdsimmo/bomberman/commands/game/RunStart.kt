package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmRunStartCountDownIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class RunStart(parent: Cmd) : GameCommand(parent) {
    override fun name(): Formattable {
        return Text.START_NAME
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun flags(sender: CommandSender): Set<String> {
        return setOf("d", "o")
    }

    override fun flagExtension(flag: String): Formattable {
        return when (flag) {
            "d" -> Text.START_FLAG_DELAY_EXT
            else -> Message.empty
        }
    }

    override fun flagDescription(flag: String): Formattable {
        return when (flag) {
            "d" -> Text.START_FLAG_DELAY_DESC
            "o" -> Text.START_FLAG_OVERRIDE_DESC
            else -> Message.empty
        }
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty()) {
            return false
        }
        // get the delay
        val delay = when (val s = flags["d"] ?: flags["delay"]) {
            null -> 3
            else -> {
                val i = s.toIntOrNull()
                if ((i == null) || (i < 0)) {
                    Text.INVALID_NUMBER.format(cmdContext().plus("number", s)).sendTo(sender)
                    return true
                } else {
                    i
                }
            }
        }

        val e = BmRunStartCountDownIntent.startGame(game, delay, flags.containsKey("o"))
        if (e.isCancelled) {
            (e.cancelledReason
                    ?: Text.COMMAND_CANCELLED.format(cmdContext()
                            .plus("game", game)))
                .sendTo(sender)
        } else {
            Text.GAME_START_SUCCESS.format(cmdContext().plus("game", game)).sendTo(sender)
        }
        return true
    }

    override fun permission(): Permission {
        return Permissions.START
    }

    override fun extra(): Formattable {
        return Text.START_EXTRA
    }

    override fun example(): Formattable {
        return Text.START_EXAMPLE
    }

    override fun description(): Formattable {
        return Text.START_DESCRIPTION
    }

    override fun usage(): Formattable {
        return Text.START_USAGE
    }
}