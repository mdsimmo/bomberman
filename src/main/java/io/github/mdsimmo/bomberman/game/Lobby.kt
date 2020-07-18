package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.*
import io.github.mdsimmo.bomberman.messaging.CollectionWrapper
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.SenderWrapper
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import java.io.File

class Lobby private constructor(
        val name: String,
        private val location: Location,
        private val radius: Int
): Listener, GL, Formattable {

    companion object {
        private val plugin = Bomberman.instance

        @JvmStatic
        fun loadLobbies() {
            val data = plugin.settings.lobbySaves()
            val files = data
                    .listFiles { _, name ->
                        name.endsWith(".yml")
                    }
                    ?: return
            for (f in files) {
                loadLobby(f)
            }
        }

        fun loadLobby(file: File): Lobby? {
            val data = YamlConfiguration.loadConfiguration(file)
            val name = data["name"] as? String ?: return null
            val location = data["location"] as? Location ?: return null
            val radius = data["radius"] as? Number ?: return null
            return create(name, location, radius.toInt())
        }

        fun saveGame(lobby: Lobby) {
            val file = YamlConfiguration()
            file.set("name", lobby.name)
            file.set("location", lobby.location)
            file.set("radius", lobby.radius)
            file.save(File(plugin.settings.lobbySaves(), "${lobby.name}.yml"))
        }

        fun create(lobbyName: String, location: Location, radius: Int): Lobby {
            val lobby = Lobby(lobbyName, location, radius)
            Bukkit.getPluginManager().registerEvents(lobby, plugin)
            saveGame(lobby)
            return lobby
        }
    }

    private var lastSelected: Game? = null
    private val playersWaiting = mutableListOf<Player>()

    @EventHandler(ignoreCancelled = true)
    fun onListGLs(e: BmJoinableListIntent) {
        e.gls += this
    }

    @EventHandler(ignoreCancelled = true)
    fun onLookup(e: BmGLLookupIntent) {
        if (e.name.equals(name, ignoreCase = true))
            e.gl = this
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerJoinLobby(e: BmPlayerJoinGLIntent) {
        if (e.gl != this)
            return

        val nextGame = findNextGame()
        println(nextGame)
        if (nextGame != null) {
            val joinEvent = BmPlayerJoinGLIntent.join(nextGame, e.player)
            joinEvent.cancelledReason()?.also {
                e.cancelFor(it)
            }
        } else {
            playersWaiting += e.player
            LobbyPlayer.spawnLobbyPlayer(e.player, this, location)
        }
        e.setHandled()
    }

    private fun findNextGame(): Game? {
        val games = BmLobbyListGames.listGames(name)
        println(games)
        if (games.isEmpty()) {
            lastSelected = null
            return null
        }
        if (games.contains(lastSelected))
            return lastSelected
        return games.random().also {
            lastSelected = it
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerJoinGame(e: BmPlayerJoinGLIntent) {
        if (e.gl != this)
            playersWaiting.remove(e.player)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameEnded(@Suppress("UNUSED_PARAMETER") e: BmRunStoppedIntent) {
        for (p in playersWaiting) {
            val nextGame = findNextGame()
            if (nextGame != null) {
                BmPlayerJoinGLIntent.join(nextGame, p)
            } else {
                return
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(e: BmPlayerMovedEvent) {
        if (e.game != this)
            return
        if (e.player.location.distanceSquared(location) > radius*radius) {
            BmPlayerLeaveGLIntent.leave(e.player)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerLeave(e: BmPlayerLeaveGLIntent) {
        playersWaiting.remove(e.player)
    }

    @EventHandler(ignoreCancelled = true)
    fun onLobbyTerminated(e: BmGLTerminatedIntent) {
        if (e.gl != this)
            return
        HandlerList.unregisterAll(this)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true)
    fun onLobbyDeleted(e: BmGLDeleteIntent) {
        if (e.gl != this)
            return
        BmGLTerminatedIntent.terminate(this)
        if (e.isDeletingSave)
            File(Bomberman.instance.settings.lobbySaves(), "${name}.yml").delete()
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true)
    fun onServerStop(e: PluginDisableEvent) {
        if (e.plugin != plugin)
            return
        BmGLTerminatedIntent.terminate(this)
    }

    override fun format(args: List<Message>): Message {
        return when (args.firstOrNull()?.toString() ?: "name") {
            "name" -> Message.of(name)
            "type" -> Message.of("lobby")
            "x" -> Message.of(location.x.toInt())
            "y" -> Message.of(location.y.toInt())
            "z" -> Message.of(location.z.toInt())
            "world" -> Message.of(location.world.toString())
            "players" -> CollectionWrapper(playersWaiting.map { SenderWrapper(it) })
                    .format(args.drop(1))
            else -> Message.empty
        }
    }

    override fun name(): String {
        return name
    }
}
