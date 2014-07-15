package io.github.mdsimmo.bomberman;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Config {

	private static Plugin plugin = Bomberman.instance;
	private FileConfiguration c = plugin.getConfig();
	
	public static final String FARE_PATH = "stake.fare";
	public static String PRIZE_PATH = "stake.prize";
	public static String LIVES_PATH = "lives";
	public static String BOMBS_PATH = "bombs";
	public static String POWER_PATH = "power";
	public static String DEFAULT_STYLE = "defaultstyle";
	
	protected static ItemStack fare;
	protected static ItemStack prize;
	protected static boolean pot;
	protected static int bombs;
	protected static int lives;
	protected static int power;
	protected static String defaultBoard;
	
	public Config() {
		setupConfig();
		bombs = c.getInt(BOMBS_PATH);
		power = c.getInt(POWER_PATH);
		lives = c.getInt(LIVES_PATH);
		defaultBoard = c.getString(DEFAULT_STYLE);
		
		fare = c.getItemStack(FARE_PATH);
		if (c.getString(PRIZE_PATH).equals("pot")) {
			prize = null;
			pot = true;
		} else {
			prize = c.getItemStack(PRIZE_PATH);
			pot = false;
		}
		
	}
	
	private void setupConfig() {
		c.addDefault(LIVES_PATH, 3);
		c.addDefault(BOMBS_PATH, 3);
		c.addDefault(POWER_PATH, 3);
		c.addDefault(DEFAULT_STYLE, "default");
		c.addDefault(FARE_PATH, new ItemStack(Material.DIAMOND, 1));
		c.addDefault(PRIZE_PATH, "pot");
		c.options().copyDefaults(true);
		plugin.saveConfig();
	}
	
}
