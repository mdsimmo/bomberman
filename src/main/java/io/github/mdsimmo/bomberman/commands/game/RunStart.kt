package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.events.BmRunStartCountDownIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class RunStart(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.START_NAME).format()
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun flags(args: List<String>, flags: Map<String, String>): Set<String> {
        return setOf("d")
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
                    Text.INVALID_NUMBER.with("number", s).sendTo(sender)
                    return true
                } else {
                    i
                }
            }
        }

        val e = BmRunStartCountDownIntent.startGame(game, delay)
        if (e.isCancelled) {
            (e.cancelledReason
                    ?: Text.GAME_START_CANCELLED
                            .with("game", game)
                            .format()
                    )
                    .sendTo(sender)
        } else {
            Text.GAME_START_SUCCESS.with("game", game).sendTo(sender)
        }
        return true
    }

    override fun permission(): Permission {
        return Permission.GAME_OPERATE
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