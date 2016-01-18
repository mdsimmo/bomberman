package io.github.mdsimmo.bomberman.save;

import io.github.mdsimmo.bomberman.Bomberman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public abstract class Save extends YamlConfiguration {

	protected static final Plugin plugin = Bomberman.instance;
	protected static final String VERSION_PATH = "version";
	protected final File file;
	private static HashMap<String, Version> versions = new HashMap<>();

	public enum Version {
		V0_0_1 ( "0.0.1" ),
		V0_0_2 ( "0.0.2" ),
		V0_0_2a( "0.0.2a" ),
		V0_0_3_SNAPSHOT( "0.0.3-SNAPSHOT" ),
		V0_0_3( "0.0.3" ),
		V0_0_3a( "0.0.3a" ),
		V0_0_3b( "0.0.3b" ),
		V0_0_3c( "0.0.3c" ),
		V0_0_3d( "0.0.3d" ),
		V0_1_0_SNAPSHOT  ( "0.1.0-SNAPSHOT" ),
		V0_1_0_SNAPSHOT_2( "0.1.0-SNAPSHOT-2" ),
		V0_1_0_SNAPSHOT_3( "0.1.0-SNAPSHOT-3" ),
		V0_1_0 ( "0.1.0" ),
		V0_1_0a( "0.1.0a" ),
		V0_1_0b( "0.1.0b" ),
		V0_1_0c( "0.1.0c" ),
		V0_1_0d( "0.1.0d" ),
		V0_1_0e( "0.1.0e" ),
		PAST( "past" ),
		FUTURE(	"future" );

		Version( String name ) {
			versions.put( name, this );
		}

		public static Version from( String version ) {
			if ( version == null )
				return PAST;
			else {
				Version v = versions.get( version );
				return v == null ? FUTURE : versions.get( version );
			}
		}

		public boolean isBefore( Version other ) {
			return this.ordinal() < other.ordinal();
		}

		public boolean isAfter( Version other ) {
			return this.ordinal() > other.ordinal();
		}
	}

	public Save( String name ) {
		this( toFile( name ) );
	}

	public Save( File file ) {
		super();
		this.file = file;
		try {
			load( file );
		} catch ( FileNotFoundException e ) {
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	private static File toFile( String name ) {
		return new File( plugin.getDataFolder(), name.toLowerCase() );
	}

	public void save() {
		try {
			save( file );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * converts the save from the specified version to the current version
	 * 
	 * @param version
	 *            the version that the save is at
	 * @param raw
	 *            the String of what was given in the save file
	 */
	public abstract void convert( Version version );

	public void convert() {
		String raw = getString( VERSION_PATH );
		convert( Version.from( raw ) );
	}

	@Override
	public void save( File file ) throws IOException {
		set( VERSION_PATH, plugin.getDescription().getVersion() );
		super.save( file );
	}

	public void setCompressedSection( String path, CompressedSection value ) {
		super.set( path, value.toString() );
	}

	/**
	 * Gets a Version from this YAML file
	 * 
	 * @return the version
	 */
	public Version getVersion() {
		return Version.from( getVersionRaw() );
	}

	/**
	 * Gets the version String that is in the YAML file
	 */
	public String getVersionRaw() {
		return getString( VERSION_PATH );
	}

}
