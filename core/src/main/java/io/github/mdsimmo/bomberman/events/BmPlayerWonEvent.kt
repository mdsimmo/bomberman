package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class BmPlayerWonEvent(val game: Game, val player: Player) : BmEvent() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

}