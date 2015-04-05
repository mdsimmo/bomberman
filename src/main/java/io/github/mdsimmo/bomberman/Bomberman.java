package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.save.BoardSaver;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	@Override
	public void onEnable() {
		instance = this;
		getDataFolder().mkdirs();
		BoardGenerator.copyDefaults();
		BoardSaver.convertOldArenas();
		new BaseCommand();
		Game.loadGames();
	}
	
	@Override
	public void onDisable() {
		for (String game : Game.allGames()) {
			Game.findGame(game).stop();
			Game.findGame(game).saveGame();
		}
	}
	
	public static void main(String[] args) throws Exception {
		YamlConfiguration config = new YamlConfiguration();
		for (Text text : Text.values()) {
			config.set(text.getPath(), text.getDefault());
		}
		config.save(new File("src/main/resources/lang.lang"));
	}
}
