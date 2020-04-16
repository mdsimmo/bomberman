package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GameRegistry;
import io.github.mdsimmo.bomberman.game.GameSettings;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Bomberman extends JavaPlugin implements Listener {

	@NotNull
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
		List.of("purple.schem")
				.forEach(it -> {
					try (var input = new BufferedInputStream(
							Objects.requireNonNull(getClassLoader().getResourceAsStream(it)))) {
						Path output = new File(settings.builtinSaves(), it).toPath();
						Files.deleteIfExists(output);
						Files.copy(input, output);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}
}