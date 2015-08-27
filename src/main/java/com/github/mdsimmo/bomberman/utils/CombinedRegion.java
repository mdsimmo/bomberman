package com.github.mdsimmo.bomberman.utils;

import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

/**
 * A combined region takes the union of multiple regions.
 */
public final class CombinedRegion implements Region {

    private final Region[] subRegions;

    /**
     * Creates a combined region from multiple other regions. This method will
     * clone the passed array of regions.
     * @param subRegions a list of the regions to combine
     * @throws NullPointerException if subRegions is null or any region in subRegions is null.
     */
    public CombinedRegion( Region ... subRegions ) {
        if ( subRegions == null )
            throw new NullPointerException( "subRegions cannot be null" );
        int length = subRegions.length;
        this.subRegions = new Region[length];
        for ( int i = 0; i < length; i++ ) {
            if ( subRegions[i] == null )
                throw new NullPointerException( "Null sub region at position: " + i );
            this.subRegions[i] = subRegions[i];
        }
    }

    @Override
    public boolean contains( World w, int x, int y, int z ) {
        for ( Region region : subRegions ) {
            if ( region.contains( w, x, y, z ) )
                return true;
        }
        return false;
    }

    @Override
    public boolean contains( Place p ) {
        for ( Region region : subRegions ) {
            if ( region.contains( p ) )
                return true;
        }
        return false;
    }

    @Override
    public Region shift( int x, int y, int z ) {
        Region[] regions = new Region[subRegions.length];
        for ( int i = 0; i < regions.length; i++ )
            regions[i] = subRegions[i].shift( x, y, z );
        return new CombinedRegion( regions );
    }

    /**
     * For serialization reasons only. DO NOT USE
     * @return a map representing this region
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>( 1 );
        map.put( "regions", subRegions );
        return map;
    }

    /**
     * For serialization purposes only. DO NOT USE
     * @param map the data to use
     * @return a CombinedRegion based on the given map
     */
    public static CombinedRegion deserialize( Map<String, Object> map ) {
        Region[] regions = (Region[])map.get( "regions" );
        return new CombinedRegion( regions );
    }
}
