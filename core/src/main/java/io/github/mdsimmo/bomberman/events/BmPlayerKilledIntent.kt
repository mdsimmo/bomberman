package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * Called whenever a bm player is killed
 */
class BmPlayerKilledIntent private constructor(val game: Game, val player: Player, val attacker: Player)
    : BmEvent(), Intent by BmIntent() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun kill(game: Game, player: Player, cause: Player) {
            val e = BmPlayerKilledIntent(game, player, cause)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}