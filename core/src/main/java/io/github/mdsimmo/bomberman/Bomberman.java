package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GamePlayer;
import io.github.mdsimmo.bomberman.game.GameSettings;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
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

		ConfigurationSerialization.registerClass(GameSettings.class);
		ConfigurationSerialization.registerClass(Game.BuildFlags.class);

		saveResource("sample_config.yml", true);
		FileConfiguration config = getConfig();
		settings = BmSetting.load(config);

		getDataFolder().mkdirs();
		extractResources();
		BaseCommand bmCmd = new BaseCommand();
		PluginCommand bukkitBmCmd = Objects.requireNonNull(getCommand("bomberman"));
		bukkitBmCmd.setExecutor(bmCmd);
		bukkitBmCmd.setTabCompleter(bmCmd);

		Game.loadGames();

		GamePlayer.setupLoginWatcher();
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