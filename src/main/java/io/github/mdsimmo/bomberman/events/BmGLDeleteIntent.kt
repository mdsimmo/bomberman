package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.GL
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called when a game is completely deleted from the server
 */
class BmGLDeleteIntent private constructor(val gl: GL)
    : BmEvent(), IntentCancellable by BmIntentCancellable() {

    var isDeletingSave = false

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun delete(gl: GL, deleteSave: Boolean) {
            val e = BmGLDeleteIntent(gl)
            e.isDeletingSave = deleteSave
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}