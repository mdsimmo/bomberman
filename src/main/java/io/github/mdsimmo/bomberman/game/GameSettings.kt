package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.utils.BukkitUtils
import io.github.mdsimmo.bomberman.utils.RefectAccess
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionType
import javax.annotation.Nonnull

class GameSettings : ConfigurationSerializable {
    companion object {
        @RefectAccess
        @JvmStatic
        fun deserialize(data: Map<String?, Any?>): GameSettings {
            val settings = GameSettings()
            settings.bombItem = Material.matchMaterial(data["bomb"] as String)!!
            settings.powerItem = Material.matchMaterial(data["power"] as String)!!
            settings.blockLoot = (data["block-loot"] as Map<String, Map<ItemStack, Number>>)
                    .mapKeys { (matName, _) -> Material.matchMaterial(matName)!! }
            settings.destructable = (data["destructable"] as List<String>)
                    .map { name: String -> Material.matchMaterial(name)!! }
                    .toSet()
            settings.initialItems = data["initial-items"] as List<ItemStack>
            settings.lives = (data["lives"] as Number).toInt()
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
            Material.SNOW_BLOCK
    )
    var initialItems = listOf(
            ItemStack(bombItem, 3),
            ItemStack(powerItem, 1)
    )
    var lives = 3

    @Nonnull
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
        return objs
    }
}