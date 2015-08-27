package com.github.mdsimmo.bomberman.utils;

import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.Serializable;

/**
 * A region defines a collection of a bunch of blocks. Regions can be any shape
 * or size and can span multiple worlds. A region should be immutable.
 */
public interface Region extends Serializable, ConfigurationSerializable {

    /**
     * Tests if the x, y and z points of the given world are within this region.
     * @param w the world
     * @param x the x location
     * @param y the y location
     * @param z the z location
     * @return true if the points are within the bounds
     */
    boolean contains( World w, int x, int y, int z );

    /**
     * Convenience method for {@code contains( p.world, p.x, p.y, p.z )}.
     * However, implementations may have optimised this method.
     * @param p the place to test for
     * @return true if the place is inside this region
     */
    boolean contains( Place p );

    /**
     * Creates a new region that is the same as this region but shifted by the
     * specified amount.
     * @param x the x amount to shift by
     * @param y the y amount to shift by
     * @param z the z amount to shift by
     * @return the shifted region
     */
    Region shift( int x, int y, int z );

}
