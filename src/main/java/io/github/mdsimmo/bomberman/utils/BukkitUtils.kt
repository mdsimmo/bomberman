package io.github.mdsimmo.bomberman.utils

import org.bukkit.Location
import org.bukkit.World
import javax.annotation.CheckReturnValue

object BukkitUtils {

    @CheckReturnValue
    fun boxLoc1(box: Box): Location = Location(box.world, box.p1.x.toDouble(), box.p1.y.toDouble(), box.p1.z.toDouble())

    @CheckReturnValue
    fun boxLoc2(box: Box): Location = Location(box.world, box.p2.x.toDouble(), box.p2.y.toDouble(), box.p2.z.toDouble())

    @CheckReturnValue
    fun asLoc(world: World, dim: Dim): Location = Location(world, dim.x.toDouble(), dim.y.toDouble(), dim.z.toDouble())
}