package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GLCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmGLDeleteIntent
import io.github.mdsimmo.bomberman.game.GL
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class GLDelete(parent: Cmd) : GLCommand(parent) {
    override fun name(): Message {
        return context(Text.DELETE_NAME).format()
    }

    override fun glRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, gl: GL): Boolean {
        if (args.isNotEmpty())
            return false
        BmGLDeleteIntent.delete(gl, true)
        context(Text.DELETE_SUCCESS)
                .with("gl", gl)
                .sendTo(sender)
        return true
    }

    override fun permission(): Permission {
        return Permissions.DELETE
    }

    override fun example(): Message {
        return context(Text.DELETE_EXAMPLE).format()
    }

    override fun extra(): Message {
        return context(Text.DELETE_EXTRA).format()
    }

    override fun description(): Message {
        return context(Text.DELETE_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.DELETE_USAGE).format()
    }
}