package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GameRegistry;
import io.github.mdsimmo.bomberman.game.GameSettings;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Bomberman extends JavaPlugin implements Listener {

	@SuppressWarnings("NotNullFieldNotInitialized")
	@Nonnull
	public static Bomberman instance;

	private BmSetting settings;

	@Override
	public void onEnable() {
		instance = this;

		ConfigurationSerialization.registerClass(Game.class);
		ConfigurationSerialization.registerClass(GameSettings.class);
		ConfigurationSerialization.registerClass(BmSetting.class);

		settings = getConfig().getObject("config", BmSetting.class, new BmSetting());

		getDataFolder().mkdirs();
		extractResources();
		new BaseCommand();
		GameRegistry.loadGames();
	}

	public BmSetting getSettings() {
		return settings;
	}

	private void extractResources() {
		String[] schematics = new String[] {
		        "purple.schem"
        };
	    for (String schem : schematics) {
			try (InputStream input = new BufferedInputStream(
					Objects.requireNonNull(getClassLoader().getResourceAsStream(schem)))) {
				Path output = new File(settings.builtinSaves(), schem).toPath();
				Files.deleteIfExists(output);
				Files.copy(input, output);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}