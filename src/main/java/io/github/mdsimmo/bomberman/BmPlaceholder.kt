package io.github.mdsimmo.bomberman

import io.github.mdsimmo.bomberman.events.BmGameListIntent
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.SimpleContext
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class BmPlaceholder : PlaceholderExpansion() {

    companion object {
        @JvmStatic
        fun register() {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                BmPlaceholder().register()
            }
        }
    }

    override fun getIdentifier(): String = "bomberman"

    override fun getAuthor(): String = "mdsimmo"

    override fun getVersion(): String = "internal"

    override fun persist(): Boolean = true

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        return try {
            SimpleContext(params)
                .with("games", BmGameListIntent.listGames())
                .format()
                .toString()
        } catch (e: RuntimeException) {
            Message.error(e.message ?: "Error").toString()
        }
    }
}