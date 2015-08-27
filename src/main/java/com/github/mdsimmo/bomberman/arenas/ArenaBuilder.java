package com.github.mdsimmo.bomberman.arenas;

import com.github.mdsimmo.bomberman.Bomberman;
import com.github.mdsimmo.bomberman.arenas.blocks.BlockRep;
import com.github.mdsimmo.bomberman.utils.Place;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * The ArenaBuilder does all the dirty work of actually building an arena. This
 * class only knows how to work with the BoxArena class as it needs access to
 * the arena's internal block data and needs the arena to be box shaped.
 */
final class ArenaBuilder {

    private static Plugin plugin = Bomberman.instance();

    private final BoxArena arena;
    private final Place place;
    private final Arena.Callback callback;
    private int id = -1;
    private final int buildRate;
    private final int buildSize;
    private int count, tick;
    private final Location temp;
    private final Vector temp2 = new Vector();

    public ArenaBuilder( BoxArena arena, Place place, Arena.Callback callback ) {
        this.arena = arena;
        this.place = place;
        temp = place.toLocation();
        this.callback = callback;
        buildRate = arena.getBuildRate();
        buildSize = arena.getBlockCount();
    }

    public void start() {
        if ( id != -1 )
            throw new IllegalStateException( "ArenaBuilder already started" );
        id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask( plugin, new Worker(), 0, 1 );
        tick = count = 0;
    }

    private class Worker implements Runnable {

        @Override
        public void run() {
            tick++;
            while ( true ) {
                // make sure we haven't worked too hard this tick
                if ( buildRate > 0 && count > tick * buildRate )
                    return;
                // check if build is finished
                if ( count >= buildSize ) {
                    plugin.getServer().getScheduler().cancelTask( id );
                    callback.onCallback( arena );
                    return;
                }
                // build one more block
                place.toLocation( temp );
                BlockRep block = arena.getBlock( count, temp2 );
                temp.add( temp2 );
                block.setBlock( temp.getBlock() );
                count++;
            }
        }
    }
}