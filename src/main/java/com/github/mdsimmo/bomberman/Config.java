package com.github.mdsimmo.bomberman;

import com.github.mdsimmo.bomberman.utils.Files;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Config implements Configuration {

    public interface ConfigKey<T> {
        String getPath();
        T defaultValue();
    }

    private static Plugin plugin = Bomberman.instance();
    private static Config mainConfig = getConfig( plugin.getConfig() );

    public static Config getConfig( File file ) {
        if ( filename == null )
            throw new NullPointerException( "filename cannot be null" );
        filename = Files.standardise( filename );
        File file = new File( plugin.getDataFolder(), filename );
        return new Config( con);
    }

    public static Config defaultConfig() {
        return mainConfig;
    }

    private final Configuration configuration;

    private Config( Configuration configuration ) {
        this.configuration = configuration;
    }


    @SuppressWarnings( "unchecked" )
    /**
     * Gets the value mapped to the key. If the key does not exist, then the value
     * will try to be taken from the main config. If the main config does not
     * contain the value, then the keys default value is returned.
     */
    public <T> T getValue( ConfigKey<T> key ) {
        if ( key == null )
            throw new NullPointerException( "key cannot be null" );
        String path = key.getPath();
        if ( configuration.contains( path ) )
            return (T) configuration.get( key.getPath() );
        else if ( mainConfig.contains( path ) )
            return (T) mainConfig.get( path );
        else
            return key.defaultValue();
    }

    /**
     * Sets a value in this config
     * @param key the key to use
     * @param value the value to set the key to
     * @param <T> the type of the value to set
     */
    public <T> void set( ConfigKey<T> key, T value ) {
        configuration.set( key.getPath(), value );
    }

    /**
     * Tests if this config contains the specific key. If this config does not contain the key, then this method will
     * <strong>not</strong> lookup the key in the main config.
     * @param key the key to test against
     * @param <T> the type of the key
     * @return true if this config contains the key.
     */
    public <T> boolean contains( ConfigKey<T> key ) {

    }

}
