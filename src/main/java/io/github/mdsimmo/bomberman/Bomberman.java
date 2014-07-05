package io.github.mdsimmo.bomberman;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	private final String ITEM_PATH = "stake-item";
	private final String STAKE_PATH = "stake-amount";
	private final String LIVES_PATH = "lives";
	private final String BOMBS_PATH = "bombs";
	public static Bomberman instance;
	
	/* TODO FEATURES
	 * configuability
	 * Multi game runs
	 * kill count
	 * bombs start other bombs
	*/
	
	/* TODO BUGS (in order id importance)
	 * game reloading is dodgy
	 * games build one block to long
	 * scoring at the end is dodgy (the winneres storage is bad)
	 * players can spawn inside wall slightly
	 * players continue to burn after leaving
	 * chests (and other like things) don't spawn with contents
	 * flames from one bomb put out the flames of another 
	 */
	
	@Override
	public void onEnable() {
		instance = this;
		setupConfig();
		new GameCommander();
		Game.loadGames();
	}
	
	@Override
	public void onDisable() {
		for (Game game : Game.allGames()) {
			game.saveGame();
		}
	}
	
	private void setupConfig() {
		FileConfiguration c = getConfig();
		c.addDefault(ITEM_PATH, "DIAMOND");
		c.addDefault(STAKE_PATH, 3);
		c.addDefault(LIVES_PATH, 3);
		c.addDefault(BOMBS_PATH, 3);
		c.options().copyDefaults(true);
		saveConfig();
	}
}
