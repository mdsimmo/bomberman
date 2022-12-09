package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.game.GamePlayer;
import io.github.mdsimmo.bomberman.game.GameSave;
import io.github.mdsimmo.bomberman.game.GameSettings;
import io.github.mdsimmo.bomberman.utils.DataRestorer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Bomberman extends JavaPlugin implements Listener {

	@SuppressWarnings("NotNullFieldNotInitialized")
	@Nonnull
	public static Bomberman instance;

	@Override
	public void onEnable() {
		instance = this;

		ConfigurationSerialization.registerClass(GameSettings.class);
		ConfigurationSerialization.registerClass(DataRestorer.class, "io.github.mdsimmo.bomberman.game.Game$BuildFlags");

		getDataFolder().mkdirs();

		BaseCommand bmCmd = new BaseCommand();
		PluginCommand bukkitBmCmd = Objects.requireNonNull(getCommand("bomberman"));
		bukkitBmCmd.setExecutor(bmCmd);
		bukkitBmCmd.setTabCompleter(bmCmd);

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new BmPlaceholder().register();
		}

		// Update the old file system
		GameSave.updatePre080Saves();

		// Archive the old schematics folder
		File schematics = new File(getDataFolder(), "schematics");
		if (schematics.exists()) {
			File schemPath = new File(getDataFolder(), "old/schematics");
			schemPath.getParentFile().mkdirs();
			schematics.renameTo(schemPath);
		}

		// Archive the old config
		FileConfiguration config = getConfig();
		if (config.contains("default-game-settings")) {
			File configFile = new File(getDataFolder(), "config.yml");
			File configOut = new File(getDataFolder(), "old/config.yml");
			configOut.getParentFile().mkdirs();
			configFile.renameTo(configOut);
		}
		new File(getDataFolder(), "sample_config.yml").delete();

		// Copy resources
		saveResource("config.yml", true);
		saveResource("messages.yml", false );
		saveResource("default_messages.yml", true);
		saveResource("games/templates/purple.game.zip", true);
		saveResource("games/README.yml", true);
		saveResource("games/templates/README.txt", true);
		saveResource("temp/README.txt", true);

		GameSave.loadGames();
		GamePlayer.setupLoginWatcher();
	}

	public Path gameSaves() {
		try {
			Path dir = getDataFolder().toPath().resolve("games");
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			return dir;
		} catch (IOException e) {
			throw new RuntimeException("No write access", e);
		}
	}

	public Path templates() {
		try {
			Path dir = getDataFolder().toPath().resolve("games/templates");
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			return dir;
		} catch (IOException e) {
			throw new RuntimeException("No write access", e);
		}
	}

	public Path tempGameData() {
		try {
			Path dir = getDataFolder().toPath().resolve("temp/game");
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			return dir;
		} catch (IOException e) {
			throw new RuntimeException("No write access", e);
		}
	}

	public Path tempPlayerData() {
		try {
			Path dir = getDataFolder().toPath().resolve("temp/player");
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			return dir;
		} catch (IOException e) {
			throw new RuntimeException("No write access", e);
		}
	}

	public Path language() {
		return getDataFolder().toPath().resolve("messages.yml");
	}

}