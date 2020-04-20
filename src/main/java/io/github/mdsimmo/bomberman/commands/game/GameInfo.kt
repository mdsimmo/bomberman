package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class GameInfo(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return Text.INFO_NAME.format()
    }

    override fun permission(): Permission {
        return Permission.PLAYER
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, modifiers: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        context(Text.INFO_DETAILS).with("game", game).sendTo(sender)
        return true
    }

    override fun extra(): Message {
        return context(Text.INFO_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.INFO_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.INFO_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.INFO_USAGE).format()
    }
}