package io.github.mdsimmo.bomberman.game

import com.sun.org.apache.xpath.internal.operations.Bool
import io.github.mdsimmo.bomberman.utils.RefectAccess
import org.bukkit.GameMode
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import java.lang.IllegalArgumentException

data class LobbySettings(
        var autostart: Boolean = true,
        var autostartDelay: Int = 10,
        var autostartJoinAt: Int = 3,
        var fastforward: Boolean = true,
        var fastforwardDelay: Int = 3,
        var minPlayers: Int = 2,
        var preventDamage: Boolean = true,
        var gamemode: GameMode = GameMode.ADVENTURE,
        var initialItems: List<ItemStack?> = listOf()
) : ConfigurationSerializable {

    companion object {
        @RefectAccess
        @JvmStatic
        fun deserialize(data: Map<String?, Any?>): LobbySettings {
            val settings = LobbySettings()
            (data["autostart.enabled"] as? Boolean)?.also { settings.autostart = it }
            (data["autostart.delay"] as? Int)?.also { settings.autostartDelay = it.coerceAtLeast(0) }
            (data["autostart.join-at-count"] as? Int)?.also { settings.autostartJoinAt = it.coerceIn(0, settings.autostartDelay) }
            (data["autostart.min-players"] as? Int)?.also { settings.minPlayers = it.coerceAtLeast(1) }
            (data["autostart.fastforward.enabled"] as? Boolean)?.also { settings.fastforward = it }
            (data["autostart.fastforward.delay"] as? Int)?.also { settings.fastforwardDelay = it.coerceIn(0, settings.autostartDelay) }
            (data["prevent-damage"] as? Boolean)?.also { settings.preventDamage = it }
            (data["gamemode"] as? String)
                    ?.let { try { GameMode.valueOf(it.toUpperCase()) } catch (e: IllegalArgumentException) { null } }
                    ?.also { settings.gamemode = it }
            (data["initial-items"] as? List<*>)
                    ?.map { it as? ItemStack }
                    ?.also {
                        settings.initialItems = it
                    }
            return settings
        }
    }

    override fun serialize(): Map<String, Any> {
        val objs: MutableMap<String, Any> = HashMap()
        objs["autostart.enabled"] = autostart
        objs["autostart.delay"] = autostartDelay
        objs["autostart.minplayers"] = minPlayers
        objs["autostart.join-at-count"] = autostartJoinAt
        objs["prevent-damage"] = preventDamage
        objs["gamemode"] = gamemode.name.toLowerCase()
        objs["initial-items"] = initialItems.dropLastWhile { it == null }
        return objs
    }
}