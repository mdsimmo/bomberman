package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.GL
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called to find a game instance
 */
class BmGLLookupIntent(val name: String) : BmEvent() {
    var gl: GL? = null

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        fun find(name: String): GL? {
            val e = BmGLLookupIntent(name)
            Bukkit.getPluginManager().callEvent(e)
            return e.gl
        }

        @JvmStatic
        val handlerList = HandlerList()
    }
}