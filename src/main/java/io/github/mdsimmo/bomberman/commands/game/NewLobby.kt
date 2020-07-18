package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmJoinableListIntent
import io.github.mdsimmo.bomberman.events.BmGLLookupIntent
import io.github.mdsimmo.bomberman.game.GL
import io.github.mdsimmo.bomberman.game.Lobby
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NewLobby(parent: Cmd) : Cmd(parent) {

    override fun name(): Message {
        return context(Text.NEWLOBBY_NAME).format()
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> BmJoinableListIntent.list().map(GL::name).toList()
            else -> emptyList()
        }
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
        if (args.size != 1)
            return false
        if (sender !is Player) {
            context(Text.MUST_BE_PLAYER).sendTo(sender)
            return true
        }
        val lobbyName = args[0]
        BmGLLookupIntent.find(lobbyName)?.let { gl ->
            context(Text.NEWLOBBY_GL_EXISTS)
                    .with("gl", gl)
                    .sendTo(sender)
            return true
        }

        val lobby = Lobby.create(lobbyName, sender.location, 50)
        context(Text.NEWLOBBY_SUCCESS)
                .with("lobby", lobby)
                .sendTo(sender)
        return true
    }

    override fun permission(): Permission {
        return Permissions.NEWLOBBY
    }

    override fun example(): Message {
        return context(Text.NEWLOBBY_EXAMPLE).format()
    }

    override fun extra(): Message {
        return context(Text.NEWLOBBY_EXTRA).format()
    }

    override fun description(): Message {
        return context(Text.NEWLOBBY_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.NEWLOBBY_USAGE).format()
    }
}