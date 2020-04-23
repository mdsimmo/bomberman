package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Explosion.BlockPlan
import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import java.util.*

/**
 * Called when a bomb turns from a tnt block into a fire '+'
 */
class BmExplosionEvent(val game: Game, val cause: Player, igniting: Set<BlockPlan>)
    : BmEvent(), Cancellable by BmCancellable() {

    val igniting = HashSet(igniting)

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}