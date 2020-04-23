package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.event.HandlerList

/**
 * Called whenever a game run is stopped. May be due to game finishing, game forcefully stoped, server shutdown, etc.
 */
class BmTimerCountedEvent(val game: Game, var count: Int) : BmEvent() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

}