package io.github.mdsimmo.bomberman;

import java.io.File;
import java.io.IOException;

import io.github.mdsimmo.bomberman.arenabuilder.ArenaGenerator;
import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.save.BoardSaver;

import org.bukkit.configuration.file.YamlConfiguration;
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
	
	public static void main( String[] args ) {
		YamlConfiguration c = new YamlConfiguration();
		for ( Phrase text : Text.values() ) {
			c.set( text.getPath(), text.getDefault() );
		}
		System.out.println( new File( "english.lang" ).getAbsolutePath() );
		try {
			c.save( new File( "english.lang" ) );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
}