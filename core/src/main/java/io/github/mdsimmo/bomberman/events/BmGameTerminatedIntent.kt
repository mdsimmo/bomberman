package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called whenever a game is being removed - may be because game deleted or because
 * server is shutting down. Is possible game starts back when server starts back.
 * All event listeners for the game should destroy themselves on this event.
 */
class BmGameTerminatedIntent private constructor(val game: Game) : BmEvent(), Intent by BmIntent() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun terminateGame(game: Game) {
            val e = BmGameTerminatedIntent(game)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}