package com.github.mdsimmo.bomberman.arenas;

import com.github.mdsimmo.bomberman.arenas.blocks.BlockRep;
import com.github.mdsimmo.bomberman.utils.Box;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * An arena is a styling of a game
 */
public final class Arena {

    public static Arena createFrom( Box ... boxs ) {
        return null;
    }

    public interface ArenaCallback {
        void onCallback( Arena a );
    }

    private final BlockRep[] blocks;


    /**
     * Builds an arena at the specified location. This does not happen instantly:
     * it will happen over multiple steps.
     * @param l the location to build the arena at
     * @param callback called when construction has finished
     */
    public void build( Location l, ArenaCallback callback ) {

    }

    void convertToVector( int block, Vector storage ) {
    }

    BlockRep block( int block ) {
        return blocks[block];
    }

}
