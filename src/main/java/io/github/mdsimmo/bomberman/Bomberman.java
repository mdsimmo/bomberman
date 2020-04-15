package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.game.GameRegestry;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class Bomberman extends JavaPlugin implements Listener {

	@Nonnull
	@NotNull
	public static Bomberman instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		getDataFolder().mkdirs();
		new BaseCommand();
		GameRegestry.loadGames();

		// make sure the default language is enabled
		String defLang = Config.LANGUAGE.getValue();
	}
}