package io.github.mdsimmo.bomberman.save;

import io.github.mdsimmo.bomberman.Bomberman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public abstract class Save extends YamlConfiguration {

	protected static final Plugin plugin = Bomberman.instance;
	protected final File file;
	private static HashMap<String, Version> versions = new HashMap<>();
	
	protected enum Version {
		V0_0_1("0.0.1"),
		V0_0_2("0.0.2"),
		V0_0_2a("0.0.2a"),
		V0_0_3_SNAPSHOT("0.0.3-SNAPSHOT"),
		V0_0_3("0.0.3"),
		PAST("past"),
		FUTURE("future");
		
		Version(String name) {
			versions.put(name, this);
		}
		
		public static Version from(String version) {
			if (version == null)
				return PAST;
			else {
				Version v = versions.get(version);
				return v == null ? FUTURE : versions.get(version);
			}
		}
	}
	
	public Save (String name) {
		this(toFile(name));
		
	}
	
	public Save(File file) {
		super();
		this.file = file;
		try {
			load(file);
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static File toFile(String name) {
		return new File(plugin.getDataFolder(), name.toLowerCase());
	}
	
	public void save() {
		set("version", plugin.getDescription().getVersion());
		try {
			super.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * converts the save from the specified version to the current version
	 * @param version the verion that the save is at
	 */
	public abstract void convert(Version version);
	
	/**
	 * @deprecated use save()
	 */
	@Deprecated
	@Override
	public void save(File file) throws IOException {
		super.save(file);
	}
	
	/**
	 * @deprecated use save()
	 */
	@Deprecated
	@Override
	public void save(String file) throws IOException {
		super.save(file);
	}
	
	@Override
	public void set(String path, Object value) {
		if (value instanceof CompressedSection) {
			super.set(path, ((CompressedSection)value).value);
		} else {
			super.set(path, value);
		}
	}
	
	public CompressedSection getCompressedSection(String path) {
		CompressedSection section = new CompressedSection();
		section.setValue(getString(path));
		section.seperator = section.value.charAt(section.value.length());
		return section;
	}
	
	/**
	 * Gets a Version from this YAML file
	 * @param path the path to read from
	 * @return the version
	 */
	public Version getVersion(String path) {
		String v = getString(path);
		return Version.from(v);
	}
	
	public static class CompressedSection {

		private String value = "";
		private char seperator;
		
		public CompressedSection(char seperator) {
			this.seperator = seperator;
		}
		
		public CompressedSection() {
			this(';');
		}
		
		public void addParts(Object... parts) {
			for (Object part : parts)
				value += part.toString() + seperator;
		}

		/**
		 * reads the next part of the file.
		 * 
		 * @return the read part. null if the end is reached
		 */
		public List<String> readParts() {
			List<String> parts = new ArrayList<>();
			String part = "";
			for (char c : value.toCharArray()) {
				if (c == seperator) {
					parts.add(part);
					part = "";
				} else {
					part += c;
				}
			}
			return parts;
		}

		public void reset() {
			value = "";
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		/**
		 * Turns the file into the savable string
		 */
		@Override
		public String toString() {
			return value;
		}
	}
}
