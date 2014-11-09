package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.CommandHandler;
import io.github.mdsimmo.bomberman.save.BoardSaver;

import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	@Override
	public void onEnable() {
		instance = this;
		getDataFolder().mkdirs();
		BoardGenerator.copyDefaults();
		BoardSaver.convertOldArenas();
		new CommandHandler();
		Game.loadGames();
	}
	
	@Override
	public void onDisable() {
		for (String game : Game.allGames()) {
			Game.findGame(game).stop();
			Game.findGame(game).saveGame();
		}
	}
}
