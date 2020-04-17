package io.github.mdsimmo.bomberman.events

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * Event that occurs whenever a player is standing on a bomb. Will be called every tick that the player remains on the
 * bomb
 */
class BmPlayerHitIntent private constructor(val player: Player, val cause: Player) : BmEvent(),
        IntentCancellable by BmIntentCancellable() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun hit(player: Player, cause: Player) {
            val onBombEvent = BmPlayerHitIntent(player, cause)
            Bukkit.getPluginManager().callEvent(onBombEvent)
            onBombEvent.verifyHandled()
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}