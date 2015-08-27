package com.github.mdsimmo.bomberman.arenas;

import com.github.mdsimmo.bomberman.Bomberman;
import com.github.mdsimmo.bomberman.Config;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * The ArenaBuilder does all the dirty work of actually building an arena.
 */

final class ArenaBuilder implements Runnable {
    public interface BuildListener {
        void onComplete();
    }
    private static Config.ConfigKey<Integer> buildRateKey = new Config.ConfigKey<Integer>() {
        @Override
        public String getPath() {
            return "build-rate";
        }
        @Override
        public Integer defaultValue() {
            return 100;
        }
    };

    private static Plugin plugin = Bomberman.instance();

    private final Arena arena;
    private final Location location;
    private final BuildListener listener;
    private final int id;
    private final int buildRate;
    private int count, tick;
    private final Location temp;
    private final Vector temp2 = new Vector();

    public ArenaBuilder( Arena arena, Location location, BuildListener l ) {
        this.arena = arena;
        this.location = location;
        temp = location.clone();
        this.listener = l;
        id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask( plugin, this, 0, 1 );
        buildRate = arena.config().getValue( buildRateKey );
        tick = count = 0;
    }

    @Override
    public void run() {
        tick++;
        while ( true ) {
            // make sure we haven't worked too hard this tick
            if ( buildRate > 0 && count > tick * buildRate)
                return;
            Location l = temp;
            l.setX( location.getX() );
            l.setY( location.getY() );
            l.setZ( location.getZ() );
            arena.convertToVector( count, temp2 );
            l.add( temp2 );
            arena.block( count ).setBlock( l.getBlock() );
            count++;
            if (count >= arena.xSize * board.ySize * board.zSize) {
                // finishing touches
                for (Vector v : board.delayed.keySet()) {
                    l = location.clone().add(v);
                    board.delayed.get(v).setBlock(l.getBlock());
                }
                plugin.getServer().getScheduler().cancelTask( id );
                if ( listener != null )
                    listener.onComplete();
                return;
            }
        }
    }
}