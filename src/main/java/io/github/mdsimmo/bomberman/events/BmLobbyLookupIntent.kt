package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Lobby
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called to find a game instance
 */
class BmLobbyLookupIntent(val name: String) : BmEvent() {
    var lobby: Lobby? = null

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        fun find(name: String): Lobby? {
            val e = BmLobbyLookupIntent(name)
            Bukkit.getPluginManager().callEvent(e)
            return e.lobby
        }

        @JvmStatic
        val handlerList = HandlerList()
    }
}