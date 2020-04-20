package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.BmExplosionEvent
import io.github.mdsimmo.bomberman.events.BmPlayerPlacedBombEvent
import io.github.mdsimmo.bomberman.events.BmRunStoppedIntent
import io.github.mdsimmo.bomberman.game.Explosion.BlockPlan
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class Bomb private constructor(
        private val game: Game,
        private val player: Player,
        private val block: Block,
        private val strength: Int,
        fuse: Long)
    : Listener {

    private var taskId: Int = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, { explode() }, fuse)
    private var noExplode = false

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onExplosion(e: BmExplosionEvent) {
        if (e.game != game) return
        if (!noExplode && e.igniting.any { b: BlockPlan -> b.block == block }) { // explode one tick latter
            Bukkit.getScheduler().cancelTask(taskId)
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(Bomberman.instance) { explode() }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onRunStopped(e: BmRunStoppedIntent) {
        if (e.game != game) return
        Bukkit.getScheduler().cancelTask(taskId)
        noExplode = true
    }

    private fun explode() {
        HandlerList.unregisterAll(this)
        // The ran flag prevents the tnt from exploding itself twice
        if (noExplode) {
            return
        }
        noExplode = true
        Explosion.spawnExplosion(game, block.location, player, strength)
    }

    companion object {
        private val plugin: Plugin = Bomberman.instance

        @JvmStatic
        fun spawnBomb(game: Game, player: Player, b: Block): Boolean {
            val tntPlaceEvent = BmPlayerPlacedBombEvent(game, player, b, game.settings.fuse)
            Bukkit.getPluginManager().callEvent(tntPlaceEvent)
            if (tntPlaceEvent.isCancelled)
                return false
            val bomb = Bomb(game, player, b, tntPlaceEvent.strength, tntPlaceEvent.fuse)
            Bukkit.getPluginManager().registerEvents(bomb, plugin)
            return true
        }
    }
}