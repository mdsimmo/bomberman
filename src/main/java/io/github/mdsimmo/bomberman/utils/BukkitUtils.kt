package io.github.mdsimmo.bomberman.utils

import org.bukkit.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.material.Dye
import org.bukkit.material.Wool
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType
import javax.annotation.CheckReturnValue

object BukkitUtils {

    @CheckReturnValue
    fun boxLoc1(box: Box): Location = Location(box.world, box.p1.x.toDouble(), box.p1.y.toDouble(), box.p1.z.toDouble())

    @CheckReturnValue
    fun boxLoc2(box: Box): Location = Location(box.world, box.p2.x.toDouble(), box.p2.y.toDouble(), box.p2.z.toDouble())

    @CheckReturnValue
    fun asLoc(world: World, dim: Dim): Location = Location(world, dim.x.toDouble(), dim.y.toDouble(), dim.z.toDouble())

    @CheckReturnValue
    fun makePotion(type: PotionType, qty: Int = 1, extend: Boolean = false, upgraded: Boolean = false):ItemStack {
        return ItemStack(Material.POTION, qty).also {
            val meta = it.itemMeta as PotionMeta
            meta.basePotionData = PotionData(type, extend, upgraded)
            it.itemMeta = meta
        }
    }

    @CheckReturnValue
    fun makeStainedGlassPane(color: DyeColor, qty: Int = 1): ItemStack {
        return ItemStack(Material.STAINED_GLASS_PANE, qty, 1, color.woolData)
    }

    @CheckReturnValue
    fun makeConcrete(color: DyeColor, qty: Int = 1): ItemStack {
        return ItemStack(Material.CONCRETE, qty, 1, color.woolData)
    }

    @CheckReturnValue
    fun blockLoc(loc: Location): Location {
        return Location(loc.world,
                loc.blockX.toDouble(), loc.blockY.toDouble(), loc.blockZ.toDouble())
    }
}