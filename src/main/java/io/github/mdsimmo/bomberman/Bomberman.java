package io.github.mdsimmo.bomberman;

import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	/* TODO FEATURES
	 * Multi game runs
	 * kill count
	 * inform players who killed who
	 * bombs start other bombs
	 * let players create styles that go underground
	 * make bomb explosions more intuitive
	 * randomly remove dirt (bomb explosions should be fixed first)
	 * make chests (and other like things) spawn with contents
	*/
	
	/* TODO BUGS
	 * games build one block to long (but only default board????)
	 * flames from one bomb put out the flames of another
	 * players can only be hurt when on burnable tile
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
