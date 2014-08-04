package io.github.mdsimmo.bomberman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

@SuppressWarnings("unchecked")
public abstract class Config {

	private static Plugin plugin = Bomberman.instance;
	private static FileConfiguration c = plugin.getConfig();
	
	public static final String FARE_PATH = "stake.fare";
	public static final String PRIZE_PATH = "stake.prize";
	public static final String LIVES_PATH = "lives";
	public static final String BOMBS_PATH = "bombs";
	public static final String POWER_PATH = "power";
	public static final String MIN_PLAYERS_PATH = "minplayers";
	public static final String AUTOSTART_PATH = "autostart";
	public static final String DEFAULT_STYLE = "defaultstyle";
	public static final String DROPS_ITEMS = "drops.items";
	public static final String DROPS_CHANCE = "drops.chance";
	public static final String BLOCKS_DROPPING = "blocks.drop";
	public static final String BLOCKS_DESTRUCTABLE = "blocks.destructable";
	
	
	protected static ItemStack fare;
	protected static ItemStack prize;
	protected static boolean pot;
	protected static int bombs;
	protected static int lives;
	protected static int power;
	protected static int minPlayers;
	protected static boolean autostart;
	protected static String defaultBoard;
	protected static List<ItemStack> drops;
	protected static double dropChance;
	protected static List<Material> destructables;
	protected static List<Material> droppingBlocks;
	
	static {
		// add defaults
		c.addDefault(LIVES_PATH, 3);
		c.addDefault(BOMBS_PATH, 3);
		c.addDefault(POWER_PATH, 3);
		c.addDefault(AUTOSTART_PATH, false);
		c.addDefault(MIN_PLAYERS_PATH, 2);
		c.addDefault(DEFAULT_STYLE, "default");
		c.addDefault(FARE_PATH, null);
		c.addDefault(PRIZE_PATH, new ItemStack(Material.DIAMOND, 3));
		ItemStack[] dropsTemp = { 
				new ItemStack(Material.TNT, 3),
				new ItemStack(Material.BLAZE_POWDER, 2),
				new Potion(PotionType.INSTANT_HEAL, 1).toItemStack(1),
				new Potion(PotionType.SPEED, 2).toItemStack(1)};
		c.addDefault(DROPS_ITEMS, Arrays.asList(dropsTemp));
		c.addDefault(DROPS_CHANCE, 0.1d);
		writeMaterials(c, BLOCKS_DROPPING, Arrays.asList(new Material[] {
			Material.DIRT
		}));
		writeMaterials(c, BLOCKS_DESTRUCTABLE, Arrays.asList(new Material[] {
				Material.DIRT
			}));
		
		// save defaults
		c.options().copyDefaults(true);
		plugin.saveConfig();

		// load user preferences
		bombs = c.getInt(BOMBS_PATH);
		power = c.getInt(POWER_PATH);
		lives = c.getInt(LIVES_PATH);
		minPlayers = c.getInt(MIN_PLAYERS_PATH);
		autostart = c.getBoolean(AUTOSTART_PATH);
		defaultBoard = c.getString(DEFAULT_STYLE);
		drops = (List<ItemStack>)c.getList(DROPS_ITEMS);
		dropChance = c.getDouble(DROPS_CHANCE);
		destructables = (List<Material>) c.getList(BLOCKS_DESTRUCTABLE);
		droppingBlocks = (List<Material>) c.getList(BLOCKS_DROPPING);
		fare = c.getItemStack(FARE_PATH);
		if (c.getString(PRIZE_PATH).equals("pot")) {
			prize = null;
			pot = true;
		} else {
			prize = c.getItemStack(PRIZE_PATH);
			pot = false;
		}
		
	}
	
	public static void writeMaterials(FileConfiguration config, String path, List<Material> materials) {
		List<String> converted = new ArrayList<>();
		for (Material m : materials) {
			converted.add(m.toString());
		}
		config.set(path, converted);
	}
	
	public static List<Material> getMaterialList(FileConfiguration config, String path) {
		List<Material> materials = new ArrayList<>();
		List<String> values = config.getStringList(path);
		for (String s : values) {
			materials.add(Material.valueOf(s));
		}
		return materials;
	}
	
	public static int tryInt (FileConfiguration config, String path) {
		if (config.contains(path))
			return config.getInt(path);
		else
			return c.getInt(path);
	}
	
	public static double tryDouble (FileConfiguration config, String path) {
		if (config.contains(path))
			return config.getDouble(path);
		else
			return c.getDouble(path);
	}
	
	public static boolean tryBoolean (FileConfiguration config, String path) {
		if (config.contains(path))
			return config.getBoolean(path);
		else
			return c.getBoolean(path);
	}
	
	public static ItemStack tryStack (FileConfiguration config, String path) {
		if (config.contains(path))
			return config.getItemStack(path);
		else
			return c.getItemStack(path);
	}
	
	public static List<ItemStack> tryStackList (FileConfiguration config, String path) {
		if (config.contains(path))
			return (List<ItemStack>) config.getList(path);
		else
			return (List<ItemStack>) c.getList(path);
	}
	
	public static List<Material> tryMaterialList (FileConfiguration config, String path) {
		if (config.contains(path))
			return getMaterialList(config, path);
		else
			return getMaterialList(c, path);
	}
}