package io.github.mdsimmo.bomberman.game

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.mask.BlockTypeMask
import com.sk89q.worldedit.function.mask.Masks
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.block.BlockTypes
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.commands.game.UndoBuild
import io.github.mdsimmo.bomberman.events.*
import io.github.mdsimmo.bomberman.messaging.*
import io.github.mdsimmo.bomberman.utils.Box
import io.github.mdsimmo.bomberman.utils.BukkitUtils
import io.github.mdsimmo.bomberman.utils.WorldEditUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.server.PluginDisableEvent
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writer

class Game constructor(private val save: GameSave) : Formattable, Listener {

    companion object {
        private val plugin = Bomberman.instance

        private fun tempDataFile(game: Game): Path {
            return plugin.tempGameData().resolve(GameSave.sanitize("${game.name}.yml"))
        }

        fun buildGameFromRegion(name: String, box: Box, settings: GameSettings): Game {

            // Copy the blocks to a clipboard
            val region = WorldEditUtils.convert(box)
            val clipboard = BlockArrayClipboard(region)
            WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(box.world))
                    .use { editSession ->
                        val forwardExtentCopy = ForwardExtentCopy(
                                editSession, region, clipboard, region.minimumPoint
                        )
                        Operations.complete(forwardExtentCopy)
                    }

            // Save all data
            val save = GameSave.createNewSave(
                name,
                BukkitUtils.boxLoc1(box),
                settings,
                clipboard
            )

            // Delete history from previous games with the same name
            UndoBuild.removeHistory(name)

            // Create a new game
            val game = Game(save)
            game.makeCages()
            return game
        }

        fun buildGameFromSchema(name: String, loc: Location, clipboard: Clipboard, settings: GameSettings): Game {
            val save = GameSave.createNewSave(name, loc, settings, clipboard)
            val game = Game(save)
            UndoBuild.retainHistory(game.name, game.box)
            BmGameBuildIntent.build(game)
            return game
        }
    }

    val name = save.name
    var settings: GameSettings
        get() = save.getSettings()
        set(value) {
            save.updateSettings(value)
        }

    private val origin: Location get() = save.origin
    val clipboard: Clipboard @Throws(IOException::class) get() = save.getSchematic() // TODO make private
    private val box: Box by lazy {
        WorldEditUtils.pastedBounds(origin, clipboard)
    }
    private val players: MutableSet<Player> = HashSet()
    private var running = false
    private val tempData: YamlConfiguration = tempDataFile(this).let { path ->
        if (path.exists()) {
            path.reader().use { YamlConfiguration.loadConfiguration(it) }
        } else {
            YamlConfiguration()
        }
    }

    // Spawns are saved in temporary file to avoid needing to read the schematic on server load
    private val spawns: Set<Location> by lazy {
        (tempData.getList("spawn-points"))
            ?.filterIsInstance(Location::class.java)?.toSet()
            ?: searchSpawns().also { writeTempData("spawns", it.toList()) }
    }

    init {
        // If game was not shut down cleanly (i.e. server died), rebuild the arena
        if (tempData.getBoolean("rebuild-needed", false)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                BmGameBuildIntent.build(this)
            }
        }

        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    private fun writeTempData(path: String, obj: Any?) {
        tempData.set(path, obj)
        tempDataFile(this).writer().use { it.write(tempData.saveToString()) }
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

    private fun searchSpawns(): Set<Location> {
        plugin.logger.info("Searching for spawns...")
        val spawns = mutableSetOf<Location>()
        for (loc in clipboard.region) {
            val block = clipboard.getFullBlock(loc)
            block.nbtData?.let {
                if (
                    it.getString("Text1").contains("[spawn]", ignoreCase = true) or
                    it.getString("Text2").contains("[spawn]", ignoreCase = true) or
                    it.getString("Text3").contains("[spawn]", ignoreCase = true) or
                    it.getString("Text4").contains("[spawn]", ignoreCase = true)
                ) {
                    spawns += BukkitAdapter.adapt(box.world, loc.subtract(clipboard.origin)).add(origin).block.location
                }
            }
        }
        plugin.logger.info("  ${spawns.size} spawns found")
        return spawns
    }

    private fun findSpareSpawn(): Location? {
        return spawns.firstOrNull { spawn ->
            players.map { it.location }.none { playerLocation ->
                spawn.blockX == playerLocation.blockX
                        && spawn.blockY == playerLocation.blockY
                        && spawn.blockZ == playerLocation.blockZ
            }
        }
    }

    private fun removeCages() {
        WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(save.origin.world))
                .use { editSession ->
                    val offset = BlockVector3.at(save.origin.x, save.origin.y, save.origin.z)
                            .subtract(save.getSchematic().origin)
                    spawnBlocks()
                            .filter { (isSpawn, _) ->
                                !isSpawn
                            }
                            .forEach { (_, block) ->
                                val loc = block.location
                                val blockVec = BlockVector3.at(loc.x, loc.y, loc.z)
                                val clipLocation = blockVec.subtract(offset)
                                val blockState = save.getSchematic().getFullBlock(clipLocation)
                                editSession.setBlock(blockVec, blockState)
                            }
                }
    }

    private fun makeCages() {
        spawnBlocks().forEach { (sign, block) ->
            if (sign) {
                block.type = Material.AIR
            } else if (block.isPassable) {
                block.type = settings.cageBlock
            }
        }
    }

    private fun spawnBlocks() = sequence {
        for (location: Location in spawns) {
            for (i in -1..1) {
                for (j in -1..2) {
                    for (k in -1..1) {
                        val blockLoc = location.clone().add(i.toDouble(), j.toDouble(), k.toDouble())
                        if(!box.contains(blockLoc)) {
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
            e.cancelFor(Text.GAME_ALREADY_STARTED.format(Context(false)
                    .plus("game", this)
                    .plus("player", e.player)))
            return
        }

        // Check if there is spare room
        val gameSpawn = findSpareSpawn()
        if (gameSpawn == null) {
            e.cancelFor(Text.JOIN_GAME_FULL.format(Context(false)
                    .plus("game", this)
                    .plus("player", e.player)))
            return
        }

        GamePlayer.spawnGamePlayer(e.player, this, gameSpawn)
        players.add(e.player)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onPlayerDamaged(e: EntityDamageEvent) {
        if (!players.contains(e.entity))
            return

        // Find any changes to apply to this event based on cause
        val damageChanges = this.settings.damageSources
            .filterKeys { e.cause.toString().matches(Regex(it, RegexOption.IGNORE_CASE)) }
            .map { it.value }

        // If none specified, assume cancel event, except CUSTOM (which is what our plugin uses)
        if (damageChanges.isEmpty() && e.cause != EntityDamageEvent.DamageCause.CUSTOM) {
            e.isCancelled = true
            return
        }

        // Special check to test if event should be cancelled
        damageChanges
            .forEach { change ->
                val cancelExpression = change.entries.firstOrNull { it.key.equals("cancel", ignoreCase = true) }?.value
                    ?: return@forEach
                val result = Expander.expand(cancelExpression, Context(false)
                    .plus("base", Message.of(e.damage))
                    .plus("final", Message.of(e.finalDamage))
                    .plus("cause", Message.of(e.cause.toString()))
                    .plus("player", SenderWrapper(e.entity))
                    .plus("game", this)).toString()
                val asDouble = result.toDoubleOrNull() ?: return@forEach
                if (asDouble > 0.000001 || asDouble < -0.000001) {
                    e.isCancelled = true
                    return
                }
            }

        damageChanges
            // Each change has a set of rules which apply to individual modifiers
            .forEach { rules ->
                // All other rules affect the modifiers
                EntityDamageEvent.DamageModifier.values()
                    .filter { e.isApplicable(it) } // not-applicable modifiers cause crashes
                    .forEach { modifier ->
                        // find rules that apply to this modifier (regex match)
                        rules.filterKeys { modifier.toString().matches(Regex(it, RegexOption.IGNORE_CASE)) }
                            // apply rule
                            .forEach {
                                val result = Expander.expand(it.value, Context(false)
                                    .plus("base", Message.of(e.damage))
                                    .plus("damage", Message.of(e.getDamage(modifier)))
                                    .plus("final", Message.of(e.finalDamage))
                                    .plus("cause", Message.of(e.cause.toString()))
                                    .plus("player", SenderWrapper(e.entity))
                                    .plus("game", this)
                                    .plus("modifier", Message.of(modifier.toString()))
                                )
                                result.toString().toDoubleOrNull()?.also { damage ->
                                    e.setDamage(modifier, damage)
                                }
                            }
                    }
            }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onRunStartCountDown(e: BmRunStartCountDownIntent) {
        if (e.game != this)
            return

        if (running) {
            e.cancelBecause(Text.GAME_ALREADY_STARTED.format(Context(false).plus("game", this)))
            return
        }

        if (players.size == 0) {
            e.cancelBecause(Text.GAME_NO_PLAYERS.format(Context(false).plus("game", this)))
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
        removeCages()
        GameProtection.protect(this, box)
        writeTempData("rebuild-needed", true)
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    fun onPlayerMoveOutOfArena(e: BmPlayerMovedEvent) {
        if (e.game != this)
            return
        if (!box.contains(e.getTo())) {
            BmPlayerLeaveGameIntent.leave(e.player)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerLeave(e: BmPlayerLeaveGameIntent) {
        if (players.contains(e.player)) {
            players.remove(e.player)

            // If not running (but might be counting down) or no players left, stop the game immediately
            if (players.size < 1 || !this.running) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
                    BmRunStoppedIntent.stopGame(this)
                }
            } else if (players.size == 1) {
                // Tell remaining player they won
                players.forEach {
                    Bukkit.getPluginManager().callEvent(BmPlayerWonEvent(this, it))
                }
                // Let player celebrate for 5 seconds, then stop the game
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                    BmRunStoppedIntent.stopGame(this)
                }, 5*20L) // 5 seconds to see messages
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    fun onRunStoppedWhileRunning(e: BmRunStoppedIntent) {
        if (e.game != this)
            return
        if (!running) {
            e.cancelFor(Text.STOP_NOT_STARTED.format(Context(false).plus("game", this)))
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onRunStopped(e: BmRunStoppedIntent) {
        if (e.game != this)
            return
        if (running) {
            running = false
            // Reset the arena
            BmGameBuildIntent.build(this)
        }
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onGameRebuild(e: BmGameBuildIntent) {
        if (e.game != this)
            return

        plugin.logger.info("Building schematic ...")

        // cleanup any dropped items
        box.world.getNearbyEntities(BukkitUtils.convert(box))
            .filter { it !is Player }
            .forEach{
                //println("DELETE ENTITY: " + it.type + " " + it.location)
                //println("  box: " + box)
                it.remove() }

        // Paste the schematic
        WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(box.world))
            .use { editSession ->

                val operation = ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(origin.blockX, origin.blockY, origin.blockZ))
                    .copyEntities(true)
                    .maskSource(Masks.negate(BlockTypeMask(editSession,
                        save.getSettings().sourceMask.mapNotNull { BlockTypes.get(it.key.toString()) })))
                    .build()
                Operations.complete(operation)

                editSession.close()
            }
        makeCages()

        plugin.logger.info("Rebuild done")
        writeTempData("rebuild-needed", false)
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
        plugin.logger.info("Deleting $name" + if (e.isDeletingSave) {""} else {" (keeping data)"})
        BmGameTerminatedIntent.terminateGame(this)
        tempDataFile(this).deleteIfExists()
        if (e.isDeletingSave) {
            plugin.gameSaves().resolve(GameSave.sanitize("${name}.game.zip")).deleteIfExists()
        }
        e.setHandled()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onServerStop(e: PluginDisableEvent) {
        if (e.plugin != plugin)
            return
        BmGameTerminatedIntent.terminateGame(this)
    }

    override fun applyModifier(arg: Message): Formattable {
        return when (arg.toString().lowercase()) {
            "name" -> Message.of(name)
            "spawns" -> CollectionWrapper(spawns.map {
                RequiredArg { spawnArg->
                    when (spawnArg.toString().lowercase()) {
                        "world", "w" -> Message.of(it.world?.name ?: "unknown")
                        "x" -> Message.of(it.x.toInt())
                        "y" -> Message.of(it.y.toInt())
                        "z" -> Message.of(it.z.toInt())
                        else -> throw IllegalArgumentException("Unknown spawn format $spawnArg")
                    }
                }
            })
            "players" -> CollectionWrapper(players.map { SenderWrapper(it) })
            "power" -> Message.of(settings.initialItems.sumOf {
                if (it?.type == settings.bombItem) {
                    it.amount
                } else {
                    0
                }
            })
            "bombs" -> Message.of(settings.initialItems.sumOf {
                if (it?.type == settings.powerItem) {
                    it.amount
                } else {
                    0
                }
            })
            "lives" -> Message.of(settings.lives.toString())
            "w", "world" -> Message.of(origin.world?.name ?: "unknown")
            "x" -> Message.of(origin.x.toInt())
            "y" -> Message.of(origin.y.toInt())
            "z" -> Message.of(origin.z.toInt())
            "xsize" -> Message.of(box.size.x)
            "ysize" -> Message.of(box.size.y)
            "zsize" -> Message.of(box.size.z)
            "running" -> Message.of(
                if (running) {
                    "true"
                } else {
                    "false"
                }
            )
            "schema" -> this // for backwards compatibility
            else -> Message.empty
        }
    }

    override fun format(context: Context): Message {
        return applyModifier("name").format(context)
    }
}
