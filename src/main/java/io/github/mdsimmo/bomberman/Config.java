package io.github.mdsimmo.bomberman;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Config {

	private static Plugin plugin = Bomberman.instance;
	private FileConfiguration c = plugin.getConfig();
	
	private final String ITEM_PATH = "stake.item";
	private final String STAKE_PATH = "stake.amount";
	private final String LIVES_PATH = "lives";
	private final String BOMBS_PATH = "bombs";
	private final String POWER_PATH = "power";
	private final String DEFAULT_STYLE = "defaultstyle";
	
	protected static ItemStack stake;
	protected static int bombs;
	protected static int lives;
	protected static int power;
	protected static String defaultBoard;
	
	public Config() {
		setupConfig();
		Material item = Material.getMaterial(c.getString(ITEM_PATH));
		int amount = c.getInt(STAKE_PATH);
		stake = new ItemStack(item, amount);
		bombs = c.getInt(BOMBS_PATH);
		power = c.getInt(POWER_PATH);
		lives = c.getInt(LIVES_PATH);
		defaultBoard = c.getString(DEFAULT_STYLE);		
		
	}
	
	private void setupConfig() {
		c.addDefault(ITEM_PATH, Material.DIAMOND.toString());
		c.addDefault(STAKE_PATH, 3);
		c.addDefault(LIVES_PATH, 3);
		c.addDefault(BOMBS_PATH, 3);
		c.addDefault(POWER_PATH, 3);
		c.addDefault(DEFAULT_STYLE, "default");
		c.options().copyDefaults(true);
		plugin.saveConfig();
	}
	
}
