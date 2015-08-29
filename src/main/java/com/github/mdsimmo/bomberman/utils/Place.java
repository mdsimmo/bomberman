package com.github.mdsimmo.bomberman.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Designed to be similar to Bukkit's Location class, except is immutable, uses
 * integers and does not have an associated direction.
 */
public final class Place implements Serializable, ConfigurationSerializable {

    /**
     * Creates a Place from a Location. The x, y and z components will be taken
     * from the locations {@link Location#getBlockX()} methods.
     * @param location the location to construct from
     * @return the blocks
     * @throws NullPointerException if location is null or location's world is null
     */
    public static Place from( Location location ) {
        if ( location == null )
            throw new NullPointerException( "location cannot be null" );
        if ( location.getWorld() == null )
            throw new NullPointerException( "world cannot be null" );
        return new Place( location.getWorld(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ() );
    }

    /**
     * Creates a Place from the passed components
     * @param world the world the place is in
     * @param x the x place
     * @param y the y place
     * @param z the z place
     * @return the created Place
     * @throws NullPointerException if world is null
     */
    public static Place from( World world, int x, int y, int z ) {
        if ( world == null )
            throw new NullPointerException( "world cannot be null" );
        return new Place( world, x, y, z );
    }

    /**
     * Gets the place that a block is at.
     * @param block the block
     * @return the block's place
     */
    public static Place of( Block block ) {
        if ( block == null )
            throw new NullPointerException( "block cannot be null" );
        if ( block.getWorld() == null )
            throw new NullPointerException( "block's world cannot be null" );
        return new Place( block.getWorld(), block.getX(), block.getY(), block.getZ() );
    }

    public final World world;
    public final int x, y, z;
    private transient int hashCode;
    private transient boolean hashMade = false;

    Place( World world, int x, int y, int z ) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Adds each component to this place
     * @param x the x value to add
     * @param y the y value to add
     * @param z the z value to add
     * @return the new place
     */
    public Place plus( int x, int y, int z ) {
        return new Place( this.world, this.x + x, this.y + y, this.z + z );
    }

    /**
     * Adds the point onto this point
     * @param p the point to add on
     * @return a new place
     */
    public Place plus( Point3D p ) {
        return new Place( world, x + p.x, y + p.y, z + p.z );
    }

    /**
     * Gets the distance between two places. If the places are in different worlds,
     * then an exception is thrown.
     * @param other the first place
     * @return the point representing the distance between the points
     * @throws IllegalArgumentException if the places are in different worlds
     */
    public Point3D distance( Place other ) {
        if ( other.world != world )
            throw new IllegalArgumentException( "places cannot have different worlds" );
        int xDist = x - other.x;
        int yDist = y - other.y;
        int zDist = z - other.z;
        return Point3D.from( xDist, yDist, zDist );
    }

    /**
     * Subtracts each component from this place
     * @param x the x component to subtract
     * @param y the y component to subtract
     * @param z the z component to subtract
     * @return a new place
     */
    public Place minus( int x, int y, int z ) {
        return new Place( world, this.x - x, this.y - y, this.z - z );
    }

    /**
     * Subtracts each component of the point from this place
     * @param p the point to subtract
     * @return a new place
     */
    public Place minus( Point3D p ) {
        return new Place( world, x - p.x, y - p.y, z - p.z );
    }

    /**
     * Multiplies each component by a value
     * @param scalar the amount to scale each value by
     * @return a new place
     */
    public Place times( int scalar ) {
        return new Place( world, x * scalar, y * scalar, z * scalar );
    }

    /**
     * Divides each component by an amount
     * @param divider the amount to divide by
     * @return a new place
     */
    public Place divide( int divider ) {
        return new Place( world, x / divider, y / divider, z / divider );
    }

    /**
     * Converts this point to a {@link Location}
     * @return a new location
     */
    public Location toLocation() {
        return new Location( world, x, y, z );
    }

    /**
     * Alters the passed location to represent the same location as this.
     * @param storage the Location to alter
     */
    public void toLocation( Location storage ) {
        storage.setWorld( world );
        storage.setX( x );
        storage.setX( y );
        storage.setX( z );
    }

    /**
     * Gets the block that is at this location
     * @return the block
     */
    public Block getBlock() {
        return world.getBlockAt( x, y, z );
    }

    /**
     * Another point is only equal if it is of type Place and it has x, y and z
     * values equal to this points and the worlds are equal.
     * @param obj the object to test against
     * @return true if the objects are equal
     */
    @Override
    public boolean equals( Object obj ) {
        if ( !(obj instanceof Place) )
            return false;
        Place o = (Place)obj;
        return o.world.equals( world ) && x == o.x && y == o.y && z == o.z;
    }

    @Override
    public int hashCode() {
        if ( hashMade )
            return hashCode;
        int hash = 31;
        hash = hash * world.hashCode();
        hash = hash * 31 + x;
        hash = hash * 31 + y;
        hash = hash * 31 + z;
        hashCode = hash;
        hashMade = true;
        return hash;
    }

    /**
     * For serialization purposes only. DO NOT USE
     * @return a map representing this place
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>( 4 );
        map.put( "world", world.getUID() );
        map.put( "x", x );
        map.put( "x", y );
        map.put( "x", z );
        return map;
    }

    /**
     * For serialization purposes only. DO NOT USE
     * @param map the data to use
     * @return a place built from the given map
     */
    public static Place deserialize( Map<String, Object> map ) {
        UUID worldUUID = (UUID)map.get( "world" );
        World world = Bukkit.getServer().getWorld( worldUUID );
        if ( world == null )
            throw new IllegalArgumentException( "No world exists with uuid: " + worldUUID );
        int x = (Integer)map.get( "x" );
        int y = (Integer)map.get( "y" );
        int z = (Integer)map.get( "z" );
        return new Place( world, x, y, z );
    }
}
