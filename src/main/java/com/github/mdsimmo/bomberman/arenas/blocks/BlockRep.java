package com.github.mdsimmo.bomberman.arenas.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple representation of a block
 */
public class BlockRep implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass( BlockRep.class );
    }

    public static BlockRep getRep( BlockState state ) {
        if ( state instanceof InventoryHolder )
            return new InventoryBlock( state );
        else
            return new BlockRep( state );
    }

    private final Material material;
    private final byte data;

    BlockRep ( BlockState state ) {
        this( state.getType(), state.getRawData() );
    }

    /**
     * Creates a BlockRep with this material and data
     */
    BlockRep( Material type, byte data ) {
        this.material = type;
        this.data = data;
    }

    /**
     * Converts the block at a specific location to be the same as this block
     * @param block the block to convert
     */
    public void setBlock( Block block ) {
        block.setType( material );
        block.setData( data );
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "type", material.toString() );
        map.put( "data", data );
        return map;
    }

    /**
     * For serialization use only. DO NOT USE
     */
    public static BlockRep deserialize( Map<String, Object> map ) {
        String type = (String)map.get( "type" );
        Material material = Material.getMaterial( type );
        if ( material == null )
            material = Material.AIR;
        byte data = (Byte)map.get( "data" );
        return new BlockRep( material, data );
    }


}
