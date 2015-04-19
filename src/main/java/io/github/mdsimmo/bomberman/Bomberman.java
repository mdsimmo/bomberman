package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.arenabuilder.ArenaGenerator;
import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.save.BoardSaver;

import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	@Override
	public void onEnable() {
		instance = this;
		getDataFolder().mkdirs();
		ArenaGenerator.copyDefaults();
		BoardSaver.convertOldArenas();
		new BaseCommand();
		Game.loadGames();
		CommandSign.load();
		SignHandler.load();
	}
	
	@Override
	public void onDisable() {
		CommandSign.save();
		for (String game : Game.allGames()) {
			Game.findGame(game).stop();
			Game.findGame(game).saveGame();
		}
	}
}