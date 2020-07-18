package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.game.Lobby
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

class BmLobbyListGames(val lobby: String) : BmEvent() {
    val games = mutableSetOf<Game>()

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        fun listGames(lobby: String): Set<Game> {
            val e = BmLobbyListGames(lobby)
            Bukkit.getPluginManager().callEvent(e)
            return e.games
        }

        @JvmStatic
        val handlerList = HandlerList()
    }
}