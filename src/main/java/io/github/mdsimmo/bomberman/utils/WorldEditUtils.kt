package io.github.mdsimmo.bomberman.utils

import com.sk89q.worldedit.BlockVector
import com.sk89q.worldedit.CuboidClipboard
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldedit.world.World
import org.bukkit.Location

object WorldEditUtils {

    @JvmStatic
    fun convert(box: Box): CuboidRegion = CuboidRegion(BukkitWorld(box.world) as World,
            BlockVector(box.p1.x, box.p1.y, box.p1.z),
            BlockVector(box.p2.x, box.p2.y, box.p2.z))

    @JvmStatic
    fun convert(dim: Dim): BlockVector = BlockVector(dim.x, dim.y, dim.z)

    @JvmStatic
    fun convert(vec: Vector): Dim = Dim(vec.x.toInt(), vec.y.toInt(), vec.z.toInt())

    @JvmStatic
    fun pastedBounds(pasteLocation: Location, clipboard: CuboidClipboard): Box {
        val pasteVec = BlockVector(pasteLocation.blockX, pasteLocation.blockY, pasteLocation.blockZ)
        val delta = pasteVec.subtract(clipboard.origin)
        val min = clipboard.offset.add(delta)
        val max = clipboard.offset.add(delta).add(clipboard.size)

        return Box(pasteLocation.world!!, convert(min), convert(max))
    }

    @JvmStatic
    fun selectionBounds(region: Region): Box {
        val min = region.minimumPoint
        val max = region.maximumPoint

        return Box((region.world as BukkitWorld).world, convert(min), convert(max))
    }

}