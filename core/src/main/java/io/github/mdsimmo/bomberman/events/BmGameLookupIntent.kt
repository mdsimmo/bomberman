package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called to find a game instance
 */
class BmGameLookupIntent(val name: String) : BmEvent() {
    var game: Game? = null

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        fun find(name: String): Game? {
            val e = BmGameLookupIntent(name)
            Bukkit.getPluginManager().callEvent(e)
            return e.game
        }

        @JvmStatic
        val handlerList = HandlerList()
    }
}