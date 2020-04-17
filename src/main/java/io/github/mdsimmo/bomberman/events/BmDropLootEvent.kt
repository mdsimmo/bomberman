package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Explosion.BlockPlan
import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Called when a bomb turns from a tnt block into a fire '+'
 */
class BmDropLootEvent(
        val game: Game,
        val cause: Player,
        val ignited: Set<BlockPlan>,
        drops: Map<Location, Set<ItemStack>>)
    : BmEvent(), Cancellable by BmCancellable() {

    val drops = HashMap(drops)

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}