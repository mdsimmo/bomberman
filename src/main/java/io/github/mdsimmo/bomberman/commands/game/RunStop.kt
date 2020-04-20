package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.events.BmRunStoppedIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class RunStop(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.STOP_NAME).format()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, modifiers: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        val e = BmRunStoppedIntent.stopGame(game)
        if (!e.isCancelled) Text.STOP_SUCCESS
                .with("game", game)
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
        return Permission.GAME_OPERATE
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
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