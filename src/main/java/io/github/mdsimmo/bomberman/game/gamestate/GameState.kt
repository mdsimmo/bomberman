package io.github.mdsimmo.bomberman.game.gamestate

import io.github.mdsimmo.bomberman.game.GamePlayer
import io.github.mdsimmo.bomberman.messaging.Message
import org.bukkit.entity.Player

interface GameState {

    fun tryJoin(player: Player, success: (GamePlayer) -> Unit, fail: (Message) -> Unit) {

    }

    fun stop()

}
