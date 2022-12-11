package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.utils.RefectAccess
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import java.io.InputStreamReader

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
    val sourceMask: Set<Material>,
    val cageBlock: Material,
) : ConfigurationSerializable {

    companion object {
        @RefectAccess
        @JvmStatic
        fun deserialize(data: Map<String?, Any?>): GameSettings {

            val default = if (loadingDefault) {
                null
            } else {
                defaultSettings
            }

            return GameSettings(
                bombItem = (data["bomb"] as? String?)?.let { Material.matchMaterial(it) } ?: default!!.bombItem,
                powerItem = (data["power"] as? String?)?.let { Material.matchMaterial(it)} ?: default!!.powerItem,
                fireType = (data["fire"] as? String?)?.let { Material.matchMaterial(it)} ?: default!!.fireType,
                blockLoot = (data["loot-table"] as? List<*>?)
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
                    ?: default!!.blockLoot,
                destructible = readMaterials(data["destructible"]) ?: default!!.destructible,
                indestructible = readMaterials(data["indestructible"]) ?: default!!.indestructible,
                passKeep = readMaterials(data["pass-keep"]) ?: default!!.passKeep,
                passDestroy = readMaterials(data["pass-destroy"]) ?: default!!.passDestroy,
                initialItems = (data["initial-items"] as? List<*>)
                    ?.map { it as? ItemStack }
                    ?: default!!.initialItems,
                lives = (data["lives"] as? Number)?.toInt() ?: default!!.lives,
                fuseTicks = (data["fuse-ticks"] as? Number)?.toInt()?.coerceAtLeast(0) ?: default!!.fuseTicks,
                fireTicks = (data["fire-ticks"] as? Number)?.toInt()?.coerceAtLeast(0) ?: default!!.fireTicks,
                immunityTicks = (data["immunity-ticks"] as? Number)?.toInt()?.coerceAtLeast(0) ?: default!!.immunityTicks,
                damageSources = (data["damage-source"] as? Map<*, *>)
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
                    ?: default!!.damageSources,
                sourceMask = readMaterials(data["source-mask"]) ?: default!!.sourceMask,
                cageBlock = (data["cage-block"] as? String?)?.let { Material.matchMaterial(it)} ?: default!!.cageBlock,
            )
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
        objs["source-mask"] = sourceMask
            .map { it.key.toString()}
        objs["cage-block"] = cageBlock.key.toString()

        return objs
    }
}

class GameSettingsBuilder(
    var bombItem: Material,
    var powerItem: Material,
    var fireType: Material,
    var blockLoot: Map<Material, Map<ItemStack, Int>>,
    var destructible: Set<Material>,
    var indestructible: Set<Material>,
    var passKeep: Set<Material>,
    var passDestroy: Set<Material>,
    var initialItems: List<ItemStack?>,
    var lives: Int,
    var fuseTicks: Int,
    var fireTicks: Int,
    var immunityTicks : Int,
    var damageSources: Map<String, Map<String, String>>,
    var sourceMask: Set<Material>,
    var cageBlock: Material,
) {

    constructor() : this(defaultSettings)

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
        settings.sourceMask,
        settings.cageBlock
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
            sourceMask,
            cageBlock
        )
}

private var loadingDefault = false

private val defaultSettings: GameSettings by lazy {
    Bomberman.instance.getResource("games/README.yml")!!.use { resource ->
        InputStreamReader(resource).use {
            loadingDefault = true
            val result = YamlConfiguration.loadConfiguration(it).getSerializable("settings", GameSettings::class.java)!!
            loadingDefault = false
            result
        }
    }
}