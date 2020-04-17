package io.github.mdsimmo.bomberman.events

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class BmPlayerLeaveGameIntent private constructor(val player: Player) : BmEvent(), Intent by BmIntent() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun leave(player: Player): BmPlayerLeaveGameIntent {
            val leave = BmPlayerLeaveGameIntent(player)
            Bukkit.getPluginManager().callEvent(leave)
            // Leave event may not be handled if player was not joined
            return leave
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}