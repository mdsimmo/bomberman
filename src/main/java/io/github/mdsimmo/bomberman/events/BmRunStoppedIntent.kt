package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called whenever a game run is stopped. May be due to game finishing, game forcefully stopped, server shutdown, etc.
 */
class BmRunStoppedIntent private constructor(val game: Game) : BmEvent(),
        IntentCancellableReasoned by BmIntentCancellableReasoned() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun stopGame(game: Game): BmRunStoppedIntent {
            val e = BmRunStoppedIntent(game)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
            return e
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}