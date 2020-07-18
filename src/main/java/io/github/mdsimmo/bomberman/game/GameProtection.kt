package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.BmRunStoppedIntent
import io.github.mdsimmo.bomberman.utils.Box
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.plugin.Plugin

/**
 * Protects an arena from getting damaged from the game.
 *
 * It is up to Server Owners to protect the arena from griefers
 */
class GameProtection private constructor(private val game: Game, private val bounds: Box) : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onRunStopped(e: BmRunStoppedIntent) {
        if (e.game == game) {
            HandlerList.unregisterAll(this)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBlockBurn(e: BlockBurnEvent) {
        if (bounds.contains(e.block.location)) {
            e.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onBlockIgnite(e: BlockIgniteEvent) {
        if (e.cause == BlockIgniteEvent.IgniteCause.SPREAD
                && bounds.contains(e.block.location)) {
            e.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onFireSpread(e: BlockSpreadEvent) {
        if (bounds.contains(e.block.location)) {
            e.isCancelled = true
        }
    }

    companion object {
        private val plugin: Plugin = Bomberman.instance
        fun protect(game: Game, bounds: Box) {
            val protection = GameProtection(game, bounds)
            Bukkit.getPluginManager().registerEvents(protection, plugin)
        }
    }

}