package com.github.mdsimmo.bomberman.arenas;

import com.github.mdsimmo.bomberman.arenas.blocks.BlockRep;
import com.github.mdsimmo.bomberman.utils.Box;
import com.github.mdsimmo.bomberman.utils.Place;
import com.github.mdsimmo.bomberman.utils.Point3D;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.Vector;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A basic implementation of an arena. This implementation only knows how to
 * handle structures bound by a single box region.
 */
public class BoxArena implements Arena {

    static {
        ConfigurationSerialization.registerClass( BoxArena.class );
    }

    /**
     * Defines a new arena. The method will schedule a background task to create
     * the new arena over several ticks.
     * @param name the name of the new arena. If an arena with the same name
     *             already existed, then it will be overridden
     * @param box the box to define the new arena from
     * @param callback gives the created arena back
     */
    public static void createArena( String name, Box box, Arena.Callback callback ) {
        if ( name == null )
            throw new NullPointerException( "name is null" );
        if ( box == null )
            throw new NullPointerException( "box cannot be null" );
        if ( callback == null )
            throw new NullPointerException( "callback cannot be null" );
        ArenaCreator creator = new ArenaCreator( name, box, callback );
        creator.start();
    }

    /**
     * The name of the arena
     */
    private final String name;
    /**
     * All the normal blocks in the arena. Blocks are ordered so that the blocks
     * will be iterated through in z, y, x axis order
     */
    private final BlockRep[] blocks;
    /**
     * All the blocks that must be placed after the normal blocks have been
     * placed. These blocks have a one-to-one mapping with the delayedLocations
     * array
     */
    private final BlockRep[] delayedBlocks;
    /**
     * The locations of all the delayed blocks.
     */
    private final Point3D[] delayedLocations;
    /**
     * The dimensions of the arena
     */
    private final int xSize, ySize, zSize;
    /**
     * The rate that the arena will be built at
     * TODO remove hard-coded build rate
     */
    private final int buildRate = 100;

    BoxArena( String name, int xSize, int ySize, int zSize, BlockRep[] blocks, BlockRep[] delayedBlocks, Point3D[] delayedLocations ) {
        if ( name == null )
            throw new NullPointerException( "name cannot be null" );
        if ( xSize < 0 || ySize < 0 || zSize < 0 )
            throw new IllegalArgumentException( "Passed a negative dimension. Given" +
                    " xSize=" + xSize +
                    " ySize=" + ySize +
                    " zSize=" + zSize );
        if ( blocks == null )
            throw new NullPointerException( "blocks cannot be null" );
        if ( delayedBlocks == null )
            throw new NullPointerException( "delayedBlocks cannot be null" );
        if ( delayedLocations == null )
            throw new NullPointerException( "delayedLocation cannot be null" );
        if ( blocks.length != xSize * ySize * zSize )
            throw new IllegalArgumentException( "block's length (" + blocks.length
                    + ") was not equal to arena's volume (" + xSize*ySize*zSize + ")" );
        if ( delayedBlocks.length != delayedLocations.length )
            throw new IllegalArgumentException( "delayed block's length (" + delayedBlocks.length
                    + ") must be equal to delayed locations length (" + delayedLocations.length + ")"  );
        this.name = name;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.blocks = blocks;
        this.delayedBlocks = delayedBlocks;
        this.delayedLocations = delayedLocations;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean contains( int x, int y, int z ) {
        return x > 0 && y >= 0 && z >= 0 && x < xSize && y < ySize && z < zSize;
    }

    @Override
    public boolean contains( Point3D p ) {
        return contains( p.x, p.y, p.z );
    }

    @Override
    public List<Point3D> getSpawnPoints() {
        return null;
    }

    int getBuildRate() {
        return buildRate;
    }

    @Override
    public void build( Place origin, Arena.Callback callback ) {
        ArenaBuilder builder = new ArenaBuilder( this, origin, callback );
        builder.start();
    }

    // METHODS BELOW HERE ARE ALL FOR INTERNAL USE ONLY

    /**
     * Gets the amount of blocks that must be iterated through to completely
     * build the arena
     */
    int getBlockCount() {
        return blocks.length + delayedBlocks.length;
    }

    /**
     * For internal use only: gets the block at a specific id location
     * @param id the id number. Must be between 0 and getBlockCount()
     * @param storage a Vector to store the relative location of the block into
     * @return the BlockRep at that location
     */
    BlockRep getBlock( int id, Vector storage ) {
        if ( id < blocks.length ) {
            // a normal block
            storage.setZ( id % zSize );
            storage.setY( ( id / zSize ) % ySize  );
            storage.setX( id / zSize / ySize );
            return blocks[id];
        } else {
            // a delayed block
            id -= blocks.length;
            Point3D p = delayedLocations[id];
            storage.setX( p.x ).setY( p.y ).setZ( p.z );
            return delayedBlocks[id];
        }

    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "name", name );
        return map;
    }

    public static BoxArena deserialize( Map<String, Object> map ) {
        String name = (String)map.get( "name" );
        BoxArena arena;
        try {
            arena = ArenaLoader.load( name );
        } catch ( FileNotFoundException e ) {
            throw new IllegalArgumentException( "unknown arena " + name );
        }
        return arena;
    }
}
