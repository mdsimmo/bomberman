package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.utils.BlockLocation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class DynamicSigns {

	public static class DynamicSign {
		final String text;
		final Message messageCache;
		final int line;
		final Game game;
		final BlockLocation loc;
		
		public DynamicSign( BlockLocation l, int line, String text, Game game ) {
			this.loc = l;
			this.text = text;
			this.line = line;
			this.game = game;
			this.messageCache = new Message( null, text ).put( "game", game );
		}
		
		
	}
	
	private static Plugin plugin = Bomberman.instance;
	private static HashMap<BlockLocation, HashMap<Integer, DynamicSign>> signs = new HashMap<>();
	private static final File dir = new File( plugin.getDataFolder(), "signs" );
	static {
		dir.mkdir();
		Bukkit.getScheduler().scheduleSyncRepeatingTask( Bomberman.instance, new Runnable() {
			
			@Override
			public void run() {
				for ( HashMap<Integer, DynamicSign> lines : signs.values() ) {
					for ( DynamicSign sign : lines.values() ) {
						// ensure the block is still a sign
						BlockLocation l = sign.loc;
						BlockState state = l.getBlock().getState();
						Sign signState;
						if ( state instanceof Sign )
							signState = (Sign)state;
						else {
							disable( l, sign.line );
							return;
						}
						
						// update the sign's text
						signState.setLine( sign.line, sign.messageCache.toString() );
						signState.update();
					}
				}
			}
		}, 20, 20 );
	}
	
	public static boolean disable( BlockLocation l, int line ) {
		HashMap<Integer, DynamicSign> lines = signs.get( l );
		if ( lines == null )
			return false;
		boolean removed = lines.remove( line ) != null;
		if ( lines.size() == 0 )
			signs.remove( l );
		return removed;
	}
	
	public static boolean disable( BlockLocation l ) {
		return signs.remove( l ) != null;
	}
	
	public static void enable( DynamicSign sign ) {
		HashMap<Integer, DynamicSign> lines = signs.get( sign.loc );
		if ( lines == null )
			signs.put( sign.loc, lines = new HashMap<Integer, DynamicSigns.DynamicSign>( 2 ) );
		lines.put( sign.line, sign );
	}
	
	public static void save() {
		int id = 0;
		for ( HashMap<Integer, DynamicSign> lines : signs.values() ) {
			for ( DynamicSign sign : lines.values() ) {
				YamlConfiguration save = new YamlConfiguration();
				BlockLocation l = sign.loc;
				save.set( "version", plugin.getDescription().getVersion() );
				save.set( "world", l.world.getName() );
				save.set( "x", l.x );
				save.set( "y", l.y );
				save.set( "z", l.z );
				save.set( "line", sign.line );
				save.set( "text", sign.text );
				save.set( "game", sign.game.name );
				File saveFile = new File( dir, "sign-" + id++ + ".dyn" );
				try {
					save.save( saveFile );
				} catch ( IOException e ) {
					plugin.getLogger().warning( "Couldn't save sign at " + l );
				}
			}
		}
	}
	
	public static void load() {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept( File dir, String name ) {
				return name.toLowerCase().endsWith( ".dyn" );
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
				String text = save.getString( "text" );
				String gameName = save.getString( "game" );
				Game game = Game.findGame( gameName );
				int line = save.getInt( "line" );
				
				DynamicSign sign = new DynamicSign( l, line, text, game );
				enable( sign );
				
				file.delete();
			} catch( Exception e ) {
				plugin.getLogger().info( "Couldn't load " + file );
			}
		}
	}
	
}
