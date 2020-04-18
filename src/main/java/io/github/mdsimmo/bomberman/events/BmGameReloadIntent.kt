package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

class BmGameReloadIntent private constructor(val game: Game) : BmEvent(), Intent by BmIntent() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun reload(game: Game) {
            val e = BmGameReloadIntent(game)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}