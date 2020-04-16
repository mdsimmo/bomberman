package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.game.GameSettings;
import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BmSetting implements ConfigurationSerializable {

	String schematicsBuiltin = "schematics/builtin";
	String schematicsCustom = "schematics/custom";
	String gameSaves = "games";
	GameSettings defaultGameSettings = new GameSettings();
	String language = "builtin";

	public File gameSaves() {
		File file = new File(Bomberman.instance.getDataFolder(), gameSaves);
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	public File builtinSaves() {
		File file = new File(Bomberman.instance.getDataFolder(), schematicsBuiltin);
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	public File customSaves() {
		File file = new File(Bomberman.instance.getDataFolder(), schematicsCustom);
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	@Nonnull
	@Override
	public Map<String, Object> serialize() {
		return new HashMap<>(Map.of(
				"schematics-save.builtin", schematicsBuiltin,
				"schematics-save.custom", schematicsCustom,
				"game-saves", gameSaves,
				"default-game-settings", defaultGameSettings,
				"language", language
		));
	}

	@RefectAccess
	public static BmSetting deserialize(Map<String, Object> data) {
		BmSetting settings = new BmSetting();
		settings.schematicsBuiltin = (String) data.get("schematics-save.builtin");
		settings.schematicsCustom = (String) data.get("schematics-save.custom");
		settings.gameSaves = (String) data.get("game-saves");
		settings.defaultGameSettings = (GameSettings) data.get("default-game-settings");
		settings.language = (String) data.get("language");
		return settings;
	}


}
