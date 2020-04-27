@file:Suppress("DEPRECATION")

package io.github.mdsimmo.bomberman.game

import com.sk89q.worldedit.BlockVector
import com.sk89q.worldedit.CuboidClipboard
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.schematic.SchematicFormat
import com.sk89q.worldedit.world.World
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.*
import io.github.mdsimmo.bomberman.messaging.*
import io.github.mdsimmo.bomberman.utils.Box
import io.github.mdsimmo.bomberman.utils.BukkitUtils
import io.github.mdsimmo.bomberman.utils.RefectAccess
import io.github.mdsimmo.bomberman.utils.WorldEditUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference

class Game private constructor(val name: String, private var schema: Arena, val settings: GameSettings = plugin.settings.defaultGameSettings())
    : Formattable, Listener {

    companion object {
        private val plugin = Bomberman.instance

        @JvmStatic
        fun loadGames() {
            val data = plugin.settings.gameSaves()
            val files = data
                    .listFiles { _, name ->
                        name.endsWith(".yml")
                    }
                    ?: return
            for (f in files) {
                loadGame(f)
            }
        }

        fun loadGame(file: File): Game? {
            val data = YamlConfiguration.loadConfiguration(file)
            val name = data["name"] as? String ?: return null
            val schema = data["schema"] as? String ?: return null
            val loc = data["origin"] as? Location ?: return null
            val settings = data["settings"] as? GameSettings ?: plugin.settings.defaultGameSettings()
            val flags = data["build-flags"] as? BuildFlags ?: BuildFlags()
            return Game(name, Arena(File(schema), loc, flags), settings)
        }

        fun saveGame(game: Game) {
            val file = YamlConfiguration()
            file.set("name", game.name)
            file.set("schema", game.schema.file.path)
            file.set("origin", game.schema.origin)
            file.set("settings", game.settings)
            file.set("build-flags", game.schema.flags)
            file.save(File(plugin.settings.gameSaves(), "${game.name}.yml"))
        }

        private fun tempDataFile(game: Game): File {
            return File(plugin.settings.tempGameData(), "${game.name}.yml")
        }

        fun buildGameFromRegion(name: String, box: Box, flags: BuildFlags): Game {

            // Copy the blocks to a clipboard
            val clipboard = CuboidClipboard(WorldEditUtils.convert(box.size), WorldEditUtils.convert(box.p1))
            val editSession = WorldEdit.getInstance().editSessionFactory.getEditSession(BukkitWorld(box.world) as World, -1)
            clipboard.copy(editSession)
            editSession.flushQueue()

            // Write the clipboard to a schematic file
            val file = File(plugin.settings.customSaves(), "$name.schematic")
            SchematicFormat.MCEDIT.save(clipboard, file)

            // Make the Game
            val game = Game(name, Arena(file, BukkitUtils.boxLoc1(box), clipboard, flags))
            // TODO when building from selection, only need to build cages
            BmGameBuildIntent.build(game)
            return game
        }

        fun buildGameFromSchema(name: String, loc: Location, file: File, flags: BuildFlags): Game {
            val game = Game(name, Arena(file, loc, flags))
            BmGameBuildIntent.build(game)
            return game
        }
    }

    class BuildFlags : ConfigurationSerializable {
        var skipAir = false
        var deleteVoid = false

        override fun serialize(): Map<String, Any> {
            return mapOf(
                    Pair("skip-air", skipAir),
                    Pair("delete-void", deleteVoid)
            )
        }

        companion object {
            @JvmStatic
            @RefectAccess
            fun deserialize(data: Map<String, Any?>): BuildFlags {
                val buildFlags = BuildFlags()
                (data["skip-air"] as? Boolean)?.let { buildFlags.skipAir = it }
                (data["delete-void"] as? Boolean)?.let { buildFlags.deleteVoid = it }
                return buildFlags
            }
        }
    }

    private class Arena : Formattable {

        val origin: Location
        val file: File
        val flags: BuildFlags

        var boxCache: Box? = null
        val box: Box get() {
            return boxCache ?: {
                val c = loadClipboard()
                val box = WorldEditUtils.pastedBounds(origin, c)
                boxCache = box
                box
            }()
        }
        val spawns: Set<Location> by lazy {
            searchSpawns()
        }

        private var clipboard: WeakReference<CuboidClipboard>? = null

        constructor(file: File, origin: Location, flags: BuildFlags) {
            this.file = file
            this.flags = flags
            this.origin = BukkitUtils.blockLoc(origin)
        }

        constructor(file: File, origin: Location, clipboard: CuboidClipboard, flags: BuildFlags) {
            this.file = file
            this.flags = flags
            this.origin = BukkitUtils.blockLoc(origin)
            this.boxCache =  WorldEditUtils.pastedBounds(origin, clipboard)
            this.clipboard = WeakReference(clipboard)
        }

        internal fun loadClipboard() : CuboidClipboard =
            clipboard?.get() ?: {
                plugin.logger.info("Reading schematic data")
                // Load the schematic
                val c = SchematicFormat.MCEDIT.load(file)

                // cache the schematic
                clipboard = WeakReference(c)
                plugin.logger.info("data read")
                c
            }()

        private fun searchSpawns(): Set<Location> {
            val clip = loadClipboard()

            plugin.logger.info("Searching for spawns...")
            val spawns = mutableSetOf<Location>()
            for (loc in CuboidRegion(Vector.ZERO, clip.size.subtract(1, 1, 1))) {
                val block = clip.getBlock(loc)
                block.nbtData?.let {
                    if (
                            it.getString("Text1").contains("[spawn]", ignoreCase = true) or
                            it.getString("Text2").contains("[spawn]", ignoreCase = true) or
                            it.getString("Text3").contains("[spawn]", ignoreCase = true) or
                            it.getString("Text4").contains("[spawn]", ignoreCase = true)
                    ) {
                        spawns += Location(box.world, loc.x, loc.y, loc.z)
                                .add(origin).add(clip.offset.x, clip.offset.y, clip.offset.z)
                    }
                }
            }
            plugin.logger.info("  ${spawns.size} spawns found")
            return spawns
        }

        fun build() {

            plugin.logger.info("Building schematic ...")
            val clip = loadClipboard()

            // cleanup any dropped items
            box.world.getNearbyEntities(
                    BukkitUtils.boxLoc1(box).add(BukkitUtils.boxLoc2(box)).multiply(0.5),
                    box.size.x.toDouble()/2, box.size.y.toDouble()/2, box.size.z.toDouble()/2)
                    .filterIsInstance<Item>()
                    .forEach{ it.remove() }

            // Paste the schematic
            val editSession = WorldEdit.getInstance().editSessionFactory.getEditSession(BukkitWorld(box.world) as World, -1)
            clip.paste(editSession, BlockVector(origin.blockX, origin.blockY, origin.blockZ), flags.skipAir)
            editSession.flushQueue()

            // TODO undo arena build
            // this undoes the building, but it should also delete the game
            //if (user != null)
            //    WorldEdit.getInstance().sessionManager.get(BukkitAdapter.adapt(user))?.remember(editSession)
            plugin.logger.info("Rebuild done")
        }

        override fun format(args: List<Message>): Message {
            return when (args.firstOrNull()?.toString()?.toLowerCase() ?: "name") {
                "name" -> Message.of(file.nameWithoutExtension)
                "file" -> Message.of(file.path)
                "filename" -> Message.of(file.name)
                "parent" -> Message.of(file.parent ?: "")
                "xsize" -> Message.of(box.size.x)
                "ysize" -> Message.of(box.size.y)
                "zsize" -> Message.of(box.size.z)
                else -> Message.empty
            }
        }
    }

    private val players: MutableSet<Player> = HashSet()
    private var running = false
    private var spawns: Set<Location>
    private var tempData: YamlConfiguration

    init {
        tempData = YamlConfiguration.loadConfiguration(tempDataFile(this))
        spawns = (tempData.getList("spawns"))
                ?.filterIsInstance(Location::class.java)?.toSet()
                ?: schema.spawns
        writeData("spawns", spawns.toList())

        // If game was not shut down cleanly (ie. server died), rebuild the arena
        if (tempData.getBoolean("rebuild-needed", false)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                BmGameBuildIntent.build(this)
            }
        }

        Bukkit.getPluginManager().registerEvents(this, plugin)
        saveGame(this)
    }

    private fun writeData(path: String, obj: Any?) {
        tempData.set(path, obj)
        tempData.save(tempDataFile(this))
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

    private fun removeCages(replaceSpawnSign: Boolean) {
        val clip = schema.loadClipboard()
        val editSession = WorldEdit.getInstance().editSessionFactory
                .getEditSession(BukkitWorld(schema.origin.world) as World, -1)
        val offset = BlockVector(schema.origin.x, schema.origin.y, schema.origin.z).add(clip.offset)
        spawnBlocks()
                .filter { (isSpawn, _) ->
                    replaceSpawnSign or !isSpawn
                }
                .forEach { (_, block) ->
                    val loc = block.location
                    val blockVec = BlockVector(loc.x, loc.y, loc.z)
                    val clipLocation = blockVec.subtract(offset)
                    val blockState = clip.getBlock(clipLocation)
                    editSession.setBlock(blockVec, blockState)
                }
    }

    private fun makeCages() {
        spawnBlocks().forEach { (sign, block) ->
            if (sign) {
                block.type = Material.AIR
            } else if (!block.type.isSolid) {
                block.type = Material.STAINED_GLASS
            }
        }
    }

    private fun spawnBlocks() = sequence {
        for (location: Location in spawns) {
            for (i in -1..1) {
                for (j in -1..2) {
                    for (k in -1..1) {
                        val blockLoc = location.clone().add(i.toDouble(), j.toDouble(), k.toDouble())
                        if(!schema.box.contains(blockLoc)) {
                            continue
                        }
                        val b = blockLoc.block
                        if ((j == 0 || j == 1) && (i == 0 && k == 0)) {
                            yield(Pair(true, b))
                        } else if (((j == 0 || j == 1) && (i == 0 || k == 0)) || (i == 0 && k == 0)) {
                            yield(Pair(false, b))
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onGameListing(e: BmGameListIntent) {
        e.games.add(this)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onGameLookup(e: BmGameLookupIntent) {
        if (e.name.equals(name, ignoreCase = true))
            e.game = this
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
            e.cancelFor(Text.JOIN_GAME_FULL
                    .with("game", this)
                    .with("player", e.player)
                    .format())
            return
        }

        GamePlayer.spawnGamePlayer(e.player, this, gameSpawn)
        players.add(e.player)
        e.setHandled()
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
    fun onRunStarted(e: BmRunStartedIntent) {
        if (e.game != this)
            return
        running = true
        removeCages(false)
        GameProtection.protect(this, schema.box)
        writeData("rebuild-needed", true)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    fun onPlayerMoveOutOfArena(e: BmPlayerMovedEvent) {
        if (e.game != this)
            return
        if (!schema.box.contains(e.getTo())) {
            BmPlayerLeaveGameIntent.leave(e.player)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerLeave(e: BmPlayerLeaveGameIntent) {
        if (players.contains(e.player)) {
            players.remove(e.player)

            if (running && players.size <= 1) {
                players.forEach {
                    Bukkit.getPluginManager().callEvent(BmPlayerWonEvent(this, it))
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                    BmRunStoppedIntent.stopGame(this)
                }, players.size * 20*5L)
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
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
        BmGameBuildIntent.build(this)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameRebuild(e: BmGameBuildIntent) {
        if (e.game != this)
            return
        schema.build()
        makeCages()
        writeData("rebuild-needed", false)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameTerminated(e: BmGameTerminatedIntent) {
        if (e.game != this)
            return
        BmRunStoppedIntent.stopGame(this)
        HandlerList.unregisterAll(this)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameDeleted(e: BmGameDeletedIntent) {
        if (e.game != this)
            return
        BmGameTerminatedIntent.terminateGame(this)
        removeCages(true)
        tempDataFile(this).delete()
        if (e.isDeletingSave)
            File(Bomberman.instance.settings.gameSaves(), "${name}.yml").delete()
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onServerStop(e: PluginDisableEvent) {
        if (e.plugin != plugin)
            return
        BmGameTerminatedIntent.terminateGame(this)
    }

    override fun format(args: List<Message>): Message {
        if (args.isEmpty())
            return Message.of(name)
        return when (args[0].toString()) {
            "name" -> Message.of(name)
            "spawns" -> CollectionWrapper(spawns.map { object : Formattable {
                override fun format(args: List<Message>): Message {
                    require(args.size == 1) { "Spawn format must have one arg" }
                    return when(args[0].toString().toLowerCase()) {
                        "world", "w" -> Message.of(it.world?.name ?: "unknown")
                        "x" -> Message.of(it.x.toInt())
                        "y" -> Message.of(it.y.toInt())
                        "z" -> Message.of(it.z.toInt())
                        else -> throw IllegalArgumentException("Unknown spawn format ${args[0]}")
                    }
                }
            } }).format(args.drop(1))
            "schema" -> schema.format(args.drop(1))
            "players" -> CollectionWrapper(players.map { SenderWrapper(it) })
                    .format(args.drop(1))
            "power" -> Message.of(settings.initialItems.sumBy {
                if (it?.type == settings.bombItem) { it.amount } else { 0 }})
            "bombs" -> Message.of(settings.initialItems.sumBy {
                if (it?.type == settings.powerItem) { it.amount } else { 0 }})
            "lives" -> Message.of(settings.lives.toString())
            "w", "world" -> Message.of(schema.origin.world?.name ?: "unknown")
            "x" -> Message.of(schema.origin.x.toInt())
            "y" -> Message.of(schema.origin.y.toInt())
            "z" -> Message.of(schema.origin.z.toInt())
            "running" -> Message.of(if (running) { "true" } else { "false" })
            else -> Message.empty
        }
    }

}
