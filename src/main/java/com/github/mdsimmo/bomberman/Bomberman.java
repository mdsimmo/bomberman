package com.github.mdsimmo.bomberman;

import com.github.mdsimmo.bomberman.arenas.BoxArena;
import com.github.mdsimmo.bomberman.arenas.blocks.BlockRep;
import com.github.mdsimmo.bomberman.arenas.blocks.InventoryBlock;
import com.github.mdsimmo.bomberman.commands.CmdHandler;
import com.github.mdsimmo.bomberman.commands.BmCmd;
import com.github.mdsimmo.bomberman.localisation.Language;
import com.github.mdsimmo.bomberman.prizes.CombinedPayment;
import com.github.mdsimmo.bomberman.prizes.EmptyPayment;
import com.github.mdsimmo.bomberman.prizes.ItemPayment;
import com.github.mdsimmo.bomberman.prizes.XpPayment;
import com.github.mdsimmo.bomberman.utils.Place;
import com.github.mdsimmo.bomberman.utils.Point3D;
import com.github.mdsimmo.bomberman.utils.Point3Df;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * THe base plugin class
 */
public class Bomberman extends JavaPlugin {

    private static Bomberman instance;

    /**
     * Gets the Bomberman's plugins instance.
     * @return the instance
     * @throws IllegalStateException if the plugin has not been loaded yet
     */
    public static Bomberman instance() {
        if ( instance == null )
            throw new IllegalStateException( "plugin not loaded yet" );
        return instance;
    }

    public void onEnable() {
        instance = this;
        registerConfigClasses();
        CmdHandler executor = new CmdHandler( new BmCmd() );
        getCommand( "bomberman" ).setExecutor( executor );
    }

    /**
     * Registers all the bomberman classes that implement ConfigurationSerialization
     */
    private void registerConfigClasses() {
        ConfigurationSerialization.registerClass( BoxArena.class );
        ConfigurationSerialization.registerClass( BlockRep.class );
        ConfigurationSerialization.registerClass( InventoryBlock.class );
        ConfigurationSerialization.registerClass( Language.class );
        ConfigurationSerialization.registerClass( Place.class );
        ConfigurationSerialization.registerClass( Point3D.class );
        ConfigurationSerialization.registerClass( Point3Df.class );
        ConfigurationSerialization.registerClass( CombinedPayment.class );
        ConfigurationSerialization.registerClass( XpPayment.class );
        ConfigurationSerialization.registerClass( EmptyPayment.class );
        ConfigurationSerialization.registerClass( ItemPayment.class );
    }

}