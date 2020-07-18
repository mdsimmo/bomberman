package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.GL
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

/**
 * Called whenever a player attempts to join a game. If there are not enough spawns in the game or the player cannot
 * afford entry, or ..., the event will be cancelled
 */
class BmPlayerJoinGLIntent private constructor(val gl: GL, val player: Player) : BmEvent(),
        IntentCancellableReasoned by BmIntentCancellableReasoned() {


    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun join(gl: GL, player: Player): BmPlayerJoinGLIntent {
            val e = BmPlayerJoinGLIntent(gl, player)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
            return e
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}