package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList


class BmJoinEnterGameAtSpawnIntent private constructor(val game: Game, val player: Player, val spawn: Location) : BmEvent(),
        IntentCancellableReasoned by BmIntentCancellableReasoned() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        fun join(game: Game, player: Player, spawn: Location): BmJoinEnterGameAtSpawnIntent {
            val e = BmJoinEnterGameAtSpawnIntent(game, player, spawn)
            Bukkit.getPluginManager().callEvent(e)
            e.verifyHandled()
            return e
        }

        @JvmStatic
        val handlerList = HandlerList()
    }

}