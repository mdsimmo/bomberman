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

class SetBlockTypes(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.BLOCK_TYPES_NAME).format()
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
            context(Text.BLOCK_TYPES_NEED_CREATIVE).sendTo(sender)
            return true
        }

        // TODO, initial open block settings screen is copied twice
        showSelection(sender, game, 0, "Select Destructible Blocks", game.settings.destructible) { game.settings.destructible = it }
        return true
    }

    private fun showSelection(player: Player, game: Game, selected: Int, description: String, types: Set<Material>, result: (Set<Material>) -> Unit) {
        val typesList = types.toList()
        val settings = game.settings
        GuiBuilder.show(player, "Block Types", arrayOf(
                " d s p n ",
                " c cEc c ",
                "iiiiiiiii",
                "iiiiiiiii"),
                onInit = { index ->
                    when (index.section) {
                        'E' -> GuiBuilder.ItemSlot(Material.EMERALD).unMovable().displayName(description)
                        'd' -> GuiBuilder.ItemSlot(Material.DIRT).unMovable().displayName("Destructible")
                        's' -> GuiBuilder.ItemSlot(Material.OBSIDIAN).displayName("Indestructible")
                        'p' -> GuiBuilder.ItemSlot(Material.OXEYE_DAISY).displayName("Pass destroy")
                        'n' -> GuiBuilder.ItemSlot(Material.SIGN).displayName("Pass keep")
                        'c' -> {
                            if (index.secIndex == selected) {
                                GuiBuilder.ItemSlot(Material.YELLOW_STAINED_GLASS_PANE).displayName("^").unMovable()
                            } else {
                                GuiBuilder.blank
                            }
                        }
                        'i' -> {
                            val mat = typesList.getOrNull(index.secIndex)
                            if (mat == null) {
                                GuiBuilder.ItemSlot(null)
                            } else {
                                GuiBuilder.ItemSlot(mat)
                            }
                        }
                        else -> GuiBuilder.blank
                    }
                },
                onClick = { index, _, _ ->
                    when (index.section) {
                        'd' -> showSelection(player, game, 0, "", settings.destructible) {settings.destructible = it}
                        's' -> showSelection(player, game, 1, "Includes all solid blocks by default", settings.indestructible) {settings.indestructible = it}
                        'p' -> showSelection(player, game, 2, "Includes all non solid blocks by default", settings.passDestroy) {settings.passDestroy = it}
                        'n' -> showSelection(player, game, 3, "", settings.passKeep) {settings.passKeep = it}
                    }
                },
                onClose = {
                    val blocks = it.mapNotNull { (index, stack) ->
                        if (index.section == 'i') {
                            stack?.type
                        } else {
                            null
                        }
                    }.toSet()
                    result(blocks)
                    Game.saveGame(game)
                }
        )
    }

    override fun permission(): Permission {
        return Permissions.SET_BLOCK_TYPES
    }

    override fun extra(): Message {
        return context(Text.BLOCK_TYPES_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.BLOCK_TYPES_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.BLOCK_TYPES_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.BLOCK_TYPES_USAGE).format()
    }
}