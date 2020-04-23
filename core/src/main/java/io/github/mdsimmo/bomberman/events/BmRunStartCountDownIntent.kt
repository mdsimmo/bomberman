package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called whenever a run is attempted to be started
 */
class BmRunStartCountDownIntent private constructor(val game: Game, var delay: Int): BmEvent(),
        IntentCancellableReasoned by BmIntentCancellableReasoned() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun startGame(game: Game, delay: Int): BmRunStartCountDownIntent {
            val e = BmRunStartCountDownIntent(game, delay)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
            return e
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}