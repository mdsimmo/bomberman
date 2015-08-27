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
public final class Point3Df implements Serializable, ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass( Point3Df.class );
    }

    /**
     * Creates a new point from the passed vector.
     * @param v the vector to copy from
     * @return the created point
     */
    public static Point3Df from( Vector v ) {
        return new Point3Df( v.getX(), v.getY(), v.getZ() );
    }

    /**
     * Creates a point from the passed components
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @return the created point
     */
    public static Point3Df from( double x, double y, double z ) {
        return new Point3Df( x, y, z );
    }

    public final double x, y, z;
    private transient boolean hashMade = false;
    private transient int hashCode;

    private Point3Df( double x, double y, double z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Adds the values to this point.
     * @param x the x value to add
     * @param y the y value to add
     * @param z the z value to add
     * @return a new point
     */
    public Point3Df plus( double x, double y, double z ) {
        return new Point3Df( this.x + x, this.y + y, this.z + z );
    }

    /**
     * Subtracts the values from this point
     * @param x the x value to subtract
     * @param y the y value to subtract
     * @param z the z value to subtract
     * @return a new point
     */
    public Point3Df minus( double x, double y, double z ) {
        return new Point3Df( this.x - x, this.y - y, this.z - z );
    }

    /**
     * Subtracts the other point from this point
     * @param other the other point to subtract
     * @return a new point
     */
    public Point3Df minus( Point3D other ) {
        return new Point3Df( x - other.x, y - other.y, z - other.z );
    }

    /**
     * Creates a new point which has all components scaled by an amount
     * @param scaler the amount to scale by
     * @return the created point
     */
    public Point3Df times( double scaler ) {
        return new Point3Df( x * scaler, y * scaler, z * scaler );
    }

    /**
     * Creates a new point which has all components divided by an amount
     * @param divider the amount to divide by
     * @return the created point
     */
    public Point3Df divide( double divider ) {
        return new Point3Df( x / divider, y / divider, z / divider );
    }

    /**
     * Converts this point to an integer point. All components are rounded down.
     * @return the converted point
     */
    public Point3D toPoint3D() {
        return Point3D.from( (int)x, (int)y, (int)z );
    }

    /**
     * Converts this point to a {@link Vector}
     * @return the created vector
     */
    public Vector toVector() {
        return new Vector( x, y, z );
    }

    /**
     * Another point is only equal if it is of type Point3Df and it has x, y and z
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
        long bits;
        int hash = 31;
        bits = Double.doubleToLongBits( x );
        hash = hash * 31 + (int)(bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits( y );
        hash = hash * 31 + (int)(bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits( z );
        hash = hash * 31 + (int)(bits ^ (bits >>> 32));
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
    public static Point3Df deserialize( Map<String, Object> map ) {
        double x = (Double)map.get( "x" );
        double y = (Double)map.get( "y" );
        double z = (Double)map.get( "z" );
        return new Point3Df( x, y, z );
    }
}