package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.BmGLTerminatedIntent
import io.github.mdsimmo.bomberman.events.BmPlayerJoinGLIntent
import io.github.mdsimmo.bomberman.events.BmPlayerLeaveGLIntent
import io.github.mdsimmo.bomberman.events.BmPlayerMovedEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class LobbyPlayer private constructor(private val player: Player, private val lobby: Lobby) : Listener {

    companion object {

        private val plugin: Bomberman = Bomberman.instance

        @JvmStatic
        fun spawnLobbyPlayer(player: Player, lobby: Lobby, start: Location) {
            TemporaryPlayer.savePlayer(player)

            // Create the player to store data
            val lobbyPlayer = LobbyPlayer(player, lobby)
            plugin.server.pluginManager.registerEvents(lobbyPlayer, plugin)

            player.teleport(start.clone().add(0.5, 0.01, 0.5))
            player.gameMode = GameMode.ADVENTURE
        }
    }

    private fun resetStuffAndUnregister() {
        TemporaryPlayer.reset(player)
        HandlerList.unregisterAll(this)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerBreakBlock(e: PlayerInteractEvent) {
        if (e.player !== player)
            return
        if (e.action != Action.LEFT_CLICK_BLOCK || !e.hasBlock()) {
            // Only care about block breaking events
            return
        }
        e.isCancelled = true
        e.setUseInteractedBlock(Event.Result.DENY)
        e.setUseItemInHand(Event.Result.DENY)
        // apply mining fatigue so player doesn't see block breaking
        e.player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 1))
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerMoved(e: PlayerMoveEvent) {
        if (e.player !== player)
            return
        val bmEvent = BmPlayerMovedEvent(lobby, player, e.from, e.to ?: e.from)
        Bukkit.getPluginManager().callEvent(bmEvent)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
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
    fun onPlayerJoinGameEvent(e: BmPlayerJoinGLIntent) {
        if (e.player !== player)
            return
        resetStuffAndUnregister()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerLeaveLobbyEvent(e: BmPlayerLeaveGLIntent) {
        if (e.player !== player)
            return

        resetStuffAndUnregister()
        e.setHandled(lobby)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameTerminated(e: BmGLTerminatedIntent) {
        if (e.gl != lobby)
            return
        BmPlayerLeaveGLIntent.leave(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerLogout(e: PlayerQuitEvent) {
        if (e.player == player) {
            BmPlayerLeaveGLIntent.leave(player)
        }
    }
}