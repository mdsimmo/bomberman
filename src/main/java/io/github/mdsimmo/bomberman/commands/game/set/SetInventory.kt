package io.github.mdsimmo.bomberman.commands.game.set

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.commands.game.set.inventory.PlayerInvEditor
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetInventory(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.INVENTORY_NAME).format()
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false
        if (sender !is Player) {
            context(Text.MUST_BE_PLAYER).sendTo(sender)
            return true
        }
        if (sender.gameMode != GameMode.CREATIVE) {
            context(Text.INVENTORY_NEED_CREATIVE).sendTo(sender)
            return true
        }

        PlayerInvEditor.manage(sender, game)
        return true
    }

    override fun permission(): Permission {
        return Permissions.SET_INVENTORY
    }

    override fun extra(): Message {
        return context(Text.INVENTORY_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.INFO_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.INVENTORY_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.INVENTORY_USAGE).format()
    }
}