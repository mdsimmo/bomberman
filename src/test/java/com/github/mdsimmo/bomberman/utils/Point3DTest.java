package com.github.mdsimmo.bomberman.utils;

import junit.framework.TestCase;
import org.bukkit.util.Vector;

public class Point3DTest extends TestCase {

    public void testFromVector() throws Exception {
        Vector v = new Vector( 5.3, 3.2, -6.6 );
        Point3D p = Point3D.from( v );
        assertEquals( p.x, 5 );
        assertEquals( p.y, 3 );
        assertEquals( p.z,-7 );
    }

    public void testFromCoords() throws Exception {
        Point3D p = Point3D.from( 5, -3, 6 );
        assertEquals( p.x, 5 );
        assertEquals( p.y, -3 );
        assertEquals( p.z, 6 );
    }

    public void testPlusPoint() throws Exception {
        Point3D a = Point3D.from( -2, 4, -6 );
        Point3D b = Point3D.from( 5, 2,  -2 );
        Point3D c = a.plus( b );
        assertEquals( 3, c.x );
        assertEquals( 6, c.y );
        assertEquals(-8, c.z );
    }

    public void testPlusCoords() throws Exception {
        Point3D a = Point3D.from( 2, 4, -6 );
        Point3D b = a.plus( 5, -3, -2 );
        assertEquals( 7, b.x );
        assertEquals( 1, b.y );
        assertEquals(-8, b.z );
    }

    // I can't be bothered testing all the others ...

}