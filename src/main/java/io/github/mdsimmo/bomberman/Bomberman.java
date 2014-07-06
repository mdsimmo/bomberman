package io.github.mdsimmo.bomberman;

import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	/* TODO FEATURES
	 * Multi game runs
	 * kill count
	 * bombs start other bombs
	 * let players create styles that go underground
	*/
	
	/* TODO BUGS (in order id importance)
	 * games build one block to long
	 * players can spawn inside wall slightly
	 * players continue to burn after leaving
	 * chests (and other like things) don't spawn with contents
	 * flames from one bomb put out the flames of another
	 * convert-to-game builds another board where the player is standing 
	 */
	
	@Override
	public void onEnable() {
		instance = this;
		new Config();
		new GameCommander();
		Game.loadGames();
	}
	
	@Override
	public void onDisable() {
		for (String game : Game.allGames()) {
			Game.findGame(game).saveGame();
		}
	}
}
