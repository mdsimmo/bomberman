package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.arenabuilder.ArenaGenerator;
import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.messaging.Language;
import io.github.mdsimmo.bomberman.prizes.EmptyPayment;
import io.github.mdsimmo.bomberman.prizes.ItemPayment;
import io.github.mdsimmo.bomberman.prizes.PotPayment;
import io.github.mdsimmo.bomberman.prizes.VaultPayment;
import io.github.mdsimmo.bomberman.prizes.XpPayment;
import io.github.mdsimmo.bomberman.save.BoardSaver;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	@Override
	public void onEnable() {
		instance = this;

		// configure serializable things
		ConfigurationSerialization.registerClass( ItemPayment.class );
		ConfigurationSerialization.registerClass( EmptyPayment.class );
		ConfigurationSerialization.registerClass( XpPayment.class );
		ConfigurationSerialization.registerClass( PotPayment.class );
		ConfigurationSerialization.registerClass( VaultPayment.class );

		
		getDataFolder().mkdirs();
		ArenaGenerator.copyDefaults();
		BoardSaver.convertOldArenas();
		new BaseCommand();
		Game.loadGames();
		CommandSign.load();
		DynamicSigns.load();
		SignHandler.load();
		
		// make sure the default language is enabled
		String defLang = Config.LANGUAGE.<String>getValue();
		Language lang = Language.getLanguage( defLang );
		if ( lang == null ) {
			getLogger().warning( "Could not find default language: '" + defLang + "'" );
			if ( defLang != null && defLang.endsWith( ".lang" ) )
				getLogger().warning( "Do not put the '.lang' extention on the language name!" );
		}
	}
	
	@Override
	public void onDisable() {
		CommandSign.save();
		DynamicSigns.save();
		for (String game : Game.allGames()) {
			Game.findGame(game).stop();
			Game.findGame(game).saveGame();
		}
	}
}