package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called to find a listing of every active game
 */
class BmGameListIntent : BmEvent() {
    val games = mutableSetOf<Game>()

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        fun listGames(): Set<Game> {
            val e = BmGameListIntent()
            Bukkit.getPluginManager().callEvent(e)
            return e.games
        }

        @JvmStatic
        val handlerList = HandlerList()
    }
}