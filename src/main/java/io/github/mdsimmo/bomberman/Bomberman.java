package io.github.mdsimmo.bomberman;

import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	/* TODO FEATURES
	 * kill count (real scores needed first)
	 * let players create styles that go underground
	 * make chests (and other like things) spawn with contents
	*/
	
	/* TODO BUGS
	 * players only get hurt when mostly inside block instead of touching block
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
