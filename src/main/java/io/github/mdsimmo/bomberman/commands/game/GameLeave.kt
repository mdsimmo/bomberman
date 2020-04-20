package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.events.BmPlayerLeaveGameIntent
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameLeave(parent: Cmd) : Cmd(parent) {
    override fun name(): Message {
        return context(Text.LEAVE_NAME).format()
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return emptyList()
    }

    override fun run(sender: CommandSender, args: List<String>, modifiers: Map<String, String>): Boolean {
        if (args.isNotEmpty())
            return false
        if (sender is Player) {
            val e = BmPlayerLeaveGameIntent.leave(sender)
            if (e.isHandled()) {
                Text.LEAVE_SUCCESS
                        .with("player", sender)
                        .sendTo(sender)
            } else {
                Text.LEAVE_NOT_JOINED
                        .with("player", sender)
                        .sendTo(sender)
            }
        } else {
            context(Text.MUST_BE_PLAYER)
                    .sendTo(sender)
        }
        return true
    }

    override fun permission(): Permission {
        return Permission.PLAYER
    }

    override fun extra(): Message {
        return context(Text.LEAVE_EXTRA).format()
    }

    override fun description(): Message {
        return context(Text.LEAVE_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.LEAVE_USAGE).format()
    }

    override fun example(): Message {
        return context(Text.JOIN_EXAMPLE).format()
    }
}