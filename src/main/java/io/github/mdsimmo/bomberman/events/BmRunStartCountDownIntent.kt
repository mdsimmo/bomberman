package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

/**
 * Called whenever a run is attempted to be started
 */
class BmRunStartCountDownIntent private constructor(
        val game: Game,
        var delay: Int,
        val override: Boolean
    ): BmEvent(),
        IntentCancellable by BmIntentCancellable() {
    private var cancelReason: Message? = null

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    val cancelledReason: Message?
        get() = cancelReason

    fun cancelBecause(cancelReason: Message) {
        this.cancelReason = cancelReason
        isCancelled = true
    }

    companion object {
        @JvmStatic
        fun startGame(game: Game, delay: Int, override: Boolean): BmRunStartCountDownIntent {
            val e = BmRunStartCountDownIntent(game, delay, override)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
            return e
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}