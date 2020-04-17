package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.game.GamePlayer
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList

/**
 * Called when a player places down a block of TNT (or whatever the game configured as the tnt block). Cancelling the
 * event will remove the tnt from the ground as if the player never clicked
 */
class BmPlayerPlacedBombEvent(val game: Game, val player: Player, val block: Block) : BmEvent(),
        Cancellable by BmCancellable() {

    val strength: Int = GamePlayer.bombStrength(game, player)

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

}