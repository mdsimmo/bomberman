package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList


class BmJoinEnterGameIntent private constructor(val game: Game, val player: Player) : BmEvent(),
        IntentCancellableReasoned by BmIntentCancellableReasoned() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun join(game: Game, player: Player): BmJoinEnterGameIntent {
            val e = BmJoinEnterGameIntent(game, player)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
            return e
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}