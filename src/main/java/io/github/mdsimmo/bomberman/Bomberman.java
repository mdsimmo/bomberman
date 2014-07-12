package io.github.mdsimmo.bomberman;

import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	/* TODO FEATURES
	 * kill count
	 * let players create styles that go underground
	 * randomly remove dirt
	 * make chests (and other like things) spawn with contents
	*/
	
	/* TODO BUGS
	 * games build one block to long (but only default board????)
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
