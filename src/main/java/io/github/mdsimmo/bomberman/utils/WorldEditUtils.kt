package io.github.mdsimmo.bomberman.utils

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import org.bukkit.Location

object WorldEditUtils {

    @JvmStatic
    fun convert(box: Box): CuboidRegion = CuboidRegion(BukkitAdapter.adapt(box.world),
            BlockVector3.at(box.p1.x.toDouble(), box.p1.y.toDouble(), box.p1.z.toDouble()),
            BlockVector3.at(box.p2.x.toDouble(), box.p2.y.toDouble(), box.p2.z.toDouble()))

    @JvmStatic
    fun convert(dim: Dim): BlockVector3 = BlockVector3.at(dim.x, dim.y, dim.z)

    @JvmStatic
    fun convert(vec: BlockVector3): Dim = Dim(vec.x, vec.y, vec.z)

    @JvmStatic
    fun pastedBounds(pasteLocation: Location, clipboard: Clipboard): Box {
        val pasteVec = BlockVector3.at(pasteLocation.blockX, pasteLocation.blockY, pasteLocation.blockZ)
        val delta = pasteVec.subtract(clipboard.origin)
        val min = clipboard.minimumPoint.add(delta)
        val max = clipboard.maximumPoint.add(delta)

        return Box(pasteLocation.world!!, convert(min), convert(max))
    }

    @JvmStatic
    fun selectionBounds(region: Region): Box {
        val min = region.minimumPoint
        val max = region.maximumPoint

        return Box(BukkitAdapter.adapt(region.world), convert(min), convert(max))
    }

}