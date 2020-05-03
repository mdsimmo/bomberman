package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class BmJoinFindLobbyPlayersIntent(val game: Game) : BmEvent() {

    private val players: MutableMap<Player, Long> = HashMap()

    fun addPlayer(player: Player, joinTick: Long) {
        players[player] = joinTick
    }

    fun players(): List<Player> {
        return players.toList()
                .sortedBy { it.second }
                .map { it.first }
                .toList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

}