package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.*
import io.github.mdsimmo.bomberman.messaging.Text
import kotlin.jvm.JvmStatic
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class LobbyPlayer(private val player: Player, private val lobby: Lobby) : Listener {

    companion object {
        private val plugin: Bomberman = Bomberman.instance

        @JvmStatic
        fun spawnLobbyPlayer(player: Player, lobby: Lobby, start: Location) {

            // Initialise the player for the game's lobby
            val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!
            maxHealth.modifiers.forEach { maxHealth.removeModifier(it) }
            player.teleport(start)
            player.gameMode = GameMode.ADVENTURE
            player.exhaustion = 0f
            player.foodLevel = 100000 // just a big number
            player.isFlying = false
            player.inventory.clear()
            for ((i, stack) in lobby.settings.initialItems.withIndex()) {
                if (i < player.inventory.size)
                    player.inventory.setItem(i, stack?.clone())
            }

            // Add tag for customisation
            player.addScoreboardTag("bm_lobby")

            // Create the player
            val gamePlayer = LobbyPlayer(player, lobby)
            plugin.server.pluginManager.registerEvents(gamePlayer, plugin)
        }
    }

    private val joinTime = System.currentTimeMillis()

    private fun resetStuffAndUnregister() {
        PlayerStateSaver.restore(player)
        unregister()
    }

    private fun unregister() {
        HandlerList.unregisterAll(this)
        player.removeScoreboardTag("bm_lobby")
    }

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        if (e.player != player)
            return
        if (!lobby.box.contains(e.to ?: e.from)) {
            BmPlayerLeaveIntent.leave(e.player)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onGameFindLobbyPlayers(e: BmJoinFindLobbyPlayersIntent) { // Cannot join two games at once
        if (e.lobby != lobby)
            return
        e.addPlayer(player, joinTime)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerJoinLobby(e:BmJoinGameIntent) {
        if (e.player != player)
            return
        e.cancelFor(Text.JOIN_ALREADY_JOINED
                .with("game", e.game)
                .with("player", player)
                .format())
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerJoinGame(e: BmJoinGameIntent) {
        if (e.player != player)
            return
        HandlerList.unregisterAll(this)
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerDamaged(e: EntityDamageEvent) {
        if (e.entity !== player)
            return
        // Player cannot be hurt while waiting
        if (lobby.settings.preventDamage) {
            e.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerLeaveGameEvent(e: BmPlayerLeaveIntent) {
        if (e.player != player)
            return

        // Give player their stuff back
        if (player.isDead) {
            // Attempting reset player health when dead causes very strange bugs.
            // Just let the loginWatcher handle it
            unregister()
        } else {
            resetStuffAndUnregister()
        }
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onLobbyTerminated(e: BmLobbyTerminatedIntent) {
        if (e.lobby != lobby)
            return
        BmPlayerLeaveIntent.leave(player)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onPlayerLogout(e: PlayerQuitEvent) {
        if (e.player == player) {
            BmPlayerLeaveIntent.leave(player)
        }
    }

}
