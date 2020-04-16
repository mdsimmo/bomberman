package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.type.TNT;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GameSettings implements ConfigurationSerializable {

    private static final ItemStack healingPotion = new ItemStack(Material.POTION, 1);
    static {
        PotionMeta meta = (PotionMeta)healingPotion.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        healingPotion.setItemMeta(meta);
    }

    public Material bombItem = Material.TNT;
    public Material powerItem = Material.GUNPOWDER;
    public Map<Material, Map<ItemStack, Number>> blockLoot = Map.of(
            Material.SNOW_BLOCK, Map.of(
                    new ItemStack(Material.TNT, 1),            2,
                    new ItemStack(Material.GUNPOWDER, 1),      2,
                    healingPotion,                                      1,
                    new ItemStack(Material.AIR, 0),            5
            ));
    public Set<Material> destructable = Set.of(
            Material.TNT,
            Material.SNOW
    );
    public List<ItemStack> initialItems = List.of(
            new ItemStack(bombItem, 3),
            new ItemStack(powerItem, 3)
    );
    public int lives = 3;

    @Nonnull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> objs = new HashMap<>();
        objs.put("bomb", bombItem.getKey().toString());
        objs.put("power", powerItem.getKey().toString());
        objs.put("block-loot", blockLoot.entrySet().stream()
                .map(it -> new AbstractMap.SimpleEntry<>(it.getKey().getKey().toString(), it.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
        objs.put("destructable", destructable.stream()
                .map(it -> it.getKey().toString())
                .collect(Collectors.toList()));
        objs.put("initial-items", initialItems);
        objs.put("lives", lives);
        return objs;
    }

    @RefectAccess
    public static GameSettings deserialize(Map<String, Object> data) {
        GameSettings settings = new GameSettings();
        settings.bombItem = Material.matchMaterial((String)data.get("bomb"));
        settings.powerItem = Material.matchMaterial((String)data.get("power"));
        settings.blockLoot = ((Map<String, Map<ItemStack, Number>>)data.get("block-loot")).entrySet().stream()
                .map(it -> new AbstractMap.SimpleEntry<>(Material.matchMaterial(it.getKey()), it.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        settings.destructable = ((List<String>)data.get("destructable")).stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toSet());
        settings.initialItems = (List<ItemStack>)data.get("initial-items");
        settings.lives = (int)(Number) data.get("lives");
        return settings;
    }

}
