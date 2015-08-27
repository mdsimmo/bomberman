package com.github.mdsimmo.bomberman.arenas;

import com.github.mdsimmo.bomberman.Bomberman;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * The arena loader is responsible for loading arenas from save files
 */
class ArenaLoader {

    private static final Plugin plugin = Bomberman.instance();
    private static final HashMap<String, WeakReference<BoxArena>> arenas = new HashMap<String, WeakReference<BoxArena>>();
    private static final File arenasDirectory = new File( plugin.getDataFolder(), "arenas" );
    private static final Configuration config;
    static {
        config = YamlConfiguration.loadConfiguration( new File( plugin.getDataFolder(), "arenanames.yml" ) );
        arenasDirectory.mkdirs();
    }

    /**
     * Loads the arena from storage. If the arena is already being used, then
     * the loaded arena will be returned. If there is no arena with the given
     * name, then a FileNotFoundException will be thrown.
     * @param name the name of the arena to load
     * @return the loaded arena - never null
     * @throws FileNotFoundException when the arena's name doesn't match any saved arenas
     */
    public static BoxArena load( String name ) throws FileNotFoundException {
        return null;
    }

}
