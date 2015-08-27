package com.github.mdsimmo.bomberman.arenas;

import com.github.mdsimmo.bomberman.Bomberman;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaConfig {

    private static final Map<String, UUID> lookup = new HashMap<String, UUID>();
    private static final Plugin plugin = Bomberman.instance();
    private static final File arenaDirectory = new File( plugin.getDataFolder(), "arenas" );
    private static final Configuration config = YamlConfiguration.loadConfiguration( new File( plugin.getDataFolder(), "arenas.yml" ) );

    static {
        arenaDirectory.mkdirs();
        // TODO store arena's in a file
    }

    public static File locate( String arenaname ) {
        return arenaDirectory;
    }

}
