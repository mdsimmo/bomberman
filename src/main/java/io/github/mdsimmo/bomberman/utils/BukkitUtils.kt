package io.github.mdsimmo.bomberman.utils

import io.github.mdsimmo.bomberman.Bomberman
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.permissions.PermissibleBase
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType
import org.bukkit.util.BoundingBox
import java.lang.reflect.Field
import java.util.regex.Pattern
import javax.annotation.CheckReturnValue

object BukkitUtils {

    @CheckReturnValue
    fun boxLoc1(box: Box): Location = Location(box.world, box.p1.x.toDouble(), box.p1.y.toDouble(), box.p1.z.toDouble())

    @CheckReturnValue
    fun boxLoc2(box: Box): Location = Location(box.world, box.p2.x.toDouble(), box.p2.y.toDouble(), box.p2.z.toDouble())

    @CheckReturnValue
    fun asLoc(world: World, dim: Dim): Location = Location(world, dim.x.toDouble(), dim.y.toDouble(), dim.z.toDouble())

    fun convert(box: Box): BoundingBox {
        return BoundingBox(box.p1.x.toDouble(), box.p1.y.toDouble(), box.p1.z.toDouble(),
                (box.p2.x+1).toDouble(), (box.p2.y+1).toDouble(), (box.p2.z+1).toDouble())
    }

    @CheckReturnValue
    fun makePotion(type: PotionType, qty: Int = 1, extend: Boolean = false, upgraded: Boolean = false):ItemStack {
        return ItemStack(Material.POTION, qty).also {
            val meta = it.itemMeta as PotionMeta
            meta.basePotionData = PotionData(type, extend, upgraded)
            it.itemMeta = meta
        }
    }

    @CheckReturnValue
    fun blockLoc(loc: Location): Location {
        return Location(loc.world,
                loc.blockX.toDouble(), loc.blockY.toDouble(), loc.blockZ.toDouble())
    }

    /**
     * Attempts to get the PermissibleBase object of the player. Null if cannot be gained.
     * @param player the player to get the PermissibleBase of
     * @return the base
     */
    fun getPermissibleBase(player: HumanEntity) : PermissibleBase? {
        return try {
            HUMAN_ENTITY_PERMISSIBLE_FIELD.value?.get(player) as? PermissibleBase
        } catch (e: Exception) {
            throw e
        }
    }

    // The reflex field containing the PermissibleBase object
    // Adapted from LuckPerms code:
    // me.lucko.luckperms.bukkit.util.CraftBukkitImplementation
    private val HUMAN_ENTITY_PERMISSIBLE_FIELD: Lazy<Field?> = lazy {
        val humanEntityPermissibleField = try {
            // CraftBukkit
            val serverPacketVersion = run {
                val server = Bukkit.getServer().javaClass
                val matcher = Pattern.compile("^org\\.bukkit\\.craftbukkit\\.(\\w+)\\.CraftServer$")
                    .matcher(server.name)
                if (matcher.matches()) {
                    '.' + matcher.group(1) + '.'
                } else {
                    "."
                }
            }
            val craftBukkitClassName = "org.bukkit.craftbukkit${serverPacketVersion}entity.CraftHumanEntity"
            Class.forName(craftBukkitClassName).getDeclaredField("perm")
        } catch (e: Exception) {
            try {
                // glowstone
                Class.forName("net.glowstone.entity.GlowHumanEntity").getDeclaredField("permissions")
            } catch (e: Exception) {
                // Cannot find anything
                Bomberman.instance.logger.warning("Unsupported server - Cannot modify players permissions.")
                null
            }
        }
        humanEntityPermissibleField?.isAccessible = true
        humanEntityPermissibleField
    }

}