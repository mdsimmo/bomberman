package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.utils.BukkitUtils
import io.github.mdsimmo.bomberman.utils.RefectAccess
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionType

class GameSettings : ConfigurationSerializable {
    companion object {
        @RefectAccess
        @JvmStatic
        fun deserialize(data: Map<String?, Any?>): GameSettings {
            val settings = GameSettings()
            (data["bomb"] as? String?)?.let { Material.matchMaterial(it) } ?.also { settings.bombItem = it }
            (data["power"] as? String?)?.let { Material.matchMaterial(it)} ?.also { settings.powerItem = it }
            (data["block-loot"] as? Map<*, *>?)
                    ?.mapNotNull { (key, value) ->
                        val material = (key as? String)?.let { Material.matchMaterial(key) }
                        if (material == null) {
                            null
                        } else {
                            val map = (value as? Map<*, *>)?.mapNotNull {(k, v) ->
                                val itemStack = k as? ItemStack
                                val number = v as? Number
                                if (itemStack == null || number == null)
                                    null
                                else
                                    Pair(itemStack, number)
                            }?.toMap()
                            if (map == null) {
                                null
                            } else {
                                Pair(material, map)
                            }
                        }
                    }?.toMap()
                    ?.also {
                        settings.blockLoot = it
                    }
            (data["destructable"] as? List<*>)
                    ?.filterIsInstance<String>()
                    ?.mapNotNull {
                        Material.matchMaterial(it)
                    }?.toSet()
                    ?.also {
                        settings.destructable = it
                    }
            (data["initial-items"] as? List<*>)
                    ?.filterIsInstance<ItemStack>()
                    ?.also {
                        settings.initialItems = it
                    }
            (data["lives"] as? Number)?.toInt()?.also { settings.lives = it }
            (data["fuse-ticks"] as? Number)?.toInt()?.also { settings.fuseTicks = it.coerceAtLeast(0) }
            (data["fire-ticks"] as? Number)?.toInt()?.also { settings.fireTicks = it.coerceAtLeast(0) }
            (data["immunity-ticks"] as? Number)?.toInt()?.also { settings.immunityTicks = it.coerceAtLeast(0) }
            return settings
        }
    }

    var bombItem: Material = Material.TNT
    var powerItem: Material = Material.GUNPOWDER
    var blockLoot: Map<Material, Map<ItemStack, Number>> = mapOf(
            Pair(Material.SNOW_BLOCK, mapOf(
                    Pair(ItemStack(Material.TNT, 1), 4.0),
                    Pair(ItemStack(Material.GUNPOWDER, 1), 3.0),
                    Pair(BukkitUtils.makePotion(PotionType.INSTANT_HEAL, 1), 1.0),
                    Pair(BukkitUtils.makePotion(PotionType.SPEED, 1), 1.0),
                    Pair(BukkitUtils.makePotion(PotionType.INVISIBILITY, 1), 0.5),
                    Pair(ItemStack(Material.AIR, 0), 100.0)
            ))
    )
    var destructable = setOf(
            Material.TNT,
            Material.SNOW_BLOCK,
            Material.DIRT,
            Material.GRASS_BLOCK,
            Material.COARSE_DIRT,
            Material.PODZOL
    )
    var initialItems = listOf(
            ItemStack(bombItem, 3)
    )
    var lives = 3
    var fuseTicks: Int = 40
    var fireTicks: Int = 20
    var immunityTicks : Int = 21

    override fun serialize(): Map<String, Any> {
        val objs: MutableMap<String, Any> = HashMap()
        objs["bomb"] = bombItem.key.toString()
        objs["power"] = powerItem.key.toString()
        objs["block-loot"] = blockLoot
                .mapKeys { (mat, _) -> mat.key.toString() }
        objs["destructable"] = destructable
                .map { it.key.toString() }
        objs["initial-items"] = initialItems
        objs["lives"] = lives
        objs["fuse-ticks"] = fuseTicks
        objs["fire-ticks"] = fireTicks
        objs["immunity-ticks"] = immunityTicks

        return objs
    }
}