package io.github.mdsimmo.bomberman.utils

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import java.util.*
import java.util.stream.Stream
import javax.annotation.CheckReturnValue
import kotlin.streams.asStream

@CheckReturnValue
class Box(val world: World, val p1: Dim, val p2: Dim) {

    val size: Dim get() = Dim(p2.x - p1.x + 1, p2.y - p1.y + 1, p2.z - p1.z + 1)

    constructor(world: World, x: Int, y: Int, z: Int, xSize: Int, ySize: Int, zSize: Int) : this(world, Dim(x, y, z), Dim(x+xSize-1, y+ySize-1, z+zSize-1)) {}

    constructor(l: Location, xSize: Int, ySize: Int, zSize: Int) : this(l.world!!, l.blockX, l.blockY, l.blockZ, xSize, ySize, zSize) {}

    operator fun contains(l: Location): Boolean {
        return contains(l.world, l.blockX, l.blockY, l.blockZ)
    }

    fun contains(world: World?, x: Int, y: Int, z: Int): Boolean {
        return if (world != this.world) false else x >= p1.x && x <= p1.x + size.x && y >= p1.y && y <= p1.y + size.y && z >= p1.z && z <= p1.z + size.z
    }

    val entities: List<Entity>
        get() {
            val entities: MutableList<Entity> = ArrayList()
            // the "+ 16" is to make sure the chunks at the edge are also included
            var i = p1.x
            while (i < p1.x + size.x + 16) {
                var k = p1.z
                while (k < p1.z + size.z + 16) {
                    val chunk = world.getBlockAt(i, 1, k).chunk
                    for (entity in chunk.entities) {
                        if (contains(entity.location)) entities.add(entity)
                    }
                    k += 16
                }
                i += 16
            }
            return entities
        }

    override fun toString(): String {
        return "Box $p1 - $p2 : (${size.volume()} blocks)"
    }

    override fun hashCode(): Int {
        var hash = 31
        hash = hash * 31 + world.hashCode()
        hash = hash * 31 + p1.hashCode()
        hash = hash * 31 + p2.hashCode()
        return hash
    }

    fun stream(): Stream<Location> = sequence {
        for (x in p1.x..p2.x) {
            for (y in p1.y..p2.y) {
                for (z in p1.z..p2.z) {
                    yield(Location(world, x.toDouble(), y.toDouble(), z.toDouble()))
                }
            }
        }
    }.asStream()

    override fun equals(other: Any?): Boolean {
        return if (other is Box) {
            other.world == world && other.p1 == p1 && other.p2 == p2
        } else {
            false
        }
    }

}