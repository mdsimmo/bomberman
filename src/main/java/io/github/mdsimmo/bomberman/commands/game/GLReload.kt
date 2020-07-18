package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GLCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmGameBuildIntent
import io.github.mdsimmo.bomberman.events.BmGLDeleteIntent
import io.github.mdsimmo.bomberman.game.GL
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender
import java.io.File

class GLReload(parent: Cmd) : GLCommand(parent) {
    override fun name(): Message {
        return context(Text.RELOAD_NAME).format()
    }

    override fun glRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, gl: GL): Boolean {
        if (args.isNotEmpty())
            return false
        if (gl !is Game) {
            // TODO reload lobbies
            context(Text.UNIMPLEMENTED).sendTo(sender)
            return true
        }
        BmGLDeleteIntent.delete(gl, false)
        val new = Game.loadGame(File(Bomberman.instance.settings.gameSaves(), "${gl.name}.yml"))
        if (new == null) {
            Text.RELOAD_CANNOT_LOAD
                    .with("gl", gl)
                    .sendTo(sender)
        } else {
            BmGameBuildIntent.build(new)
            Text.RELOAD_SUCCESS
                    .with("gl", gl)
                    .sendTo(sender)
        }
        return true
    }

    override fun permission(): Permission {
        return Permissions.RELOAD
    }

    override fun extra(): Message {
        return context(Text.RELOAD_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.RELOAD_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.RELOAD_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.RELOAD_USAGE).format()
    }
}