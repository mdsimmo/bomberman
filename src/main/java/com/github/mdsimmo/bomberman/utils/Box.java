package com.github.mdsimmo.bomberman.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A box is a cuboid region of a single world.
 */
public final class Box implements Region {

    public static Box ofBounds( int x, int y, int z, int xSize, int ySize, int zSize, World world ) {
        return new Box( x, y, z, xSize, ySize, zSize, world );
    }

    /**
     * Returns the smallest possible box that contains both locations.
     * @param a one location
     * @param b another location in the same world
     * @return a box tht bounds both locations
     * @throws NullPointerException if either a or b is null
     * @throws IllegalArgumentException if the locations contain different worlds
     */
    public static Box bounds( Place a, Place b ) {
        if ( a == null )
            throw new NullPointerException( "point a cannot be null" );
        if ( b == null )
            throw new NullPointerException( "point b cannot be null" );
        if ( b.world != a.world )
            throw new IllegalArgumentException( "locations had different worlds. Passed"
                    + " a.world=" + a.world.getName()
                    + " b.world=" + b.world.getName() );

        int xMin = Math.min( a.x, b.x );
        int xMax = Math.max( a.x, b.x );
        int yMin = Math.min( a.y, b.y );
        int yMax = Math.max( a.y, b.y );
        int zMin = Math.min( a.z, b.z );
        int zMax = Math.max( a.z, b.z );
        int xSize = xMax - xMin + 1;
        int ySize = yMax - yMin + 1;
        int zSize = zMax - zMin + 1;

        return new Box( xMin, yMin, zMin, xSize, ySize, zSize, a.world );
    }

    /**
     * Gets the smallest possible box that contains both blocks
     * @param a one block
     * @param b another block
     * @return a box bounding both blocks
     * @throws NullPointerException if a or b is null
     * @throws IllegalArgumentException if the blocks are in different worlds
     */
    public static Box bounds( Block a, Block b ) {
        return bounds( Place.of( a ), Place.of( b ) );
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

    @Override
    public boolean contains( World w, int x, int y, int z  ) {
        return this.world.equals( world )
                && x >= this.x && x < this.x + xSize
                && y >= this.y && y < this.y + ySize
                && z >= this.z && z < this.z + zSize;
    }

    @Override
    public boolean contains( Place p ) {
        return contains( p.world, p.x, p.y, p.z );
    }

    @Override
    public Region shift( int x, int y, int z ) {
        return new Box( this.x + x, this.y + y, this.z + z, this.xSize, this.ySize, this.zSize, this.world );
    }

    /**
     * For serialization proposes only. DO NOT USE
     * @return a map representing this box
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>( 7 );
        map.put( "x", x );
        map.put( "y", y );
        map.put( "z", z );
        map.put( "xSize", xSize );
        map.put( "ySize", ySize );
        map.put( "zSize", zSize );
        map.put( "world", world.getUID() );
        return map;
    }

    /**
     * For serialization proposes only. DO NOT USE
     * @param map the data to use
     * @return a box based on the  given map
     */
    public static Box deserialize( Map<String, Object> map ) {
        int x = (Integer)map.get( "x" );
        int y = (Integer)map.get( "y" );
        int z = (Integer)map.get( "z" );
        int xSize = (Integer)map.get( "xSize" );
        int ySize = (Integer)map.get( "ySize" );
        int zSize = (Integer)map.get( "zSize" );
        UUID worldUUID = (UUID)map.get( "world" );
        World world = Bukkit.getWorld( worldUUID );
        if ( world == null )
            throw new IllegalArgumentException( "cannot find world with uuid: " + worldUUID );
        return new Box( x, y, z, xSize, ySize, zSize, world );
    }
}
