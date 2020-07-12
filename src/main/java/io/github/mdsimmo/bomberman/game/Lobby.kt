package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.*
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.LocationWrapper
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.utils.Box
import io.github.mdsimmo.bomberman.utils.BukkitUtils
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import java.io.File

class Lobby private constructor(
        val name: String,
        val box: Box,
        val settings: LobbySettings = LobbySettings(),
        val games: List<Game> = mutableListOf()
    ) : Listener, Formattable {

    companion object {
        private val plugin = Bomberman.instance

        fun saveLobby(lobby: Lobby) {
            val file = YamlConfiguration()
            file.set("name", lobby.name)
            file.set("box", lobby.box)
            file.set("settings", lobby.settings)
            file.set("games", lobby.games)
            file.save(file(lobby.name))
        }

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

        private fun loadLobby(file: File): Lobby? {
            val data = YamlConfiguration.loadConfiguration(file)
            val name = data["name"] as? String ?: return null
            val box = data["box"] as? Box ?: return null
            val settings = data["settings"] as? LobbySettings ?: plugin.settings.defaultLobbySettings()
            return Lobby(name, box, settings)
        }

        fun file(lobby: String): File {
            return File(plugin.settings.gameSaves(), "$lobby.yml")
        }
    }

    val spawn: Location by lazy {
        box.stream()
                .map { it.block.state }
                .filter { it is Sign }
                .map { it as Sign }
                .filter { it.lines.any { line -> line.toLowerCase().contains("[spawn]") } }
                .map { it.location }
                .findAny()
                .get()
    }
    val players = mutableListOf<Player>()

    init {
        saveLobby(this)
    }

    private fun recheckGames() {
        if (players.size < settings.minPlayers)
            return
        val game = BmGameListIntent.listGames()
                .filter { it.lobby == this }
                .filter { !it.running }
                .randomOrNull() ?: return
        BmRunStartCountDownIntent.startGame(game, settings.autostartDelay, false)
    }

    @EventHandler(ignoreCancelled = true)
    fun onLobbyLookup(e: BmLobbyLookupIntent) {
        if (e.name.equals(name, ignoreCase = true))
            e.lobby = this
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerJoinLobby(e: BmJoinLobbyIntent) {
        if (e.lobby != this)
            return

        LobbyPlayer.spawnLobbyPlayer(e.player, this, spawn)
        players.add(e.player)
        recheckGames()

        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameStarted(e: BmRunStartedIntent) {
        if (e.game.lobby != this)
            return
        recheckGames()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerLeave(e: BmPlayerLeaveIntent) {
        if (players.contains(e.player)) {
            players.remove(e.player)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onCountDown(e: BmTimerCountedEvent) {
        if (e.game.lobby != this)
            return
        if (e.count <= settings.autostartJoinAt) {
            players.takeWhile { !BmJoinGameIntent.join(e.game, it).isCancelled }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameEnded(e: BmRunStoppedIntent) {
        if (e.game.lobby != this)
            return
        recheckGames()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onLobbyTerminated(e: BmLobbyTerminatedIntent) {
        if (e.lobby != this)
            return
        if (e.isDeletingSave) {
            file(name).delete()
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onServerStop(e: PluginDisableEvent) {
        if (e.plugin != plugin)
            return
        BmLobbyTerminatedIntent.delete(this, deleteSave = false)
    }

    override fun format(args: List<Message>): Message {
        return when (args.getOrNull(0)?.toString()?.toLowerCase() ?: "name") {
            "name" -> Message.of(name)
            "location" -> LocationWrapper(BukkitUtils.boxLoc1(box)).format(args.drop(1))
            "xsize" -> Message.of(box.size.x)
            "ysize" -> Message.of(box.size.y)
            "zsize" -> Message.of(box.size.z)
            else -> Message.empty
        }
    }
}