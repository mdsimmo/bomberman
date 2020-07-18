package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.GL
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class BmPlayerLeaveGLIntent private constructor(val player: Player) : BmEvent(), Intent by BmIntent() {

    var gl: GL? = null
        private set

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    fun setHandled(gl: GL) {
        this.gl = gl
        setHandled()
    }

    companion object {
        @JvmStatic
        fun leave(player: Player): BmPlayerLeaveGLIntent {
            val leave = BmPlayerLeaveGLIntent(player)
            Bukkit.getPluginManager().callEvent(leave)
            // Leave event may not be handled if player was not joined
            return leave
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}