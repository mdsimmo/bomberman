package io.github.mdsimmo.bomberman;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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
	
	protected static ItemStack fare;
	protected static ItemStack prize;
	protected static boolean pot;
	protected static int bombs;
	protected static int lives;
	protected static int power;
	protected static int minPlayers;
	protected static boolean autostart;
	protected static String defaultBoard;
	
	static {
		setupConfig();
		bombs = c.getInt(BOMBS_PATH);
		power = c.getInt(POWER_PATH);
		lives = c.getInt(LIVES_PATH);
		minPlayers = c.getInt(MIN_PLAYERS_PATH);
		autostart = c.getBoolean(AUTOSTART_PATH);
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
	
	private static void setupConfig() {
		c.addDefault(LIVES_PATH, 3);
		c.addDefault(BOMBS_PATH, 3);
		c.addDefault(POWER_PATH, 3);
		c.addDefault(AUTOSTART_PATH, false);
		c.addDefault(MIN_PLAYERS_PATH, 2);
		c.addDefault(DEFAULT_STYLE, "default");
		c.addDefault(FARE_PATH, null);
		c.addDefault(PRIZE_PATH, new ItemStack(Material.DIAMOND, 3));
		
		c.options().copyDefaults(true);
		plugin.saveConfig();
	}
	
}
