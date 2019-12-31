package io.github.mdsimmo.bomberman.arena;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.save.BoardSaver;
import io.github.mdsimmo.bomberman.utils.Box;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * A class designed to greatly simplify the process of generating arenas.
 */
public class ArenaGenerator {

	private static HashMap<String, ArenaTemplate> loadedBoards = new HashMap<>();
	private static Plugin plugin = Bomberman.instance;

	/**
	 * Copies all the default boards into the config folder
	 */
	public static void copyDefaults() {
		String[] defaults = { "default", "layers" };
		for ( String name : defaults ) {
			File file = toFile( name );
			if ( file.exists() ) {
				// already copied
				continue;
			}
			plugin.getLogger()
					.info( "Copying the default arena '" + name + "'" );
			try {
				file.createNewFile();
				InputStream inputStream = plugin.getResource( name + ".arena" );
				FileOutputStream fos = new FileOutputStream( file );
				int read = 0;
				byte[] bytes = new byte[1024];

				while ( ( read = inputStream.read( bytes ) ) != -1 )
					fos.write( bytes, 0, read );

				fos.flush();
				fos.close();
				inputStream.close();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
	}

	public interface BuildListener {
		void onContructionComplete();
	}
	
	/**
	 * Cleanly replaces the current arena with the next arena. The boards should
	 * always be of equal size. Use this method to destroy and create arena;
	 * Note: when changing boards, this method should be used twice; once to the
	 * original arena, then to the new arena.
	 * 
	 * @param current
	 *            the arena currently in the world. May be not be null
	 * @param next
	 *            the arena that you want. Must not be null.
	 * @param box
	 *            the box bounding the boards.
	 * @param l the listener to inform the build has finished. Null to not listen
	 * @throws IllegalArgumentException if the boards are not the same size
	 */
	/*public static void switchBoard(Arena current, Arena next, Box box, BuildListener l ) {
		if ( current.size != next.size || current.size != box.size)
			throw new IllegalArgumentException( "Boards must all be the same size" );
		
		// destroy all items
		for ( Entity entity : box.getEntities() ) {
			if ( entity instanceof Item )
				entity.remove();
		}

		// build other blocks
		new ArenaBuilder( next, box.corner(), l ).start(Bomberman.instance);
	}*/

	/**
	 * Loads a arena arena out of cache/save files.
	 * 
	 * @param name
	 *            the arena name
	 * @return the loaded arena
	 */
	public static ArenaTemplate loadBoard(String name) {
		try {
			if (loadedBoards.containsKey(name))
				return loadedBoards.get(name);
			return BoardSaver.loadBoard(toFile(name));
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Removes a arena from the cache. Does not delete its file
	 * @param name the name of the arena to remove
	 */
	public static void remove(String name) {
		loadedBoards.remove( name );
	}

	/**
	 * Gets the file that contains the arena
	 * @param name the name of the arena
	 * @return the boards save file
	 */
	public static File toFile( String name ) {
		return new File( plugin.getDataFolder(), name.toLowerCase() + ".arena" );
	}

	/**
	 * Gets a list of all the boards that are installed.
	 * @return a list of al the boards name's
	 */
	public static List<String> allBoards() {
		List<String> boards = new ArrayList<>();
		File[] files = plugin.getDataFolder().listFiles((dir, name) -> ( name.endsWith( ".arena" ) ));
		for ( File f : files ) {
			boards.add( f.getName().split( "\\.arena" )[0] );
		}
		return boards;
	}

	/**
	 * Saves a arena to its file
	 * @param arena the arena to save
	 */
	public static void saveBoard( ArenaTemplate arena) {
		loadedBoards.put( arena.name, arena);
		new BoardSaver(arena).save();
	}

	/**
	 * Gets the box that surrounds the target block. Happens in an synchronised thread.
	 * @param target any block in the structure
	 * @param callBack who it inform that the structure has finished being detected
	 */
	public static void getBoundingStructure(Location target, Consumer<Box> callBack ) {
		ArenaDetector detector = new ArenaDetector(target);
		detector.start(plugin, callBack);
	}


}
