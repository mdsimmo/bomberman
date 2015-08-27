package com.github.mdsimmo.bomberman.utils;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An immutable representation of a point in 3D space
 */
public class Point3D implements Serializable, ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass( Point3D.class );
    }

    /**
     * Creates a new point from the passed vector. The values used from the
     * vector will be taken from the Vector.getBlockXYZ() methods.
     * @param v the vector to copy from
     * @return the created point
     */
    public static Point3D from( Vector v ) {
        return new Point3D( v.getBlockX(), v.getBlockY(), v.getBlockZ() );
    }

    /**
     * Creates a point from the specified components
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @return the created point
     */
    public static Point3D from( int x, int y, int z ) {
        return new Point3D( x, y, z );
    }

    public final int x, y, z;
    private transient int hashCode;
    private transient boolean hashMade = false;

    private Point3D( int x, int y, int z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Adds each component to this point
     * @param x the x value to add
     * @param y the y value to add
     * @param z the z value to add
     * @return the new point
     */
    public Point3D plus( int x, int y, int z ) {
        return new Point3D( this.x + x, this.y + y, this.z + z );
    }

    /**
     * Adds the other point to this point
     * @param other the other point to add
     * @return a new point
     */
    public Point3D plus( Point3D other ) {
        return new Point3D( x + other.x, y + other.y, z + other.z );
    }

    /**
     * Subtracts each component from this point
     * @param x the x component to subtract
     * @param y the y component to subtract
     * @param z the z component to subtract
     * @return a new point
     */
    public Point3D minus( int x, int y, int z ) {
        return new Point3D( this.x - x, this.y - y, this.z - z );
    }

    /**
     * Subtracts each component of the other point from this point
     * @param other the point to subtract
     * @return a new point
     */
    public Point3D minus( Point3D other ) {
        return new Point3D( this.x - other.x, this.y - other.y, this.z - other.z );
    }

    /**
     * Multiplies each component by a value
     * @param scalar the amount to scale each value by
     * @return a new point
     */
    public Point3D times( int scalar ) {
        return new Point3D( x * scalar, y * scalar, z * scalar );
    }

    /**
     * Divides each component by an amount
     * @param divider the amount to divide by
     * @return a new point
     */
    public Point3D divide( int divider ) {
        return new Point3D( x / divider, y / divider, z / divider );
    }

    /**
     * Converts this point to an floating point point.
     * @return the converted point
     */
    public Point3Df toPoint3Df() {
        return Point3Df.from( x, y, z );
    }

    /**
     * Converts this point to a {@link Vector}
     * @return the created vector
     */
    public Vector toVector() {
        return new Vector( x, y, z );
    }

    /**
     * Another point is only equal if it is of type Point3D and it has x, y and z
     * values equal to this points
     * @param obj the object to test against
     * @return true if the objects are equal
     */
    @Override
    public boolean equals( Object obj ) {
        if ( !(obj instanceof Point3D) )
            return false;
        Point3D o = (Point3D)obj;
        return x == o.x && y == o.y && z == o.z;
    }

    @Override
    public int hashCode() {
        if ( hashMade )
            return hashCode;
        int hash = 31;
        hash = hash * 31 + x;
        hash = hash * 31 + y;
        hash = hash * 31 + z;
        hashCode = hash;
        hashMade = true;
        return hash;
    }

    /**
     * For Bukkit serialization use only. DO NOT USE.
     * @return a map that can be used to serialize this point
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>( 3 );
        map.put( "x", x );
        map.put( "y", y );
        map.put( "z", z );
        return map;
    }

    /**
     * For Bukkit serialization use only. Do not use
     * @param map the map to restore from. Must have a 'x', 'y' and 'z' value
     * @return a new Point3Df
     */
    public static Point3D deserialize( Map<String, Object> map ) {
        int x = (Integer)map.get( "x" );
        int y = (Integer)map.get( "y" );
        int z = (Integer)map.get( "z" );
        return new Point3D( x, y, z );
    }
}
