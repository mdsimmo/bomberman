package io.github.mdsimmo.bomberman.game

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.mask.BlockTypeMask
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.function.pattern.BlockPattern
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.block.BlockTypes
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.utils.Box
import io.github.mdsimmo.bomberman.utils.BukkitUtils
import io.github.mdsimmo.bomberman.utils.WorldEditUtils
import org.bukkit.Location
import org.bukkit.entity.Item
import java.io.File
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference

class SchemaBuilder : Formattable {

    companion object {
        val plugin = Bomberman.instance
    }

    val origin: Location
    val file: File
    val flags: BuildFlags

    private var boxCache: Box? = null
    val box: Box
        get() {
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

    private var clipboard: WeakReference<Clipboard>? = null

    constructor(file: File, origin: Location, flags: BuildFlags) {
        this.file = file
        this.flags = flags
        this.origin = BukkitUtils.blockLoc(origin)
    }

    constructor(file: File, origin: Location, clipboard: Clipboard, flags: BuildFlags) {
        this.file = file
        this.flags = flags
        this.origin = BukkitUtils.blockLoc(origin)
        this.boxCache = WorldEditUtils.pastedBounds(origin, clipboard)
        this.clipboard = WeakReference(clipboard)
    }

    internal fun loadClipboard() : Clipboard =
            clipboard?.get() ?: {
                plugin.logger.info("Reading schematic data")
                // Load the schematic
                val format = ClipboardFormats.findByFile(file)
                        ?: throw IllegalArgumentException("Unknown file format: '${file.path}'")
                val c = format.getReader(FileInputStream(file)).use { it.read() }

                // cache the schematic
                clipboard = WeakReference(c)
                plugin.logger.info("data read")
                c
            }()

    private fun searchSpawns(): Set<Location> {
        val clip = loadClipboard()

        plugin.logger.info("Searching for spawns...")
        val spawns = mutableSetOf<Location>()
        for (loc in clip.region) {
            val block = clip.getFullBlock(loc)
            block.nbtData?.let {
                if (
                        it.getString("Text1").contains("[spawn]", ignoreCase = true) or
                        it.getString("Text2").contains("[spawn]", ignoreCase = true) or
                        it.getString("Text3").contains("[spawn]", ignoreCase = true) or
                        it.getString("Text4").contains("[spawn]", ignoreCase = true)
                ) {
                    spawns += BukkitAdapter.adapt(box.world, loc.subtract(clip.origin)).add(origin)
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
        box.world.getNearbyEntities(BukkitUtils.convert(box))
                .filterIsInstance<Item>()
                .forEach{ it.remove() }

        // Paste the schematic
        WorldEdit.getInstance().editSessionFactory.getEditSession(BukkitAdapter.adapt(box.world), -1)
                .use { editSession ->

                    val operation = ClipboardHolder(clip)
                            .createPaste(editSession)
                            .to(BlockVector3.at(origin.blockX, origin.blockY, origin.blockZ))
                            .copyEntities(true)
                            .ignoreAirBlocks(flags.skipAir)
                            .build()
                    Operations.complete(operation)

                    editSession.flushSession()

                    if (flags.deleteVoid) {
                        editSession.replaceBlocks(WorldEditUtils.convert(box),
                                BlockTypeMask(editSession, BlockTypes.STRUCTURE_VOID),
                                BlockPattern(BlockTypes.AIR!!.defaultState)
                        )
                    }

                    // TODO undo arena build
                    // this undoes the building, but it should also delete the game
                    //if (user != null)
                    //    WorldEdit.getInstance().sessionManager.get(BukkitAdapter.adapt(user))?.remember(editSession)
                }
        plugin.logger.info("Rebuild done")
    }

    override fun format(args: List<Message>): Message {
        return when (args.firstOrNull()?.toString()?.toLowerCase() ?: "name") {
            "name" -> Message.of(file.nameWithoutExtension)
            "file" -> Message.of(file.path)
            "filename" -> Message.of(file.name)
            "parent" -> Message.of(file.parent
                    ?: "")
            "xsize" -> Message.of(box.size.x)
            "ysize" -> Message.of(box.size.y)
            "zsize" -> Message.of(box.size.z)
            else -> Message.empty
        }
    }
}