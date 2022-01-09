package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.game.*;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
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
		ConfigurationSerialization.registerClass(BuildFlags.class, "io.github.mdsimmo.bomberman.game.Game$BuildFlags"); // pre v0.8.0
		ConfigurationSerialization.registerClass(Arena.ArenaSettings.class);

		saveResource("sample_config.yml", true);
		FileConfiguration config = getConfig();
		settings = BmSetting.load(config);

		getDataFolder().mkdirs();
		BaseCommand bmCmd = new BaseCommand();
		PluginCommand bukkitBmCmd = Objects.requireNonNull(getCommand("bomberman"));
		bukkitBmCmd.setExecutor(bmCmd);
		bukkitBmCmd.setTabCompleter(bmCmd);

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new BmPlaceholder().register();
		}

		GameSave.updatePre080Saves();

		GameSave.loadGames();
		GamePlayer.setupLoginWatcher();
	}

	public BmSetting getSettings() {
		return settings;
	}

}