package io.github.mdsimmo.bomberman

import io.github.mdsimmo.bomberman.events.BmGameListIntent
import io.github.mdsimmo.bomberman.events.BmGameLookupIntent
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.SenderWrapper
import io.github.mdsimmo.bomberman.messaging.SimpleContext
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class BmPlaceholder : PlaceholderExpansion() {

    override fun getIdentifier(): String = "bomberman"

    override fun getAuthor(): String = "mdsimmo"

    override fun getVersion(): String = "internal"

    override fun persist(): Boolean = true

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        val content = params.split('_')
        when (content.getOrNull(0)) {
            "info" -> {
                val gameName = content.getOrNull(1) ?: return "info <name> <stat>"
                val game = BmGameLookupIntent.find(gameName)
                return game?.format(content.drop(2).map { Message.of(it) })?.toString() ?: ""
            }
            "msg" -> {
                return try {
                    SimpleContext(params.substring("msg_".length))
                        .with("games", BmGameListIntent.listGames())
                        .let {
                            if (player != null)
                                it.with("player", SenderWrapper(player))
                            else
                                it
                        }
                        .format()
                        .toString()
                } catch (e: RuntimeException) {
                    Message.error(e.message ?: "Error").toString()
                }
            }
            else -> return "<info|msg> ..."
        }
    }


}