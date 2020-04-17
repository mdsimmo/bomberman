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

    override fun gameRun(sender: CommandSender, args: List<String>, game: Game): Boolean {
        if (args.size > 1)
            return false
        // get the delay
        val delay = when (val s = args.getOrNull(1)) {
            null -> 3
            else -> {
                val i = s.toIntOrNull()
                when {
                    i == null -> return false
                    i < 0 -> {
                        Text.INVALID_NUMBER.with("number", i).sendTo(sender)
                        return true
                    }
                    else -> i
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