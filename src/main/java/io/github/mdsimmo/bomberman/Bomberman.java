package io.github.mdsimmo.bomberman;

import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	/* TODO FEATURES
	 * kill count (real scores needed first)
	 * let players create styles that go underground
	 * make chests (and other like things) spawn with contents
	 * more styles
	*/
	
	/* TODO BUGS
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
			Game.findGame(game).terminate();
			Game.findGame(game).saveGame();
		}
	}
}
