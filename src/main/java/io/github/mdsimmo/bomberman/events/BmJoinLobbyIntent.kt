package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Lobby
import io.github.mdsimmo.bomberman.messaging.Message
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import java.lang.IllegalStateException


class BmJoinLobbyIntent private constructor(val lobby: Lobby, val player: Player) : BmEvent(),
    IntentCancellableReasoned by BmIntentCancellableReasoned() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun join(lobby: Lobby, player: Player): BmJoinLobbyIntent {
            val e = BmJoinLobbyIntent(lobby, player)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
            return e
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}