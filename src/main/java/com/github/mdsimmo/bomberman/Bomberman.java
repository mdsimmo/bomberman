package com.github.mdsimmo.bomberman;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * THe base plugin class
 */
public class Bomberman extends JavaPlugin {

    private static Bomberman instance;

    /**
     * Gets the Bomberman's plugins instance.
     * @return the instance
     * @throws IllegalStateException if the plugin has not been loaded yet
     */
    public static Bomberman instance() {
        if ( instance == null )
            throw new IllegalStateException( "plugin not loaded yet" );
        return instance;
    }

    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
    }
}
