package io.github.mdsimmo.bomberman.commands.game.set

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.commands.game.set.inventory.GuiBuilder
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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

        GuiBuilder.show(sender, "Initial Inventory", arrayOf(
                "  HCLBS  ",
                "  aaaas  ",
                "iiiiiiiii",
                "iiiiiiiii",
                "iiiiiiiii",
                "hhhhhhhhh"),
                onInit = { index ->
                    val initialItems = game.settings.initialItems
                    when (index.section) {
                        'a' -> GuiBuilder.ItemSlot(initialItems[(3-index.secIndex)+9*4])
                        's' -> GuiBuilder.ItemSlot(initialItems[(9*4+4)])
                        'h' -> GuiBuilder.ItemSlot(initialItems[index.secIndex])
                        'i' -> GuiBuilder.ItemSlot(initialItems[index.secIndex+9])
                        'H' -> GuiBuilder.ItemSlot(Material.IRON_HELMET).unMovable().hideAttributes()
                        'C' -> GuiBuilder.ItemSlot(Material.IRON_CHESTPLATE).unMovable().hideAttributes()
                        'L' -> GuiBuilder.ItemSlot(Material.IRON_LEGGINGS).unMovable().hideAttributes()
                        'B' -> GuiBuilder.ItemSlot(Material.IRON_BOOTS).unMovable().hideAttributes()
                        'S' -> GuiBuilder.ItemSlot(Material.SHIELD).unMovable().hideAttributes()
                        else -> GuiBuilder.blank
                    }
                },
                onClose = {inventoryIterator ->
                    val closingItems = Array<ItemStack?>(9*4+5) { null }
                    for ((index, item) in inventoryIterator) {
                        when(index.section) {
                            'a' -> closingItems[9*4+(3-index.secIndex)] = item
                            's' -> closingItems[9*4+4] = item
                            'h' -> closingItems[index.secIndex] = item
                            'i' -> closingItems[9+index.secIndex] = item
                        }
                    }
                    game.settings.initialItems = closingItems.asList()
                }
        )
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