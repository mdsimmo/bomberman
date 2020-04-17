package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called when a game is completely deleted from the server
 */
class BmGameDeletedIntent private constructor(val game: Game) : BmEvent(), IntentCancellable by BmIntentCancellable() {

    var isDeletingSave = false

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun delete(game: Game, deleteSave: Boolean) {
            val e = BmGameDeletedIntent(game)
            e.isDeletingSave = deleteSave
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}