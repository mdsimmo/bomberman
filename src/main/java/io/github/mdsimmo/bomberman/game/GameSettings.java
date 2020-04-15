package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSettings implements ConfigurationSerializable {

    public Material bombItem = Material.TNT;
    public Material powerItem = Material.GUNPOWDER;
    public LootTable blockLoot = Bukkit.getLootTable(new NamespacedKey(Bomberman.instance, "dirt.json"));
    public List<Material> destructable = List.of(
            Material.TNT,
            Material.DIRT,
            Material.COARSE_DIRT,
            Material.GRASS,
            Material.PODZOL,
            Material.GRASS_PATH
    );
    public List<ItemStack> initialItems = List.of(
            new ItemStack(bombItem, 3),
            new ItemStack(powerItem, 3)
    );
    public int lives = 3;
    public int minPlayers = 2;
    public int potionDuration = 10;

    @Nonnull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> objs = new HashMap<>();
        objs.put("bomb", bombItem);
        objs.put("power", powerItem);
        objs.put("block-loot", blockLoot);
        objs.put("destructable", destructable);
        objs.put("initial-items", initialItems);
        objs.put("lives", lives);
        objs.put("min-players", minPlayers);
        objs.put("potion-duration", potionDuration);
        return objs;
    }

    public GameSettings deserialize(Map<String, Object> data) {
        GameSettings settings = new GameSettings();
        settings.bombItem = Material.matchMaterial((String)data.get("bombItem"));
        return settings;
    }

}
