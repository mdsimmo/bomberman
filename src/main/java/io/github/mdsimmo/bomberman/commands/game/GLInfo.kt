package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GLCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.game.GL
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class GLInfo(parent: Cmd) : GLCommand(parent) {
    override fun name(): Message {
        return Text.INFO_NAME.format()
    }

    override fun permission(): Permission {
        return Permissions.INFO
    }

    override fun glRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, gl: GL): Boolean {
        if (args.isNotEmpty())
            return false
        context(Text.INFO_RESULT)
                .with("gl", gl)
                .sendTo(sender)
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