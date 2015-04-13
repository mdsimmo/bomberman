package io.github.mdsimmo.bomberman.save;

import io.github.mdsimmo.bomberman.Bomberman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public abstract class Save extends YamlConfiguration {

	protected static final Plugin plugin = Bomberman.instance;
	protected static final String VERSION_PATH = "version";
	protected final File file;
	private static HashMap<String, Version> versions = new HashMap<>();
	
	protected enum Version {
		V0_0_1("0.0.1"),
		V0_0_2("0.0.2"),
		V0_0_2a("0.0.2a"),
		V0_0_3_SNAPSHOT("0.0.3-SNAPSHOT"),
		V0_0_3("0.0.3"),
		V0_0_3a("0.0.3a"),
		V0_0_3b("0.0.3b"),
		V0_0_3c("0.0.3c"),
		V0_0_3d("0.0.3d"),
		V0_1_0("0.1.0"),
		V0_1_0_SNAPSHOT("0.1.0-SNAPSHOT"),
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
		set(VERSION_PATH, plugin.getDescription().getVersion());
		try {
			super.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * converts the save from the specified version to the current version
	 * @param version the version that the save is at
	 * @param raw the String of what was given in the save file
	 */
	public abstract void convert(Version version, String raw);
	
	public void convert() {
		String raw = getString(VERSION_PATH);
		convert(Version.from(raw), raw);
	}
	
	/**
	 * This will just throw a {@link UnsupportedOperationException}. DO NOT USE
	 * @deprecated use save().
	 */
	@Deprecated
	@Override
	public void save(File file) throws IOException {
		throw new UnsupportedOperationException( "Use save()" );
	}
	
	/**
	 * This will just throw a {@link UnsupportedOperationException}. DO NOT USE
	 * @deprecated use save().
	 */
	@Deprecated
	@Override
	public void save(String file) throws IOException {
		throw new UnsupportedOperationException( "Use save()" );
	}
	
	@Override
	public void set(String path, Object value) {
		if (value instanceof CompressedSection) {
			super.set(path, ((CompressedSection)value).content.toString());
		} else {
			super.set(path, value);
		}
	}
	
	public CompressedSection getCompressedSection(String path) {
		CompressedSection section = new CompressedSection();
		section.setValue(getString(path));
		section.seperator = section.content.charAt(section.content.length());
		return section;
	}
	
	
	/**
	 * Gets a Version from this YAML file
	 * @return the version
	 */
	public Version getVersion() {
		return Version.from(getVersionRaw());
	}
	
	/**
	 * Gets the version String that is in the YAML file 
	 */
	public String getVersionRaw() {
		return getString(VERSION_PATH);
	}
	
	public static class CompressedSection {

		private StringBuilder content = new StringBuilder();
		private char seperator;
		
		public CompressedSection(char seperator) {
			this.seperator = seperator;
		}
		
		public CompressedSection() {
			this(';');
		}
		
		public void addParts(Object... parts) {
			for (Object part : parts) {
				if ( content.length() != 0 )
					content.append( seperator );
				content.append( part.toString() );
			}
		}

		/**
		 * reads the next part of the file.
		 * 
		 * @return the read part. null if the end is reached
		 */
		public List<String> readParts() {
			List<String> parts = new ArrayList<>();
			String part = "";
			int length = content.length();
			for (int i = 0; i < length; i++ ) {
				char c = content.charAt( i );
				if (c == seperator) {
					parts.add(part);
					part = "";
				} else {
					part += c;
				}
			}
			if (part != "")
				parts.add(part);
			return parts;
		}

		public void reset() {
			content.setLength( 0 );
		}
		
		public void setValue(String value) {
			if (value == null)
				throw new NullArgumentException("value cannot be null");
			reset();
			content.append( value );
		}
		
		/**
		 * Turns the file into the savable string
		 */
		@Override
		public String toString() {
			return content.toString();
		}
	}
}
