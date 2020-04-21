package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.BmDropLootEvent
import io.github.mdsimmo.bomberman.events.BmExplosionEvent
import io.github.mdsimmo.bomberman.events.BmPlayerHitIntent.Companion.hit
import io.github.mdsimmo.bomberman.events.BmPlayerMovedEvent
import io.github.mdsimmo.bomberman.events.BmRunStoppedIntent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer
import java.util.stream.Collectors

class Explosion private constructor(
        private val game: Game,
        private val blocks: MutableSet<BlockPlan>,
        private val cause: Player)
    : Listener {

    data class BlockPlan(
            val block: Block,
            val prior: BlockState,
            val ignited: BlockState,
            val destroyed: BlockState
    )

    private val taskId: Int = Bukkit.getScheduler().scheduleSyncDelayedTask(Bomberman.instance,
            { cleanup() }, game.settings.fireTicks.toLong())
    private var noExplode = false

    private fun cleanup() {
        if (noExplode) {
            HandlerList.unregisterAll(this)
            return
        }

        // Replace fire with air.
        // Note: Check if fire first because a player may have placed a block onto the space.
        // We cannot just use state.update(false) because the state was captured taken BEFORE the block was
        // converted into fire, so that would always do nothing
        blocks.forEach(Consumer { b: BlockPlan ->
            if (b.ignited.type == b.block.type) {
                b.destroyed.update(true)
            }
        })

        // Give player back their TNT
        // Check for tag in case they have left the game already
        if (cause.scoreboardTags.contains("bm_player"))
            cause.inventory.addItem(ItemStack(game.settings.bombItem, 1))

        // Drop loot
        val dropsPlaned = planDrops()
        val lootEvent = BmDropLootEvent(game, cause, blocks, dropsPlaned)
        Bukkit.getPluginManager().callEvent(lootEvent)
        if (!lootEvent.isCancelled) {
            lootEvent.drops.forEach { (location: Location, items: Set<ItemStack>) ->
                items.forEach(Consumer { item: ItemStack ->
                    if (item.amount > 0) {
                        location.world?.dropItem(location.clone().add(0.5, 0.5, 0.5), item)
                    }
                })
            }
        }

        // Delete this obj from memory
        HandlerList.unregisterAll(this)
    }

    private fun planDrops(): Map<Location, Set<ItemStack>> {
        val loot = game.settings.blockLoot
        return blocks
                .map { b: BlockPlan ->
                    Pair(
                            b.block.location,
                            lootSelect(loot[b.prior.type] ?: emptyMap())
                    )
                }.toMap()
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerMove(e: BmPlayerMovedEvent) {
        if (e.game != game) return
        if (isTouching(e.player, blocks.stream().map { b: BlockPlan -> b.block }.collect(Collectors.toSet()))) {
            hit(e.player, cause)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onAnotherExplosion(e: BmExplosionEvent) { // Don't double remove blocks
        blocks.removeIf { thisBlock: BlockPlan ->
            e.igniting
                    .any { it.block == thisBlock.block }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onRunStopped(e: BmRunStoppedIntent) {
        if (e.game != game) return

        Bukkit.getScheduler().cancelTask(taskId)
        HandlerList.unregisterAll(this)
        noExplode = true // need noExplode because sometimes tasks don't cancel?!?!
    }

    companion object {

        @JvmStatic
        fun spawnExplosion(game: Game, center: Location, cause: Player, strength: Int): Boolean {
            // Find where the explosion should expand to
            val firePlanned = planFire(center, game, strength)
            val plannedTypes = firePlanned
                    .map { b: Block ->
                        val prior = b.state
                        val ignited = b.state
                        if (!game.settings.passKeep.contains(b.type))
                            ignited.type = game.settings.fireType
                        val converted = b.state
                        if (!game.settings.passKeep.contains(b.type) && !game.settings.passRevert.contains(b.type))
                            converted.type = Material.AIR
                        BlockPlan(b, prior, ignited, converted)
                    }
                    .toSet()

            // Let others know
            val event = BmExplosionEvent(game, cause, plannedTypes)
            Bukkit.getPluginManager().callEvent(event)
            if (event.isCancelled)
                return false

            // Save the current state of all exploding blocks (for drops generation latter)
            val igniting = event.igniting
            igniting.forEach {
                it.ignited.update(true)
            }
            center.world?.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1f,Math.random().toFloat() + 0.5f)

            // Add an explosion obj to handle cleanup and watch for kills
            val explosion = Explosion(game, igniting, cause)
            Bukkit.getPluginManager().registerEvents(explosion, Bomberman.instance)
            return true
        }

        @JvmStatic
		fun isTouching(player: Player, blocks: Set<Block>): Boolean {
            return blocks.any { b: Block ->
                // TODO why not just listen for onPlayerDamage with FIRE_TICK type?
                val margin = 0.295 // magical value that seems to be how far fire burns players
                val l = player.location
                val min = b.location.add(0.0, -1.0, 0.0)
                val max = b.location.add(1.0, 2.0, 1.0)
                l.x >= min.x - margin && l.x <= max.x + margin
                        && l.y >= min.y - margin && l.y <= max.y + margin
                        && l.z >= min.z - margin && l.z <= max.z + margin
            }
        }

        /**
         * creates fire in the '+' pattern
         */
        private fun planFire(center: Location, game: Game, strength: Int): Set<Block> {
            val blocks = mutableSetOf<Block>()
            // arms
            blocks.addAll(planFire(center, game, strength, 0, 1))
            blocks.addAll(planFire(center, game, strength, 0, -1))
            blocks.addAll(planFire(center, game, strength, 1, 0))
            blocks.addAll(planFire(center, game, strength, -1, 0))
            // center column
            for (i in -1..1) {
                planFire(center, game, 0, i, 0, blocks)
            }
            return blocks
        }

        /**
         * creates a line of fire in the given x, z direction;
         *
         * @param xstep the unit to step in the x direction
         * @param zstep the unit to step in the z direction
         */
        private fun planFire(center: Location, game: Game, strength: Int, xstep: Int, zstep: Int): Set<Block> {
            val blocks = mutableSetOf<Block>()
            for (i in 1..strength) {
                planFire(center, game, i * xstep, 1, i * zstep, blocks)
                planFire(center, game, i * xstep, -1, i * zstep, blocks)
                if (planFire(center, game, i * xstep, 0, i * zstep, blocks)) return blocks
            }
            return blocks
        }

        /**
         * creates fire at the given location if it can.
         *
         * @return true if the fire-ball should stop
         */
        private fun planFire(center: Location, game: Game, x: Int, y: Int, z: Int, blocks: MutableSet<Block>): Boolean {
            val l = center.clone().add(z.toDouble(), y.toDouble(), x.toDouble())
            val b = l.block

            // Pass through air/weak things
            if (isPassing(b, game.settings)) {
                blocks.add(b)
                return false
            }

            // If hit destructible, blow up one block and then stop
            if (game.settings.destructible.contains(b.type))
                blocks.add(b)
            return true
        }

        private fun isPassing(b: Block, settings: GameSettings): Boolean {
            val t = b.type
            return t == Material.AIR || t == settings.fireType
                    || (b.isPassable && !(settings.indestructible.contains(t) || settings.destructible.contains(t)))
                    || settings.passDestroy.contains(t)
                    || settings.passKeep.contains(t)
                    || settings.passRevert.contains(t)
        }

        @JvmStatic
		fun <T> lootSelect(loot: Map<out T, Number>): Set<T> {
            // Sum the weights
            var sum = loot.values
                    .sumByDouble(Number::toDouble)

            // Select the item based off weights
            for ((item, value) in loot) {
                val weight = value.toDouble()
                if (sum * Math.random() <= weight) {
                    return setOf(item)
                }
                sum -= weight
            }

            // Handle possible empty set
            return if (sum == 0.0)
                emptySet()
            else
                throw RuntimeException("Explosion.drop didn't select (should never happen)")
        }
    }
}