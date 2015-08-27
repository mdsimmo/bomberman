package com.github.mdsimmo.bomberman.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * A box is a cubic region of a world. Boxes cannot span across worlds.
 */
public final class Box {

    public Box ofBounds( int x, int y, int z, int xSize, int ySize, int zSize, World world ) {
        return new Box( x, y, z, xSize, ySize, zSize, world );
    }

    /**
     * Returns the smallest possible box that contains both locations.
     * @param a one location
     * @param b another location in the same world
     * @return a box tht bounds both locations
     * @throws IllegalArgumentException if the locations contain different worlds
     */
    public Box bounds( Location a, Location b ) {
        int xMin = Math.min( a.getBlockX(), b.getBlockX() );
        int xMax = Math.max( a.getBlockX(), b.getBlockX() );
        int yMin = Math.min( a.getBlockY(), b.getBlockY() );
        int yMax = Math.max( a.getBlockY(), b.getBlockY() );
        int zMin = Math.min( a.getBlockZ(), b.getBlockZ() );
        int zMax = Math.max( a.getBlockZ(), b.getBlockZ() );
        int xSize = xMax - xMin + 1;
        int ySize = yMax - yMin + 1;
        int zSize = zMax - zMin + 1;
        if ( b.getWorld() != a.getWorld() )
            throw new IllegalArgumentException( "locations had different worlds. Passed"
                    + " a.world=" + a.getWorld().getName()
                    + " b.world=" + b.getWorld().getName() );
        return new Box( xMin, yMin, zMin, xSize, ySize, zSize, a.getWorld() );
    }

    /**
     * Gets the smallest possible box that contains both blocks
     * @param a one block
     * @param b another block
     * @return a box bounding both blocks
     * @throws IllegalArgumentException if the blocks are in different worlds
     */
    public Box bounds( Block a, Block b ) {
        return bounds( a.getLocation(), b.getLocation() );
    }

    private final int x, y, z;
    private final int xSize, ySize, zSize;
    private World world;

    private Box ( int x, int y, int z, int xSize, int ySize, int zSize, World world ) {
        if ( xSize < 0 )
            throw new IllegalArgumentException( "xSize cannot be smaller than 0. Passed x="+xSize );
        if ( ySize < 0 )
            throw new IllegalArgumentException( "ySize cannot be smaller than 0. Passed y="+ySize );
        if ( zSize < 0 )
            throw new IllegalArgumentException( "zSize cannot be smaller than 0. Passed z="+zSize );
        if ( world == null )
            throw new NullPointerException( "world cannot be null" );
        this.x = x;
        this.y = y;
        this.z = z;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.world = world;
    }

    /**
     * Tests if a given point is inside the box
     */
    public boolean contains( int x, int y, int z, World world ) {
        if ( this.world != world )
            return false;
        return     x >= this.x && x < this.x + xSize
                && y >= this.y && y < this.y + ySize
                && z >= this.z && z < this.z + zSize;
    }

    public boolean contains( Location location ) {
        return contains( location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld() );
    }

}
