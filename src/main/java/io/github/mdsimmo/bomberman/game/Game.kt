package io.github.mdsimmo.bomberman.game

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.*
import io.github.mdsimmo.bomberman.messaging.Contexted
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import io.github.mdsimmo.bomberman.utils.Box
import io.github.mdsimmo.bomberman.utils.BukkitUtils
import io.github.mdsimmo.bomberman.utils.RefectAccess
import io.github.mdsimmo.bomberman.utils.WorldEditUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockState
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.ref.WeakReference

class Game private constructor(val name: String, private var schema: Arena, val settings: GameSettings = GameSettings())
    : Formattable, Listener, ConfigurationSerializable {

    companion object {
        private val plugin = Bomberman.instance

        fun BuildGameFromRegion(name: String, box: Box): Game {

            // Copy the blocks to a clipboard
            val region = WorldEditUtils.convert(box)
            val clipboard = BlockArrayClipboard(region)
            WorldEdit.getInstance().editSessionFactory.getEditSession(BukkitAdapter.adapt(box.world), -1)
                    .use { editSession ->
                        val forwardExtentCopy = ForwardExtentCopy(
                                editSession, region, clipboard, region.minimumPoint
                        )
                        Operations.complete(forwardExtentCopy)
                    }

            // Write the clipboard to a schematic file
            val file = File(plugin.settings.customSaves(), "$name.schematic")
            BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(FileOutputStream(file)).use {
                writer -> writer.write(clipboard)
            }

            // Make the Game
            return Game(name, Arena(file, BukkitUtils.boxLoc1(box), clipboard))
        }

        fun BuildGameFromSchema(name: String, loc: Location, file: File, skipAir: Boolean): Game {
            val arena = Arena(file, loc)
            arena.build(skipAir)
            return Game(name, arena)
        }

        @JvmStatic
        @RefectAccess
        fun deserialize(data: Map<String, Any>): Game {
            val name = data["name"] as String
            val settings = data["settings"] as GameSettings
            val schema = data["schema"] as String
            val loc = data["origin"] as Location
            return Game(name, Arena(File(schema), loc), settings)
        }
    }

    private class Arena {

        val origin: Location
        val file: File
        var box: Box
        private var clipboard: WeakReference<Clipboard>? = null

        constructor(file: File, origin: Location) {
            this.file = file
            this.origin = origin.clone()
            this.box = Box(origin, 1, 1, 1)
            loadClipboard() // will set box size correctly
        }

        constructor(file: File, origin: Location, clipboard: Clipboard) {
            this.file = file
            this.origin = origin.clone()
            this.box =  WorldEditUtils.pastedBounds(origin, clipboard)
            this.clipboard = WeakReference(clipboard)
        }

        private fun loadClipboard() : Clipboard =
            clipboard?.get() ?: {
                // Load the schematic
                val format = ClipboardFormats.findByFile(file)
                val c = format!!.getReader(FileInputStream(file)).use { it.read() }

                // cache the schematic
                clipboard = WeakReference(c)
                this.box = WorldEditUtils.pastedBounds(origin, c)
                c
            }()

        fun build(skipAir: Boolean) {

            plugin.logger.info("Building schematic ...")
            val clip = loadClipboard()

            // Paste the schematic
            WorldEdit.getInstance().editSessionFactory.getEditSession(BukkitAdapter.adapt(box.world), -1)
                    .use { editSession ->
                        val operation: Operation = ClipboardHolder(clip)
                                .createPaste(editSession)
                                .to(BlockVector3.at(origin.blockX, origin.blockY, origin.blockZ))
                                .copyEntities(true)
                                .ignoreAirBlocks(skipAir)
                                .build()
                        Operations.complete(operation)
                        // TODO undo arena build
                        // this undoes th building, but it should also delete the game
                        //if (user != null)
                        //    WorldEdit.getInstance().sessionManager.get(BukkitAdapter.adapt(user))?.remember(editSession)
                    }
            plugin.logger.info("Rebuild done")
        }
    }

    private val players: MutableSet<Player> = HashSet()
    private var running = false
    private var cageBlocks: Set<BlockState> = emptySet()
    private var signBlocks: Set<BlockState> = emptySet()

    private var spawnCache: Set<Location>? = null
    private val spawns: Set<Location> get() = {
        val spawnCache = this.spawnCache
        if (spawnCache == null) {
            val spawns = HashSet<Location>()
            for (l in schema.box.stream()) {
                val state = l.block.state
                if (state is org.bukkit.block.Sign) {
                    val lines = state.lines
                    if (lines.any { it.toLowerCase().contains("[spawn]") }) {
                        spawns += l
                    }
                }
            }
            this.spawnCache = spawns
            spawns
        } else {
            spawnCache
        }
    }()

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        GameProtection.protect(this, schema.box)
        GameRegistry.register(this)
        makeCages()
    }

    /*
    // announce scores
    private fun winnersDisplay() {
        for (rep in observers) {
            Chat.sendMessage(getMessage(Text.SCORE_ANNOUNCE, rep.getPlayer()))

            for (i in 0 until winners.size()) {
                val repWinner = winners.get(i)
                val place = i + 1
                Chat.messageRaw(getMessage(Text.WINNERS_LIST, rep.getPlayer())
                        .put("player", repWinner).put("place", place))
            }
        }
    }
*/
    //	}
    //		return list
    //					rep ).put( "stats", getStats( rep ) ) )
    //			list.add( getMessage( Text.SCORE_DISPLAY, sender ).put( "player",
    //		for ( GamePlayer rep : players )
    //		List<Message> list = new ArrayList<Message>( players.size() )
    //	public List<Message> scoreDisplay( CommandSender sender ) {

    private fun messagePlayers(text: Contexted) {
        val message = text.with("game", this).format()
        for (player in players)
            message.sendTo(player)
    }
//
//    fun switchSchema(newSchema: File) {
//        BmRunStoppedIntent.stopGame(this)
//        spawnCache = null
//        schema = Arena(newSchema, BukkitUtils.boxLoc1(schema.box))
//        schema.build(false)
//    }

    private fun findSpareSpawn(): Location? {
        return spawns.firstOrNull { spawn ->
            players.map { it.location }.none { playerLocation ->
                spawn.blockX == playerLocation.blockX
                        && spawn.blockY == playerLocation.blockY
                        && spawn.blockZ == playerLocation.blockZ
            }
        }
    }

    private fun removeCages(replaceSpawnSigns: Boolean) {
        cageBlocks.forEach{ data -> data.update(true) }
        if (replaceSpawnSigns)
            signBlocks.forEach{ data -> data.update(true) }
    }

    private fun makeCages() {
        removeCages(true)
        cageBlocks = HashSet<BlockState>().also { cage ->
            signBlocks = HashSet<BlockState>().also { signs ->
                spawns.forEach{ makeCage(it, cage, signs) }
            }
        }
    }

    private fun makeCage(location: Location, cage: HashSet<BlockState>, signs: HashSet<BlockState>) {
        for (i in -1..1) {
            for (j in -1..2) {
                for (k in -1..1) {
                    val blockLoc = location.clone().add(i.toDouble(), j.toDouble(), k.toDouble())
                    val b = blockLoc.block
                    if ((j == 0 || j == 1) && (i == 0 && k == 0)) {
                        signs.add(b.state)
                        b.type = Material.AIR
                    } else if (((j == 0 || j == 1) && (i == 0 || k == 0)) || (i == 0 && k == 0)) {
                        if (b.isPassable) {
                            cage.add(b.state)
                            b.type = Material.WHITE_STAINED_GLASS
                        }
                    }
                }
            }
        }
    }

    private fun playerDeadOrGone(p: Player) {
        players.remove(p)

        if (players.size <= 1) {
            players.forEach {
                Bukkit.getPluginManager().callEvent(BmPlayerWonEvent(this, it))
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, { BmRunStoppedIntent.stopGame(this) }, players.size * 20*5L)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerJoinGame(e: BmPlayerJoinGameIntent) {
        if (e.game != this)
            return

        if (running) {
            e.cancelFor(Text.GAME_ALREADY_STARTED
                    .with("game", this)
                    .with("player", e.player)
                    .format())
            return
        }

        // Check if there is spare room
        val gameSpawn = findSpareSpawn()
        if (gameSpawn == null) {
            e.cancelFor(Text.GAME_FULL
                    .with("game", this)
                    .with("player", e.player)
                    .format())
            return
        }

        GamePlayer.spawnGamePlayer(e.player, this, gameSpawn)
        players.add(e.player)
        e.setHandled()

        // Trigger auto-start if needed
        val delay = if (findSpareSpawn() == null) {
            5
        } else {
            null
        }
        if (delay != null) {
            BmRunStartCountDownIntent.startGame(this, delay)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onRunStartCountDown(e: BmRunStartCountDownIntent) {
        if (e.game != this)
            return

        if (running) {
            e.cancelBecause(Text.GAME_ALREADY_STARTED.with("game", this).format())
            return
        }

        StartTimer.createTimer(this, e.delay)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onCount(e: BmTimerCountedEvent) {
        if (e.game != this)
            return
        messagePlayers(Text.GAME_COUNT.with("time", e.count))
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onRunStarted(e: BmRunStartedIntent) {
        if (e.game != this)
            return
        running = true
        removeCages(false)
        messagePlayers(Text.GAME_STARTED)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerLeave(e: BmPlayerKilledIntent) {
        if (players.contains(e.player))
            playerDeadOrGone(e.player)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerLeave(e: BmPlayerLeaveGameIntent) {
        if (players.contains(e.player))
            playerDeadOrGone(e.player)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onRunStoppedWhileRunning(e: BmRunStoppedIntent) {
        if (e.game != this)
            return
        if (!running) {
            e.cancelFor(Text.STOP_NOT_STARTED.with("game", this).format())
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onRunStopped(e: BmRunStoppedIntent) {
        if (e.game != this)
            return
        running = false
        // Reset the arena
        schema.build(false)
        makeCages()
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameTerminated(e: BmGameTerminatedIntent) {
        if (e.game != this)
            return
        BmRunStoppedIntent.stopGame(this)

        HandlerList.unregisterAll(this)
        removeCages(true)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameDeleted(e: BmGameDeletedIntent) {
        if (e.game != this)
            return
        BmGameTerminatedIntent.terminateGame(this)
        GameRegistry.remove(this, e.isDeletingSave)
        e.setHandled()
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onServerStop(e: PluginDisableEvent) {
        if (e.plugin != plugin)
            return
        BmGameTerminatedIntent.terminateGame(this)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameRebuild(e: BmGameRebuildIntent) {
        if (e.game != this)
            return
        schema.build(false)
        makeCages()
        e.setHandled()
    }

    override fun format(args: MutableList<Message>): Message {
        if (args.size == 0)
            return Message.of(name)
        return when (args[0].toString()) {
            "name" -> Message.of(name)
            "maxplayers" -> Message.of(spawns.size)
            "arena" -> Message.of(schema.file.name)
            "players" -> Message.of(players.size.toString())
            "power" -> Message.of("n/a") // FIXME initial power startup
            "bombs" -> Message.of("n/a") // FIXME initial bombs startup
            "lives" -> Message.of(settings.lives.toString())
            "x" -> Message.of(schema.origin.x.toString())
            "y" -> Message.of(schema.origin.y.toString())
            "z" -> Message.of(schema.origin.z.toString())
            "running" -> Message.of(if (running) { "true" } else { "false" })
            else -> Message.empty()
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
                Pair("name", name),
                Pair("schema", schema.file.path),
                Pair("settings", settings),
                Pair("origin", schema.origin)
        )
    }

}
