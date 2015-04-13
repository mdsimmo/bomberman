package io.github.mdsimmo.bomberman.arenabuilder;

import io.github.mdsimmo.bomberman.BlockRep;
import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.arenabuilder.ArenaDetector.BoundingListener;
import io.github.mdsimmo.bomberman.save.BoardSaver;
import io.github.mdsimmo.bomberman.utils.Box;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class ArenaGenerator {

	private static HashMap<String, Board> loadedBoards = new HashMap<>();
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

	/**
	 * destroys the current board and replaces it with the next board. <br>
	 * Use this method to destroy and create board;<br>
	 * Note: when changing boards, this method should be used twice; once to the
	 * original board, then to the new board.
	 * 
	 * @param current
	 *            the board currently in the world
	 * @param next
	 *            the board that you want
	 * @param box
	 *            the box bounding the current board
	 */
	public static void switchBoard( Board current, Board next, Box box ) {
		for ( Entity entity : box.getEntities() ) {
			if ( entity instanceof Item )
				entity.remove();
		}
		// destroy delayed blocks first
		for ( Vector v : current.delayed.keySet() ) {
			BlockRep.createBlank().setBlock( box.fromCorner( v ).getBlock() );
		}
		// build other blocks
		new ArenaBuilder( next, box.corner() );

		// set the box to be the correct size
		box.xSize = next.xSize;
		box.ySize = next.ySize;
		box.zSize = next.zSize;
	}

	/**
	 * Loads a board arena out of cache/save files.
	 * 
	 * @param name
	 *            the arena name
	 * @return the loaded board
	 */
	public static Board loadBoard( String name ) {
		try {
			if ( loadedBoards.containsKey( name ) )
				return loadedBoards.get( name );
			return BoardSaver.loadBoard( toFile( name ) );
		} catch ( IOException e ) {
			return null;
		}
	}

	public static Board remove( String name ) {
		return loadedBoards.remove( name );
	}

	public static File toFile( String name ) {
		return new File( plugin.getDataFolder(), name.toLowerCase() + ".arena" );
	}

	public static List<String> allBoards() {
		List<String> boards = new ArrayList<>();
		File[] files = plugin.getDataFolder().listFiles( new FilenameFilter() {

			@Override
			public boolean accept( File dir, String name ) {
				return ( name.endsWith( ".arena" ) );
			}
		} );
		for ( File f : files ) {
			boards.add( f.getName().split( "\\.arena" )[0] );
		}
		return boards;
	}

	public static void saveBoard( Board board ) {
		loadedBoards.put( board.name, board );
		new BoardSaver( board ).save();
	}

	/**
	 * Gets the bounds of the structure.
	 * 
	 * @return a box around the structure
	 */
	public static void getBoundingStructure( Block target,
			BoundingListener callBack ) {
		ArenaDetector detector = new ArenaDetector();
		detector.getBoundingStructure( target, callBack );
	}

	/**
	 * Creates a board arena
	 */
	public static Board createArena( String arena, Box box ) {
		Board board = new Board( arena, (int)box.xSize, (int)box.ySize,
				(int)box.zSize );
		for ( int i = 0; i < box.xSize; i++ ) {
			for ( int j = 0; j < box.ySize; j++ ) {
				for ( int k = 0; k < box.zSize; k++ ) {
					Vector v = new Vector( i, j, k );
					BlockRep block = BlockRep.createBlock( box.corner().add( v )
							.getBlock() );
					board.addBlock( block, v );
				}
			}
		}
		return board;
	}
}
