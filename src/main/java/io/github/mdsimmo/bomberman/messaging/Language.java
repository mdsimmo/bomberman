package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.Bomberman;

import java.io.File;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Language {
	
	private static HashMap<String, Language> langs = new HashMap<>();
	private static Plugin plugin = Bomberman.instance;
	private final YamlConfiguration save;
	
	public static Language getLanguage(String lang) {
		Language l = langs.get(lang);
		if (l == null) {
			if (new File(plugin.getDataFolder(), lang).exists())
				return new Language(lang);
			else
				return null;
		} else {
			return l;
		}
	}
	
	private Language(String lang) {
		File f = new File(plugin.getDataFolder(), lang);
		save = YamlConfiguration.loadConfiguration(f);
	}
	
	public String translate(Text text) {
		String t = save.getString(text.path);
		if (t == null)
			return text.message;
		else
			return t;
	}
	
}
