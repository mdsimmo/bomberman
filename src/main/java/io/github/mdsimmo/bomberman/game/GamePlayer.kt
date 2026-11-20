package io.github.mdsimmo.bomberman.game

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.registry.TypedKey
import org.bukkit.block.BlockType
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.*
import io.github.mdsimmo.bomberman.messaging.Context
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.nio.file.Path
import java.util.logging.Level
import kotlin.io.path.*

class GamePlayer private constructor(private val player: Player, private val game: Game) : Listener {

    companion object {
        private val plugin: Bomberman = Bomberman.instance

        @JvmStatic
        fun spawnGamePlayer(player: Player, game: Game, start: Location) {
            val gamePlayer = GamePlayer(player, game)
            plugin.server.pluginManager.registerEvents(gamePlayer, plugin)

            val dataFile = YamlConfiguration()
            dataFile["location"] = player.location
            dataFile["gamemode"] = player.gameMode.name.lowercase()
            dataFile["health"] = player.health
            dataFile["health-scale"] = player.healthScale
            dataFile["health-max"] = player.getAttribute(Attribute.MAX_HEALTH)!!.baseValue
            dataFile["food-level"] = player.foodLevel
            dataFile["inventory"] = player.inventory.contents.toList()
            dataFile["is-flying"] = player.isFlying
            tempDataFile(player).writer().use { it.write(dataFile.saveToString()) }

            try {
                player.addAttachment(plugin, "group.bomberman", true)
            } catch (e: Exception) {
                plugin.logger.log(Level.WARNING, "Unable to add permissions", e)
            }

            start.world!!.getNearbyEntities(start, 2.0, 3.0, 2.0)
                .filterIsInstance<Item>()
                .forEach { it.remove() }

            val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)!!
            maxHealth.baseValue = game.settings.lives.toDouble()
            maxHealth.modifiers.forEach { maxHealth.removeModifier(it) }
            player.health = game.settings.lives.toDouble()
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                player.healthScale = game.settings.lives * 2.toDouble()
            }
            if (!player.teleport(start.clone().add(0.5, 0.01, 0.5))) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                    BmPlayerLeaveGameIntent.leave(player)
                }
            }
            player.gameMode = GameMode.SURVIVAL
            player.exhaustion = 0f
            player.foodLevel = 100000
            player.isFlying = false
            player.inventory.clear()
            for ((i, stack) in game.settings.initialItems.withIndex()) {
                if (i < player.inventory.size)
                    player.inventory.setItem(i, stack?.clone())
            }
            removePotionEffects(player)

            player.addScoreboardTag("bm_player")
        }

        fun bombStrength(game: Game, player: Player): Int {
            var strength = 1
            for (stack in player.inventory.contents) {
                if (stack != null && stack.type == game.settings.powerItem) {
                    strength += stack.amount
                }
            }
            return strength.coerceAtLeast(1)
        }

        @JvmStatic
        fun setupLoginWatcher() {
            Bukkit.getPluginManager().registerEvents(object : Listener {
                @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
                fun onPlayerLogin(e: PlayerJoinEvent) {
                    val player = e.player
                    if (player.isDead)
                        return
                    val save = tempDataFile(player)
                    if (save.exists())
                        reset(player)
                }
                @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
                fun onPlayerRespawn(e: PlayerRespawnEvent) {
                    val player = e.player
                    val save = tempDataFile(player)
                    if (save.exists())
                        e.respawnLocation = reset(player)
                }
            }, plugin)
        }

        private fun reset(player: Player): Location {
            val file = tempDataFile(player)
            val dataFile = file.reader().use { YamlConfiguration.loadConfiguration(it) }

            ((dataFile["gamemode"] as? String?)?.let {
                try {
                    GameMode.valueOf(it.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            } ?: GameMode.SURVIVAL).let { player.gameMode = it }
            (dataFile["health-scale"] as? Number? ?: 20).let { player.healthScale = it.toDouble() }
            val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)!!
            maxHealth.baseValue = (dataFile["health-max"] as? Number? ?: 20.0).toDouble()
            maxHealth.modifiers.forEach { maxHealth.removeModifier(it) }
            (dataFile["health"] as? Number? ?: 20).let {
                player.health = it.toDouble().coerceAtMost(maxHealth.value)
            }
            (dataFile["food-level"] as? Number? ?: 20).let { player.foodLevel = it.toInt() }
            (dataFile["inventory"] as? List<Any?> ?: emptyList())
                .map {
                    if (it is ItemStack) it else null
                }.toTypedArray()
                .let { player.inventory.contents = it }
            (dataFile["is-flying"] as? Boolean? ?: false).let { player.isFlying = it }
            val location = (dataFile["location"] as? Location?
                ?: Bukkit.getServer().worlds.first().spawnLocation)
            player.teleport(location)

            player.removeScoreboardTag("bm_player")

            try {
                player.addAttachment(plugin, "group.bomberman", false)
            } catch (e: Exception) {
                plugin.logger.log(Level.WARNING, "Unable to remove permissions", e)
            }

            file.deleteIfExists()

            removePotionEffects(player)
            return location
        }

        private fun removePotionEffects(player: Player) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                player.fireTicks = 0
                for (effect in player.activePotionEffects) {
                    player.removePotionEffect(effect.type)
                }
            }
        }

        private fun tempDataFile(player: Player): Path {
            return plugin.tempPlayerData().resolve("${player.name}.yml")
        }
    }
    private var immunity = false

    private fun resetStuffAndUnregister() {
        player.world.getNearbyEntities(player.location, 1.0, 2.0, 1.0).stream()
            .filter { it is Item }
            .forEach { it.remove() }

        reset(player)
        HandlerList.unregisterAll(this)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerJoinGame(e: BmPlayerJoinGameIntent) {
        if (e.player === player) {
            e.cancelFor(Text.JOIN_ALREADY_JOINED.format(Context(false)
                .plus("game", e.game)
                .plus("player", player)))
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onCount(e: BmTimerCountedEvent) {
        if (e.game != game)
            return
        if (e.count > 0) {
            Text.GAME_COUNT.format(Context(false)
                .plus("time", e.count)
                .plus("game", game))
                .sendTo(player)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onRunStarted(e: BmRunStartedIntent) {
        if (e.game != game)
            return
        Text.GAME_STARTED.format(Context(false)
            .plus("game", game))
            .sendTo(player)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onPlayerRegen(e: EntityRegainHealthEvent) {
        if (e.entity != player)
            return
        if (e.regainReason == EntityRegainHealthEvent.RegainReason.MAGIC) {
            e.amount = 1.0
        } else {
            e.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerBreakBlockWithWrongTool(e: PlayerInteractEvent) {
        if (e.player !== player)
            return
        if (e.action != Action.LEFT_CLICK_BLOCK || !e.hasBlock()) {
            return
        }

        val clickedBlock = e.clickedBlock
        val item = e.item
        if (item != null && clickedBlock != null) {
            val canBreak = item.getData(DataComponentTypes.CAN_BREAK)
            if (canBreak != null && canBreak.predicates().any { predicate ->
                    predicate.blocks()?.contains(TypedKey.create(io.papermc.paper.registry.RegistryKey.BLOCK, clickedBlock.type.key)) == true
                }) {
                return
            }
        }


        e.isCancelled = true
        e.setUseInteractedBlock(Event.Result.DENY)
        e.setUseItemInHand(Event.Result.DENY)
        e.player.addPotionEffect(PotionEffect(PotionEffectType.MINING_FATIGUE, 20, 1))
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerPlaceBlock(e: BlockPlaceEvent) {
        if (e.player != player)
            return

        val canPlaceOn = e.itemInHand.getData(DataComponentTypes.CAN_PLACE_ON)
        if (canPlaceOn != null && canPlaceOn.predicates().none { predicate ->
                predicate.blocks()?.contains(TypedKey.create(io.papermc.paper.registry.RegistryKey.BLOCK, e.blockAgainst.type.key)) == true
            }) {
            e.isCancelled = true
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerPlaceTNT(e: BlockPlaceEvent) {
        if (e.player !== player) return
        val b = e.block
        if (b.type == game.settings.bombItem) {
            if (!Bomb.spawnBomb(game, player, b)) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerMoved(e: PlayerMoveEvent) {
        if (e.player !== player)
            return
        val bmEvent = BmPlayerMovedEvent(game, player, e.from, e.to ?: e.from)
        Bukkit.getPluginManager().callEvent(bmEvent)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onExplosion(e: BmExplosionEvent) {
        if (e.game != game)
            return
        if (Explosion.isTouching(player, e.igniting.map(Explosion.BlockPlan::block).toSet())) {
            BmPlayerHitIntent.hit(player, e.cause)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerHit(e: BmPlayerHitIntent) {
        if (e.player !== player)
            return
        BmPlayerHurtIntent.run(game, player, e.cause)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerHurtWithImmunity(e: BmPlayerHurtIntent) {
        if (e.player !== player)
            return
        if (immunity)
            e.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerDamaged(e: BmPlayerHurtIntent) {
        if (e.player !== player)
            return
        if (player.health > 1) {
            player.damage(1.0)
            immunity = true
            player.fireTicks = game.settings.immunityTicks
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                immunity = false
                player.fireTicks = 0
                Bukkit.getPluginManager().callEvent(
                    BmPlayerMovedEvent(game, player, player.location, player.location))
            }, game.settings.immunityTicks.toLong())
        } else {
            BmPlayerKilledIntent.kill(game, player, e.attacker)
        }
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerKilledInGame(e: BmPlayerKilledIntent) {
        if (e.player !== player) return
        player.health = 0.0
        BmPlayerLeaveGameIntent.leave(player)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerWon(e: BmPlayerWonEvent) {
        if (e.player !== player) return
        Text.PLAYER_WON.format(Context(false)
            .plus("player", player))
            .sendTo(player)
        immunity = true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerLeaveGameEvent(e: BmPlayerLeaveGameIntent) {
        if (e.player !== player)
            return

        player.world.getNearbyEntities(player.location, 2.0, 3.0, 2.0)
            .filterIsInstance<Item>()
            .forEach { it.remove() }

        if (player.isDead) {
            HandlerList.unregisterAll(this)
        } else {
            resetStuffAndUnregister()
        }
        e.setHandled(game)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onGameStopped(e: BmRunStoppedIntent) {
        if (e.game != game)
            return
        BmPlayerLeaveGameIntent.leave(player)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onGameTerminated(e: BmGameTerminatedIntent) {
        if (e.game != game)
            return
        BmPlayerLeaveGameIntent.leave(player)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerLogout(e: PlayerQuitEvent) {
        if (e.player == player) {
            BmPlayerLeaveGameIntent.leave(player)
        }
    }
}
