package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Language implements Formattable {
	
	private static Map<String, Language> langs = new HashMap<>();
	private static Plugin plugin = Bomberman.instance;
	private final YamlConfiguration save;
	private final String name;
	
	public static Language getLanguage(String lang) {
		if (lang == null)
			return null;
		lang = lang.toLowerCase();
		Language l = langs.get(lang);
		if (l == null) {
			if (getFile(lang).exists())
				return new Language(lang);
			else
				return null;
		} else {
			return l;
		}
	}
	
	private static File getFile(String lang) {
		return new File(plugin.getDataFolder(), lang.toLowerCase() + ".lang");
	}
	
	public static List<String> allLanguages() {
		File dir = plugin.getDataFolder();
		File[] files =  dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".lang");
			}
		});
		List<String> langs = new ArrayList<String>(files.length);
		for (File f : files) {
			langs.add(Utils.getFileTitle(f.getName()));
		}
		if ( !langs.contains( "english" ) )
			langs.add( "english" );
		return langs;
	}
	
	private Language(String lang) {
		File f = getFile(lang);
		save = YamlConfiguration.loadConfiguration(f);
		name = lang;
	}
	
	public String translate(Text text) {
		String t = save.getString(text.getPath());
		if (t == null) {
			Language bup = getLanguage((String)Config.LANGUAGE.getValue( save ));
			if (bup == null)
				return text.getDefault();
			else
				return bup.translate(text);
		} else
			return t;
	}
	
	@Override
	public Object format( Message message, String value ) {
		return name;
	}
	
}
