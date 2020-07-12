package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmJoinGameIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameJoin(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.JOIN_NAME).format()
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        if (sender !is Player) {
            context(Text.MUST_BE_PLAYER)
                    .sendTo(sender)
            return true
        }
        val e = BmJoinGameIntent.join(game, sender)
        e.message?.sendTo(sender)
        return true
    }

    override fun permission(): Permission {
        return Permissions.JOIN
    }

    override fun extra(): Message {
        return context(Text.JOIN_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.JOIN_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.JOIN_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.JOIN_USAGE).format()
    }
}