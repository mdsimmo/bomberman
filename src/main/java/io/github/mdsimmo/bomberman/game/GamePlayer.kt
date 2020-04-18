package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.*
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class GamePlayer private constructor(private val player: Player, private val game: Game) : Listener {

    companion object {
        private val plugin: Plugin = Bomberman.instance

        @JvmStatic
        fun spawnGamePlayer(player: Player, game: Game, start: Location) {
            // Create the player to store data
            val gamePlayer = GamePlayer(player, game)
            plugin.server.pluginManager.registerEvents(gamePlayer, plugin)


            // Clear the landing area of items (there should never be any, but we don't want no cheating)
            start.world!!.getNearbyEntities(start, 2.0, 3.0, 2.0)
                    .filterIsInstance<Item>()
                    .forEach{ it.remove() }


            // Initialise the player for the game
            player.teleport(start.clone().add(0.5, 0.5, 0.5))
            player.gameMode = GameMode.SURVIVAL
            player.health = game.settings.lives.toDouble()
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = game.settings.lives.toDouble()

            // if setHealthScale is not delayed, it can sometimes cause the client side to think they died?!?!?
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                player.healthScale = game.settings.lives * 2.toDouble()
            }
            player.exhaustion = 0f
            player.foodLevel = 100000 // just a big number
            player.isFlying = false
            player.inventory.clear()
            for (stack in game.settings.initialItems) {
                val s = stack.clone()
                player.inventory.addItem(s)
            }
            gamePlayer.removePotionEffects()

            // Add tag for customisation
            player.addScoreboardTag("bm_player")
        }

        fun bombStrength(game: Game, player: Player): Int {
            var strength = 0
            for (stack in player.inventory.contents) {
                if (stack != null && stack.type == game.settings.powerItem) {
                    strength += stack.amount
                }
            }
            return strength.coerceAtLeast(1)
        }
    }

    private var immunity = false
    private val spawnInventory: Array<ItemStack>  = player.inventory.contents
    private val spawn: Location = player.location
    private val spawnHunger: Int = player.foodLevel
    private val spawnGameMode: GameMode = player.gameMode
    private val spawnHealth: Double = player.health
    private val spawnMaxHealth: Double = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue
    private val spawnHealthScale: Double = player.healthScale
    private val isFlying = player.isFlying

    /**
     * Removes the player from the game and removes any hooks to this player. Treats the player like they disconnected
     * from the server.
     */
    private fun resetStuffAndUnregister() {
        // remove items in the direct vicinity (prevents player dropping items at spawn)
        player.world.getNearbyEntities(player.location, 1.0, 2.0, 1.0).stream()
                .filter { it is ItemStack }
                .forEach { it.remove() }

        reset()
        HandlerList.unregisterAll(this)
    }

    private fun reset() {
        // Give player StuffBack
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = spawnMaxHealth
        player.healthScale = spawnHealthScale
        player.health = spawnHealth
        player.teleport(spawn)
        player.gameMode = spawnGameMode
        player.inventory.contents = spawnInventory
        player.foodLevel = spawnHunger
        player.isFlying = isFlying
        player.removeScoreboardTag("bm_player")
        removePotionEffects()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerJoinGame(e: BmPlayerJoinGameIntent) { // Cannot join two games at once
        if (e.player === player) {
            e.cancelFor(Text.JOIN_ALREADY_JOINED
                    .with("game", e.game)
                    .with("player", player)
                    .format())
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onCount(e: BmTimerCountedEvent) {
        if (e.game != game)
            return
        Text.GAME_COUNT
                .with("time", e.count)
                .with("game", game)
                .sendTo(player)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onRunStarted(e: BmRunStartedIntent) {
        if (e.game != game)
            return
        Text.GAME_STARTED
                .with("game", game)
                .sendTo(player)
        e.setHandled()
    }

    @EventHandler(priority = EventPriority.LOWEST)
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
            // Only care about block breaking events
            return
        }
        // TODO only let player break block if they have used the correct tool
        // Maybe use CanDestroy tag? - That requires NBT though...
        // Cannot break things with hand
        e.isCancelled = true
        e.setUseInteractedBlock(Event.Result.DENY)
        e.setUseItemInHand(Event.Result.DENY)
        // apply mining fatigue so player doesn't see block breaking
        e.player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 1))
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerPlaceBlock(e: BlockPlaceEvent) {
        if (e.player !== player) return
        val b = e.block
        // create a bomb when placing tnt
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
        // TODO duplicate code: both GamePlayer and Explosion do touching checks
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
            player.fireTicks = 22
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                immunity = false
                player.fireTicks = 0
                // Call the player move event to recheck damage required
                Bukkit.getPluginManager().callEvent(
                        BmPlayerMovedEvent(game, player, player.location, player.location))
            }, 22) // 22 is slightly longer than 20 ticks a bomb is active for
        } else {
            BmPlayerKilledIntent.kill(game, player, e.attacker)
        }
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onPlayerDamaged(e: EntityDamageEvent) {
        if (e.entity !== player)
            return
        // Allow custom damage events (ie. from our plugin)
        if (e.cause == EntityDamageEvent.DamageCause.CUSTOM)
            return
        // Player cannot be burnt or hurt during game play
        e.isCancelled = true
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
        Text.PLAYER_WON
                .with("player", player)
                .sendTo(player)
        // Let player walk around like a boss until the game stops
        immunity = true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerLeaveGameEvent(e: BmPlayerLeaveGameIntent) {
        if (e.player !== player)
            return

        // Remove items near the player (stops players duplicating items out at spawn)
        player.world.getNearbyEntities(player.location, 2.0, 3.0, 2.0)
                .filterIsInstance<Item>()
                .forEach{ it.remove() }

        // Give player their stuff back
        if (player.isDead) {
            // Attempting reset player health when dead causes very strange bugs. So wait to respawned
            Bukkit.getPluginManager().registerEvents(object: Listener {
                @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
                fun onPlayerRespawn(e: PlayerRespawnEvent) {
                    e.respawnLocation = spawn
                    reset()
                    HandlerList.unregisterAll(this)
                }
            }, plugin)
            HandlerList.unregisterAll(this)
        } else {
            resetStuffAndUnregister()
        }
        e.setHandled()
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerLogout(e: PlayerQuitEvent) {
        if (e.player === player) {
            BmPlayerLeaveGameIntent.leave(player)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onGameStopped(e: BmRunStoppedIntent) {
        if (e.game != game)
            return
        BmPlayerLeaveGameIntent.leave(player)
    }

    private fun removePotionEffects() {
        val server = player.server
        if (plugin.isEnabled) server.scheduler
                .scheduleSyncDelayedTask(plugin) {
                    player.fireTicks = 0
                    for (effect in player.activePotionEffects) {
                        player.removePotionEffect(effect.type)
                    }
                }
    }
}