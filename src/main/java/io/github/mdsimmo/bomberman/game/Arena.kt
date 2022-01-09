package io.github.mdsimmo.bomberman.game

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.mask.BlockTypeMask
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.block.BlockTypes
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.utils.BukkitUtils
import io.github.mdsimmo.bomberman.utils.RefectAccess
import io.github.mdsimmo.bomberman.utils.WorldEditUtils
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Item

class Arena(val settings: ArenaSettings, val clipboard: Clipboard) : Formattable {

    data class ArenaSettings(val origin: Location, val flags: BuildFlags) : ConfigurationSerializable {

        init {
            // align origin to block
            origin.x = origin.blockX.toDouble()
            origin.y = origin.blockY.toDouble()
            origin.z = origin.blockZ.toDouble()
        }

        override fun serialize(): MutableMap<String, Any> {
            return mutableMapOf(
                Pair("flags", flags),
                Pair("origin", origin)
            )
        }

        companion object {
            @JvmStatic
            @RefectAccess
            fun deserialize(data: Map<String, Any?>): ArenaSettings {
                return ArenaSettings(
                    origin = data["origin"] as Location,
                    flags = data["flags"] as BuildFlags,
                )
            }
        }
    }

    companion object {
        private val plugin = Bomberman.instance
    }

    constructor(origin: Location, flags: BuildFlags, clipboard: Clipboard)
            :this(ArenaSettings(origin, flags), clipboard)

    val spawns: Set<Location> by lazy {
        searchSpawns()
    }
    val origin: Location get() = settings.origin
    val flags: BuildFlags get() = settings.flags
    val box = WorldEditUtils.pastedBounds(origin, clipboard)

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
                    spawns += BukkitAdapter.adapt(box.world, loc.subtract(clipboard.origin)).add(origin)
                }
            }
        }
        plugin.logger.info("  ${spawns.size} spawns found")
        return spawns
    }

    fun build() {

        plugin.logger.info("Building schematic ...")

        // cleanup any dropped items
        box.world.getNearbyEntities(BukkitUtils.convert(box))
                .filterIsInstance<Item>()
                .forEach{ it.remove() }

        // Paste the schematic
        WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(box.world))
                .use { editSession ->

                    val operation = ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(origin.blockX, origin.blockY, origin.blockZ))
                            .copyEntities(true)
                            .ignoreAirBlocks(flags.skipAir)
                            .build()
                    Operations.complete(operation)

                    editSession.close()

                    if (flags.deleteVoid) {
                        editSession.replaceBlocks(
                            WorldEditUtils.convert(box),
                            BlockTypeMask(editSession, BlockTypes.STRUCTURE_VOID),
                                BlockTypes.AIR!!.defaultState
                        )
                    }
                }

        plugin.logger.info("Rebuild done")
    }

    override fun format(args: List<Message>, elevated: Boolean): Message {
        return when (args.firstOrNull()?.toString()?.lowercase() ?: "name") {
            "xsize" -> Message.of(box.size.x)
            "ysize" -> Message.of(box.size.y)
            "zsize" -> Message.of(box.size.z)
            else -> Message.empty
        }
    }
}