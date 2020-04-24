package io.github.mdsimmo.bomberman.commands.game.set

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class SetLives(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.LIVES_NAME).format()
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.size != 1)
            return false
        val amount = args[0].toIntOrNull()
        if (amount == null) {
            context(Text.INVALID_NUMBER)
                    .with("number", args[0])
                    .sendTo(sender)
            return true
        }
        game.settings.lives = amount
        Game.saveGame(game)
        context(Text.LIVES_SUCCESS)
                .with("game", game)
                .sendTo(sender)
        return true
    }

    override fun permission(): Permission {
        return Permissions.SET
    }

    override fun extra(): Message {
        return context(Text.LIVES_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.LIVES_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.LIVES_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.LIVES_USAGE).format()
    }
}