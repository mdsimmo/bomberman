package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Lobby
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called when a game is completely deleted from the server
 */
class BmLobbyTerminatedIntent private constructor(
        val lobby: Lobby,
        val isDeletingSave: Boolean = false
    ) : BmEvent(), Intent by BmIntent() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun delete(lobby: Lobby, deleteSave: Boolean) {
            val e = BmLobbyTerminatedIntent(lobby, deleteSave)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}