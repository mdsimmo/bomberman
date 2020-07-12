package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import java.lang.IllegalStateException


class BmJoinGameIntent private constructor(val game: Game, val player: Player) : BmEvent(),
    Cancellable by BmCancellable() {

    var message: Message? = null

    fun successOf(reason: Message) {
        this.message = reason
    }

    fun cancelFor(reason: Message) {
        this.message = reason
        isCancelled = true
    }

    fun verifyHandled() {
        if (message == null && !isCancelled) {
            throw IllegalStateException("Event was not handled")
        }
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun join(game: Game, player: Player): BmJoinGameIntent {
            val e = BmJoinGameIntent(game, player)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
            return e
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}