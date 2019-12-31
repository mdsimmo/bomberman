package io.github.mdsimmo.bomberman.game.gamestate

import io.github.mdsimmo.bomberman.game.GamePlayer
import io.github.mdsimmo.bomberman.game.GameSettings
import io.github.mdsimmo.bomberman.messaging.Message
import org.bukkit.entity.Player

import java.util.ArrayList

class GameRun {

    private var state: GameState
    private val settings: GameSettings

    private val players: ArrayList<GamePlayers> = arrayOf()

    constructor(settings: GameSettings) {
        state = GameWaitingState()
        this.settings = settings
    }

    fun start() {
        state = GameStartingState()
    }

    fun stop() {
        state = GameWaitingState()

        for (player in ArrayList<Any>(players)) {
            player.removeFromGame()
        }
    }

    fun tryJoin(player: Player, success: (GamePlayer) -> Unit, fail: (Message) -> Unit) {
        state.tryJoin(player, success, fail)
    }

}
