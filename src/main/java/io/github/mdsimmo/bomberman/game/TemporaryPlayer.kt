package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.io.File

class TemporaryPlayer private constructor(private val player: Player) : Listener {

    companion object {
        private val plugin: Bomberman = Bomberman.instance

        @JvmStatic
        fun savePlayer(player: Player) {
            // Create the player to store data
            val tempPlayer = TemporaryPlayer(player)
            plugin.server.pluginManager.registerEvents(tempPlayer, plugin)

            // Record the player stats in file. Use a file so that server can crash
            val dataFile = YamlConfiguration()
            dataFile["location"] = player.location
            dataFile["gamemode"] = player.gameMode.name.toLowerCase()
            dataFile["health"] = player.health
            dataFile["health-scale"] = player.healthScale
            dataFile["health-max"] = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue
            dataFile["food-level"] = player.foodLevel
            dataFile["inventory"] = player.inventory.contents.toList()
            dataFile["is-flying"] = player.isFlying
            dataFile.save(tempDataFile(player))

            // Make the player a "blank slate"
            player.gameMode = GameMode.SURVIVAL
            player.exhaustion = 0f
            player.foodLevel = 100000 // just a big number
            player.isFlying = false
            player.inventory.clear()
            removePotionEffects(player)

            // Add tag for customisation
            player.addScoreboardTag("bm_player")
        }

        @JvmStatic
        fun setupLoginWatcher() {
            Bukkit.getPluginManager().registerEvents(object: Listener {
                @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
                fun onPlayerLogin(e: PlayerJoinEvent) {
                    val player = e.player
                    if (player.isDead)
                        return // cannot reset a dead player (client side glitches out)
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

        fun reset(player: Player): Location {
            // Give player StuffBack
            val file =  tempDataFile(player)
            val dataFile = YamlConfiguration.loadConfiguration(file)

            ((dataFile["gamemode"] as? String?)?.let { try {
                GameMode.valueOf(it.toUpperCase())
            } catch (e: IllegalArgumentException) {
                null
            }} ?: GameMode.SURVIVAL).let { player.gameMode = it }
            (dataFile["health-scale"] as? Number? ?: 20).let { player.healthScale = it.toDouble() }
            val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
            maxHealth?.baseValue = (dataFile["health-max"] as? Number? ?: 20.0).toDouble()
            maxHealth?.modifiers?.forEach { maxHealth.removeModifier(it) }
            (dataFile["health"] as? Number? ?: 20).let { player.health = it.toDouble() }
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
            file.delete()

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

        private fun tempDataFile(player: Player): File {
            return File(plugin.settings.tempPlayerData(), "${player.name}.yml")
        }
    }
}