package com.github.mdsimmo.bomberman.arenas;

import com.github.mdsimmo.bomberman.utils.Place;
import com.github.mdsimmo.bomberman.utils.Point3D;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.List;

/**
 * An arena defines the block layout of a game. An arena also defines where spawn
 * points are and what blocks are destructible.
 */
public interface Arena extends ConfigurationSerializable {

    /**
     * A generic arena call back interface for use when a long running task
     * return an arena
     */
    interface Callback {
        /**
         * Called when the task finishes it's work
         * @param a the arena that the work was finished with
         */
        void onCallback( Arena a );
    }

    /**
     * Gets the name of this arena.
     * @return the arena's name
     */
    String name();

    /**
     * Tests if the arena contains a point. The point is relative to the arena's
     * origin.
     * @param x arena's x origin
     * @param y arena's y origin
     * @param z arena's z origin
     * @return true if the arena contains the point
     */
    boolean contains( int x, int y, int z );

    /**
     * Tests if the arena contains the point. The point is relative to the arena's
     * origin.
     * @param p where to build the arena
     * @return true if the arena contains the point
     */
    boolean contains( Point3D p );

    /**
     * Gets a list of the spawn points. The spawn points are relative to the
     * arena's origin.
     * @return the arena's origin
     */
    List<Point3D> getSpawnPoints();

    /**
     * Builds the arena at the specified origin. This task will be done over
     * several server ticks. When construction work has finished, the callback
     * method will be informed.
     * @param origin the place to build the arena at
     * @param callback what to inform when the arena has finished being built.
     *                 May be null to have no callback.
     */
    void build( Place origin, Callback callback );


}
