package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called when a game is completely deleted from the server
 */
class BmGameRebuildIntent private constructor(val game: Game) : BmEvent(), Intent by BmIntent() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun rebuild(game: Game) {
            val e = BmGameRebuildIntent(game)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}