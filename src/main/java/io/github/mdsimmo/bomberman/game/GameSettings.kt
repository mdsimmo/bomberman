package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.utils.BukkitUtils
import io.github.mdsimmo.bomberman.utils.RefectAccess
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
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
            (data["fire"] as? String?)?.let { Material.matchMaterial(it)} ?.also { settings.fireType = it }
            (data["loot-table"] as? List<*>?)
                    ?.filterIsInstance<Map<*, *>>()
                    ?.flatMap { section ->
                        val blocks = (section["blocks"] as? List<*>)
                                ?.filterIsInstance<String>()
                                ?.mapNotNull { Material.matchMaterial(it) }
                        val loot = (section["loot"] as? List<*>)
                                ?.filterIsInstance<Map<*,*>>()
                                ?.mapNotNull { itemWeight ->
                                    val weight = (itemWeight["weight"] as? Number)?.toInt()
                                    val itemStack = itemWeight["item"] as? ItemStack
                                    if (itemStack == null || weight == null || weight <= 0) {
                                        null
                                    } else {
                                        Pair(itemStack, weight)
                                    }
                                }
                                ?.toMap()
                        if (blocks == null || loot == null || blocks.isEmpty() || loot.isEmpty()) {
                            emptyList()
                        } else {
                            blocks.map { b-> Pair(b, loot) }
                        }
                    }?.toMap()
                    ?.also {
                        settings.blockLoot = it
                    }
            readMaterials(data["destructible"])?.also { settings.destructible = it }
            readMaterials(data["indestructible"])?.also { settings.indestructible = it }
            readMaterials(data["pass-keep"])?.also { settings.passKeep = it }
            readMaterials(data["pass-revert"])?.also { settings.passRevert = it }
            readMaterials(data["pass-destroy"])?.also { settings.passDestroy = it }
            (data["initial-items"] as? List<*>)
                    ?.map { it as? ItemStack }
                    ?.also {
                        settings.initialItems = it
                    }
            (data["lives"] as? Number)?.toInt()?.also { settings.lives = it }
            (data["fuse-ticks"] as? Number)?.toInt()?.also { settings.fuseTicks = it.coerceAtLeast(0) }
            (data["fire-ticks"] as? Number)?.toInt()?.also { settings.fireTicks = it.coerceAtLeast(0) }
            (data["immunity-ticks"] as? Number)?.toInt()?.also { settings.immunityTicks = it.coerceAtLeast(0) }
            return settings
        }

        private fun readMaterials(data: Any?): Set<Material>? {
            return (data as? List<*>)
                    ?.filterIsInstance<String>()
                    ?.mapNotNull {
                        Material.matchMaterial(it)
                    }?.toSet()
        }
    }

    var bombItem: Material = Material.TNT
    var powerItem: Material = Material.GUNPOWDER
    var fireType: Material = Material.FIRE
    var blockLoot: Map<Material, Map<ItemStack, Int>> =
            mapOf(
                Pair(ItemStack(Material.TNT, 1), 4),
                Pair(ItemStack(Material.GUNPOWDER, 1), 3),
                Pair(BukkitUtils.makePotion(PotionType.INSTANT_HEAL, 1), 1),
                Pair(BukkitUtils.makePotion(PotionType.SPEED, 1, upgraded = true), 1),
                Pair(BukkitUtils.makePotion(PotionType.INVISIBILITY, 1), 1),
                Pair(ItemStack(Material.AIR, 0), 100)
            ).let {
                mapOf(
                    Pair(Material.SNOW_BLOCK, it),
                    Pair(Material.DIRT, it),
                    Pair(Material.SAND, it),
                    Pair(Material.GRAVEL, it)
                )
            }
    var destructible = setOf(
            Material.TNT,
            Material.SNOW_BLOCK,
            Material.DIRT,
            Material.SAND,
            Material.GRAVEL
    )
    var indestructible = setOf<Material>()
    var passKeep = setOf<Material>()
    var passRevert = setOf<Material>()
    var passDestroy = setOf<Material>()
    var initialItems = listOf<ItemStack?>(
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
        objs["fire"] = fireType.key.toString()
        // condense duplicate loot values by swapping key and values
        val lootBlock = mutableMapOf<Map<ItemStack, Number>, MutableSet<Material>>()
        blockLoot.forEach { (mat, loot) ->
            lootBlock.getOrPut(loot) { mutableSetOf() }.add(mat)
        }
        objs["loot-table"] = lootBlock
                .map { (loot, matList) -> mapOf(
                        Pair("blocks", matList.map { it.key.toString() }.toList()),
                        Pair("loot", loot.map {(item, weight) ->
                            mapOf(
                                    Pair("item", item),
                                    Pair("weight", weight)
                            )
                        })
                )}
        objs["destructible"] = destructible
                .map { it.key.toString() }
        objs["indestructible"] = indestructible
                .map { it.key.toString() }
        objs["pass-keep"] = passKeep
                .map { it.key.toString() }
        objs["pass-revert"] = passRevert
                .map { it.key.toString() }
        objs["pass-destroy"] = passDestroy
                .map { it.key.toString() }
        objs["initial-items"] = initialItems
        objs["lives"] = lives
        objs["fuse-ticks"] = fuseTicks
        objs["fire-ticks"] = fireTicks
        objs["immunity-ticks"] = immunityTicks

        return objs
    }

    fun clone(): GameSettings {
        // I'm too lazy to specify all the parameters again. So just serialize and deserialize
        val configOut = YamlConfiguration()
        configOut["data"] = this

        val string = configOut.saveToString()

        val configIn = YamlConfiguration()
        configIn.loadFromString(string)
        return configIn["data"] as GameSettings
    }
}