package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GLCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmPlayerJoinGLIntent
import io.github.mdsimmo.bomberman.game.GL
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GLJoin(parent: Cmd) : GLCommand(parent) {

    override fun name(): Message {
        return context(Text.JOIN_NAME).format()
    }

    override fun glRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, gl: GL): Boolean {
        if (args.isNotEmpty())
            return false
        if (sender !is Player) {
            context(Text.MUST_BE_PLAYER)
                    .sendTo(sender)
            return true
        }
        val e = BmPlayerJoinGLIntent.join(gl, sender)

        if (e.isCancelled) {
            e.cancelledReason()
                    ?.apply {
                        sendTo(sender)
                    }
                    ?: {
                        context(Text.COMMAND_CANCELLED)
                                .with("player", sender)
                                .sendTo(sender)
                    }()
        } else {
            context(Text.JOIN_SUCCESS)
                    .with("gl", gl)
                    .with("player", sender)
                    .sendTo(sender)
        }
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