package io.github.mdsimmo.bomberman.commands.game.set.inventory

import io.github.mdsimmo.bomberman.Bomberman
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.tags.ItemTagType
import java.util.concurrent.atomic.AtomicInteger

class GuiBuilder : Listener {

    data class Index (val x: Int, val y: Int, val invIndex: Int, val section: Char, val secIndex: Int)

    data class ItemSlot(val item: ItemStack?) {
        constructor(type: Material, qty: Int = 1): this(ItemStack(type, qty))

        fun alterMeta(mod: (ItemMeta) -> Unit): ItemSlot {
            val newItem = item?.clone() ?: return this
            val itemMeta = newItem.itemMeta!!
            mod(itemMeta)
            newItem.itemMeta = itemMeta
            return copy(item = newItem)
        }

        fun unMovable(): ItemSlot {
            return alterMeta { it.customTagContainer.setCustomTag(noMoveKey, ItemTagType.BYTE, 1) }
        }

        fun hideAttributes(): ItemSlot {
            return alterMeta { it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES) }
        }

        fun displayName(title: String): ItemSlot {
            return alterMeta { it.setDisplayName(title) }
        }
    }

    companion object {

        val blank = ItemSlot(Material.BLACK_STAINED_GLASS_PANE).hideAttributes().displayName("-").unMovable()

        fun show(player: Player, name: String, contents: Array<CharSequence>,
                 onInit: (Index) -> ItemSlot,
                 onClick: (Index, ItemStack?, ItemStack?) -> Unit = { _, _, _ -> },
                 onClose: ((Sequence<Pair<Index, ItemStack?>>) -> Unit) = { _ -> }
        ) {
            val size = contents.size*9
            // Create a dummy inventory to hold the data
            val inventory = Bukkit.createInventory(null, size, name)

            val slotLookup = ArrayList<Index>()
            val sectionCount = mutableMapOf<Char, AtomicInteger>()
            for (i in 0..size) {
                val x  = i % 9
                val y = i / 9
                val c = contents[y][x]
                val index = Index(
                        x  = i % 9,
                        y = i / 9,
                        invIndex = i,
                        secIndex = sectionCount.getOrPut(c) {AtomicInteger(0)}.getAndIncrement(),
                        section = c
                )
                slotLookup += index
                val slot = onInit(index)
                inventory.setItem(i, slot.item)
            }
            val view = player.openInventory(inventory) ?: return
            lookup[view] = InvMemory(slotLookup, onClick, onClose)
        }

        private val plugin = Bomberman.instance
        private val noMoveKey = NamespacedKey(plugin, "nomove")
        private val lookup: MutableMap<InventoryView, InvMemory> = HashMap()

        init {
            Bukkit.getPluginManager().registerEvents(GuiBuilder(), plugin)
        }

        private fun isNotMovable(item: ItemStack): Boolean {
            val b = item.itemMeta?.customTagContainer?.getCustomTag(noMoveKey, ItemTagType.BYTE) ?: return false
            return b == 1.toByte()
        }
    }

    private data class InvMemory(
            val slots: List<Index>,
            val onClick: (Index, ItemStack?, ItemStack?) -> Unit,
            val onClose: ((Sequence<Pair<Index, ItemStack?>>) -> Unit)
    )

    @EventHandler
    fun onInventoryClosed(e: InventoryCloseEvent) {
        val mem = lookup[e.view] ?: return
        lookup.remove(e.view)
        val inv = e.inventory
        mem.onClose(sequence {
            for (index in mem.slots) {
                yield(Pair(index, inv.getItem(index.invIndex)))
            }
        })
    }

    @EventHandler
    fun onInventoryItemClicked(e: InventoryClickEvent) {
        val mem = lookup[e.view] ?: return
        val index = mem.slots[e.slot]
        val currentItem = e.currentItem
        val cursorItem = e.cursor
        mem.onClick(index, currentItem, cursorItem)
        if ((currentItem != null && isNotMovable(currentItem))
                || (cursorItem != null && isNotMovable(cursorItem))) {
            e.isCancelled = true
        }
    }

}