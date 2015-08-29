package com.github.mdsimmo.bomberman.arenas.blocks;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryBlock extends BlockRep implements ConfigurationSerializable {

    private final ItemStack[] contents;

    InventoryBlock( BlockState state ) {
        super( state );
        if ( !(state instanceof InventoryHolder) )
            throw new IllegalStateException( "state must be a inventory holder" );
        ItemStack[] contents = ((InventoryHolder)state).getInventory().getContents();
        this.contents = new ItemStack[contents.length];
        for ( int i = 0; i < contents.length; i++ )
            this.contents[i] = new ItemStack( contents[i] );
    }

    InventoryBlock( Material type, byte data, ItemStack[] contents ) {
        super( type, data );
        this.contents = contents;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put( "contents", contents );
        return map;
    }

    /**
     * For serialization use only. DO NOT USE
     */
    public static InventoryBlock deserialize( Map<String, Object> map ) {
        String type = (String)map.get( "type" );
        Material material = Material.getMaterial( type );
        if ( material == null )
            material = Material.AIR;
        byte data = (Byte)map.get( "data" );
        ItemStack[] contents = (ItemStack[])map.get( "contents" );
        return new InventoryBlock( material, data, contents );
    }

}
