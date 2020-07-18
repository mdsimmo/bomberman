package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.GL
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * Called whenever a bm player moves. Cannot modify the event.
 */
class BmPlayerMovedEvent(
        val game: GL,
        val player: Player,
        private val from: Location,
        private val to: Location) : BmEvent() {

    fun getFrom(): Location {
        return from.clone()
    }

    fun getTo(): Location {
        return to.clone()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

}