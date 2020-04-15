package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.BaseCommand;
import io.github.mdsimmo.bomberman.game.GameRegestry;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Bomberman extends JavaPlugin implements Listener {

	@Nonnull
	@NotNull
	public static Bomberman instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		getDataFolder().mkdirs();
		extractResources();
		new BaseCommand();
		GameRegestry.loadGames();
	}

	public File schematics() {
		return new File(getDataFolder(), "schematics");
	}

	private void extractResources() {
		schematics().mkdirs();
		List.of("purple.schem")
				.forEach(it -> {
					try (var input = new BufferedInputStream(
							Objects.requireNonNull(getClassLoader().getResourceAsStream(it)))) {
						Path output = new File(schematics(), it).toPath();
						Files.deleteIfExists(output);
						Files.copy(input, output);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}
}