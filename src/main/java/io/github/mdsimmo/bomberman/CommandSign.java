package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.utils.BlockLocation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class CommandSign {

	private static Plugin plugin = Bomberman.instance;
	private static Map<BlockLocation, List<String>> signs = new HashMap<>();
	private static final File dir = new File( plugin.getDataFolder(), "signs" );
	static {
		dir.mkdir();
	}

	public static boolean isCommandSign( BlockLocation l ) {
		return signs.get( l ) != null;
	}
	
	public static boolean removeSign( BlockLocation l ) {
		return signs.remove( l ) != null;
	}
	
	public static void addCommand( BlockLocation l, String command ) {
		List<String> sign = signs.get( l );
		if ( sign == null )
			signs.put( l, sign = new ArrayList<String>() );
		sign.add( command );
	}
	
	public static void addCommands( BlockLocation l, List<String> commands ) {
		List<String> sign = signs.get( l );
		if ( sign == null )
			signs.put( l, sign = new ArrayList<String>() );
		sign.addAll( commands );
	}
	
	public static List<String> getCommands( BlockLocation l ) {
		return signs.get( l );
	}
	
	public static void save() {
		int id = 0;
		for ( Entry<BlockLocation, List<String>> entry : signs.entrySet() ) {
			YamlConfiguration save = new YamlConfiguration();
			BlockLocation l = entry.getKey();
			save.set( "version", plugin.getDescription().getVersion() );
			save.set( "world", l.world.getName() );
			save.set( "x", l.x );
			save.set( "y", l.y );
			save.set( "z", l.z );
			save.set( "commands", entry.getValue() );
			File saveFile = new File( dir, "sign" + id++ + ".sign" );
			try {
				save.save( saveFile );
			} catch ( IOException e ) {
				plugin.getLogger().warning( "Couldn't save sign at " + l );
			}
		}
	}
	
	public static void load() {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept( File dir, String name ) {
				return name.toLowerCase().endsWith( ".sign" );
			}
		};
		for ( File file : dir.listFiles( filter )) {
			try {
				YamlConfiguration save = YamlConfiguration.loadConfiguration( file );
				int x = save.getInt( "x" );
				int y = save.getInt( "y" );
				int z = save.getInt( "z" );
				World world = plugin.getServer().getWorld( save.getString( "world" ) );
				BlockLocation l = BlockLocation.getLocation( world, x, y, z );
				List<String> commands = save.getStringList( "commands" );
				signs.put( l, commands );
				file.delete();
			} catch( Exception e ) {
				plugin.getLogger().info( "Couldn't load " + file );
			}
		}
	}
	
	public static boolean execute( BlockLocation l, CommandSender sender ) {
		List<String> commands = signs.get( l );
		if ( commands == null )
			return false;
		for ( String command : commands ) 
			plugin.getServer().dispatchCommand( sender, command );
		return true;
	}

}