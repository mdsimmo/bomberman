package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * Called whenever a bm player takes damage.
 */
class BmPlayerHurtIntent(val game: Game, val player: Player, val attacker: Player) : BmEvent(),
        IntentCancellable by BmIntentCancellable() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun run(game: Game, player: Player, cause: Player) {
            val hurtEvent = BmPlayerHurtIntent(game, player, cause)
            Bukkit.getPluginManager().callEvent(hurtEvent)
            hurtEvent.verifyHandled()
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}