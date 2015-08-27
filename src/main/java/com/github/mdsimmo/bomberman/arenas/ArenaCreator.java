package com.github.mdsimmo.bomberman.arenas;

import com.github.mdsimmo.bomberman.Bomberman;
import com.github.mdsimmo.bomberman.utils.Box;
import org.bukkit.plugin.Plugin;

/**
 * An ArenaCreator does all the dirty work of defining a new arena. This class
 * only knows how to create BoxArenas.
 */
final class ArenaCreator {

    private static final Plugin plugin = Bomberman.instance();
    private final String name;
    private final Box box;
    private final Arena.Callback callback;
    private int id = -1;
    private int buildRate = 1000;

    public ArenaCreator( String name, Box box, Arena.Callback callback ) {
        this.name = name;
        this.box = box;
        this.callback = callback;
    }

    public void start() {
        if ( id != -1 )
            throw new IllegalStateException( "ArenaCreator already started" );
        id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask( plugin, new Worker(), 0, 1 );
    }

    private class Worker implements Runnable {

        @Override
        public void run() {
            plugin.getServer().getScheduler().cancelTask( id );
        }
    }

}
