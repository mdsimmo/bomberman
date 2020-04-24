package io.github.mdsimmo.bomberman.commands.game.set.inventory

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.BmGameLookupIntent
import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.tags.ItemTagType

class PlayerInvEditor : Listener {

    companion object {

        fun manage(player: Player, game: Game) {
            // Create a dummy inventory to hold the data
            val inventory = Bukkit.createInventory(null, 9*6, "Starter Inventory")
            for ((index, item) in game.settings.initialItems.withIndex()) {
                when (index) {
                    // Hot bar
                    in (0+9*0)..(8+9*0) -> inventory.setItem(9*5+index, item)
                    // Main inventory
                    in (0+9*1)..(8+9*3) -> inventory.setItem(index+9*1, item)
                    // Armor slots
                    in (0+9*4)..(3+9*4) -> inventory.setItem(3-(index-9*4)+2+9*1, item)
                    // Offhand
                    in (4+9*4)..(4+9*4) -> inventory.setItem((index-9*4)+2+9*1, item)
                }
            }

            // Pretty the display up
            val blank = ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1).also {
                setKey(it, gameKey, ItemTagType.STRING, game.name)
                markUnmovable(it)
                hideOther(it)
                modMeta(it) { meta -> meta.setDisplayName("-") }
            }
            inventory.setItem(0, blank.clone())
            inventory.setItem(1, blank.clone())
            inventory.setItem(2, ItemStack(Material.IRON_HELMET).also {
                markUnmovable(it)
                hideOther(it)
            })
            inventory.setItem(3, ItemStack(Material.IRON_CHESTPLATE).also {
                markUnmovable(it)
                hideOther(it)
            })
            inventory.setItem(4, ItemStack(Material.IRON_LEGGINGS).also {
                markUnmovable(it)
                hideOther(it)
            })
            inventory.setItem(5, ItemStack(Material.IRON_BOOTS).also {
                markUnmovable(it)
                hideOther(it)
            })
            inventory.setItem(6, ItemStack(Material.SHIELD).also {
                markUnmovable(it)
                hideOther(it)
            })
            inventory.setItem(7, blank.clone())
            inventory.setItem(8, blank.clone())
            inventory.setItem(0+9, blank.clone())
            inventory.setItem(1+9, blank.clone())
            inventory.setItem(7+9, blank.clone())
            inventory.setItem(8+9, blank.clone())

            // Show it. Static EventListener will handle the rest
            player.openInventory(inventory)
        }

        private val plugin = Bomberman.instance
        private val gameKey = NamespacedKey(plugin, "gameid")
        private val noMoveKey = NamespacedKey(plugin, "nomove")

        init {
            Bukkit.getPluginManager().registerEvents(PlayerInvEditor(), plugin)
        }

        private fun markUnmovable(item: ItemStack) {
            setKey(item, noMoveKey, ItemTagType.BYTE, 1)
        }

        private fun isNotMovable(item: ItemStack): Boolean {
            val b = getKey(item, noMoveKey, ItemTagType.BYTE) ?: false
            return b == 1.toByte()
        }

        private fun <T, Z> setKey(item: ItemStack, key: NamespacedKey, type: ItemTagType<T, Z>, value: Z) {
            modMeta(item) { it.customTagContainer.setCustomTag(key, type, value) }
        }

        private fun <T, Z> getKey(item: ItemStack, key: NamespacedKey, type: ItemTagType<T, Z>): Z? {
            return item.itemMeta?.customTagContainer?.getCustomTag(key, type)
        }

        private fun hideOther(item: ItemStack) {
            modMeta(item) { it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES) }
        }

        private fun modMeta(itemStack: ItemStack, mod: (ItemMeta) -> Unit) {
            val itemMeta = itemStack.itemMeta!!
            mod(itemMeta)
            itemStack.itemMeta = itemMeta
        }
    }

    private fun gameName(inv: Inventory): String? {
        val identifier = inv.getItem(0) ?: return null
        return getKey(identifier, gameKey, ItemTagType.STRING)
    }

    @EventHandler
    fun onInventoryClosed(e: InventoryCloseEvent) {
        val inv = e.inventory
        val gameName = gameName(inv) ?: return
        val game = BmGameLookupIntent.find(gameName) ?: return

        val items: MutableList<ItemStack?> = e.inventory.contents.toMutableList()
        val initialItems = Array<ItemStack?>(items.size) { null }
        for ((i, item) in items.withIndex()) {
            when (i) {
                // armor slots
                in (2+0+1*9)..(2+3+1*9) -> initialItems[4*9 + 3-(i-2-1*9)] = item
                // off hand slot
                (2+4+1*9) -> initialItems[4*9 + i-2-1*9] = item
                // main inventory
                in (0+2*9..8+4*9) -> initialItems[i-1*9] = item
                // hot bar
                in (0+5*9..8+5*9) -> initialItems[i-5*9] = item
            }
        }

        game.settings.initialItems = initialItems.map { it?.clone() }.toList()
        Game.saveGame(game)
    }

    @EventHandler
    fun onInventoryItemClicked(e: InventoryClickEvent) {
        val currentItem = e.currentItem
        val cursorItem = e.cursor
        if ((currentItem != null && isNotMovable(currentItem))
                || (cursorItem != null && isNotMovable(cursorItem))) {
            e.isCancelled = true
        }
    }

}