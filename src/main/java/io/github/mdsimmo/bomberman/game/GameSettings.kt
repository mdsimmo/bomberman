package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.utils.BukkitUtils
import io.github.mdsimmo.bomberman.utils.RefectAccess
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionType

/**
 * Defines the settings for how a game operates. Class is immutable
 */
data class GameSettings(
    val bombItem: Material,
    val powerItem: Material,
    val fireType: Material,
    val blockLoot: Map<Material, Map<ItemStack, Int>>,
    val destructible: Set<Material>,
    val indestructible: Set<Material>,
    val passKeep: Set<Material>,
    val passDestroy: Set<Material>,
    val initialItems: List<ItemStack?>,
    val lives: Int,
    val fuseTicks: Int,
    val fireTicks: Int,
    val immunityTicks : Int,
    val damageSources: Map<String, Map<String, String>>,
    val skipAir: Boolean,
    val deleteVoid: Boolean,
) : ConfigurationSerializable {

    companion object {
        @RefectAccess
        @JvmStatic
        fun deserialize(data: Map<String?, Any?>): GameSettings {
            val builder = GameSettingsBuilder()
            (data["bomb"] as? String?)?.let { Material.matchMaterial(it) } ?.also { builder.bombItem = it }
            (data["power"] as? String?)?.let { Material.matchMaterial(it)} ?.also { builder.powerItem = it }
            (data["fire"] as? String?)?.let { Material.matchMaterial(it)} ?.also { builder.fireType = it }
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
                    builder.blockLoot = it
                }
            readMaterials(data["destructible"])?.also { builder.destructible = it }
            readMaterials(data["indestructible"])?.also { builder.indestructible = it }
            readMaterials(data["pass-keep"])?.also { builder.passKeep = it }
            readMaterials(data["pass-destroy"])?.also { builder.passDestroy = it }
            (data["initial-items"] as? List<*>)
                ?.map { it as? ItemStack }
                ?.also {
                    builder.initialItems = it
                }
            (data["lives"] as? Number)?.toInt()?.also { builder.lives = it }
            (data["fuse-ticks"] as? Number)?.toInt()?.also { builder.fuseTicks = it.coerceAtLeast(0) }
            (data["fire-ticks"] as? Number)?.toInt()?.also { builder.fireTicks = it.coerceAtLeast(0) }
            (data["immunity-ticks"] as? Number)?.toInt()?.also { builder.immunityTicks = it.coerceAtLeast(0) }
            (data["damage-source"] as? Map<*, *>)
                ?.map { entry ->
                    Pair(entry.key.toString(), entry.value.let { cause ->
                        when (cause) {
                            is Map<*, *> ->
                                cause.map { Pair(it.key.toString(), it.value.toString()) }
                                    .toMap(mutableMapOf()) //  use mutable map to avoid instance references
                            null ->
                                mutableMapOf()
                            else ->
                                mapOf(Pair("base", cause.toString()))
                        }
                    })
                }
                ?.toMap()
                ?.also {
                    builder.damageSources = it
                }
            (data["skip-air"] as? Boolean)?.let { builder.skipAir = it }
            (data["delete-void"] as? Boolean)?.let { builder.deleteVoid = it }
            return builder.build()
        }

        private fun readMaterials(data: Any?): Set<Material>? {
            return (data as? List<*>)
                ?.filterIsInstance<String>()
                ?.mapNotNull {
                    Material.matchMaterial(it)
                }?.toSet()
        }
    }

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
        objs["pass-destroy"] = passDestroy
            .map { it.key.toString() }
        objs["initial-items"] = initialItems.dropLastWhile { it == null }
        objs["lives"] = lives
        objs["fuse-ticks"] = fuseTicks
        objs["fire-ticks"] = fireTicks
        objs["immunity-ticks"] = immunityTicks

        objs["damage-source"] = damageSources
        objs["damage-sourcesa"] = mutableMapOf<String, String>()
        objs["damage-sourcesb"] = mutableMapOf<String, String>()
        objs["skip-air"] = skipAir
        objs["delete-void"] = deleteVoid

        return objs
    }
}

class GameSettingsBuilder(
    var bombItem: Material = Material.TNT,
    var powerItem: Material = Material.GUNPOWDER,
    var fireType: Material = Material.FIRE,
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
        },
    var destructible: Set<Material> = setOf(
        Material.TNT,
        Material.SNOW_BLOCK,
        Material.DIRT,
        Material.SAND,
        Material.GRAVEL
    ),
    var indestructible: Set<Material> = setOf(),
    var passKeep: Set<Material> = setOf(),
    var passDestroy: Set<Material> = setOf(),
    var initialItems: List<ItemStack?> = listOf<ItemStack?>(
        ItemStack(bombItem, 3)
    ),
    var lives: Int = 3,
    var fuseTicks: Int = 40,
    var fireTicks: Int = 20,
    var immunityTicks : Int = 21,
    var damageSources: Map<String, Map<String, String>> = emptyMap(),
    var skipAir: Boolean = false,
    var deleteVoid: Boolean = false,
) {

    constructor(settings: GameSettings) :this(
        settings.bombItem,
        settings.powerItem,
        settings.fireType,
        settings.blockLoot,
        settings.destructible,
        settings.indestructible,
        settings.passKeep,
        settings.passDestroy,
        settings.initialItems,
        settings.lives,
        settings.fuseTicks,
        settings.fireTicks,
        settings.immunityTicks,
        settings.damageSources,
        settings.skipAir,
        settings.deleteVoid,
    )

    fun build(): GameSettings =
        GameSettings(
            bombItem,
            powerItem,
            fireType,
            blockLoot,
            destructible,
            indestructible,
            passKeep,
            passDestroy,
            initialItems,
            lives,
            fuseTicks,
            fireTicks,
            immunityTicks,
            damageSources,
            skipAir,
            deleteVoid
        )
}