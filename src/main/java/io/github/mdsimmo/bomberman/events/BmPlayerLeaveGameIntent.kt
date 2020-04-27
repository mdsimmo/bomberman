package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class BmPlayerLeaveGameIntent private constructor(val player: Player) : BmEvent(), Intent by BmIntent() {

    var game: Game? = null
        private set

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    fun setHandled(game: Game) {
        this.game = game
        setHandled()
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