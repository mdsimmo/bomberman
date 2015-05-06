package io.github.mdsimmo.bomberman.save;

import io.github.mdsimmo.bomberman.BlockRep;
import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.Config;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

public class BoardSaver extends Save {

	private Board board;

	public BoardSaver( Board board ) {
		// used when saving a board
		super( board.name + ".arena" );
		this.board = board;
	}

	private BoardSaver( File file ) {
		// used when loading boards
		super( file );
	}

	@Override
	public void save() {
		set( "name", board.name );
		set( "size.x", board.xSize );
		set( "size.y", board.ySize );
		set( "size.z", board.zSize );

		// save standard blocks
		CompressedSection section = new CompressedSection( ';' );
		for ( int i = 0; i < board.xSize; i++ ) {
			for ( int j = 0; j < board.ySize; j++ ) {
				for ( int k = 0; k < board.zSize; k++ ) {
					BlockRep rep = board.getBlock( i, j, k );
					section.addParts( rep.save( this ) );
				}
			}
		}
		setCompressedSection( "blocks.standard", section );

		// save special blocks
		section.reset();
		for ( Map.Entry<Vector, BlockRep> entry : board.delayed.entrySet() ) {
			BlockRep rep = entry.getValue();
			Vector v = entry.getKey();
			CompressedSection special = new CompressedSection( ':' );
			special.addParts( v.getBlockX(), v.getBlockY(), v.getBlockZ() );
			special.addParts( rep.save( this ) );
			section.addParts( special );
		}
		setCompressedSection( "blocks.special", section );
		super.save();
	}

	@SuppressWarnings( "unchecked" )
	public static Board loadBoard( File file ) throws IOException {
		if ( !file.exists() )
			return null;
		plugin.getLogger().info( "Loading arena '" + file.getName() + "'" );
		BoardSaver save = new BoardSaver( file );
		save.convert();
		int x = save.getInt( "size.x" );
		int y = save.getInt( "size.y" );
		int z = save.getInt( "size.z" );

		Board board = new Board( save.getString( "name" ), x, y, z );

		// Read out normal blocks
		CompressedSection blocks = new CompressedSection( ';' );
		blocks.setValue( save.getString( "blocks.standard" ) );
		List<String> sections = blocks.readParts();
		// decode read string
		int count = 0;
		for ( int i = 0; i < board.xSize; i++ ) {
			for ( int j = 0; j < board.ySize; j++ ) {
				for ( int k = 0; k < board.zSize; k++ ) {
					BlockRep block = BlockRep.loadFrom(
							sections.get( count++ ), save );
					board.addBlock( block, new Vector( i, j, k ) );
				}
			}
		}

		// read out the delayed blocks
		blocks.setValue( save.getString( "blocks.special" ) );
		sections = blocks.readParts();
		CompressedSection special = new CompressedSection( ':' );
		for ( String s : sections ) {
			special.setValue( s );
			List<String> parts = special.readParts();
			int x2 = Integer.parseInt( parts.get( 0 ), 10 );
			int y2 = Integer.parseInt( parts.get( 1 ), 10 );
			int z2 = Integer.parseInt( parts.get( 2 ), 10 );
			Vector v = new Vector( x2, y2, z2 );
			BlockRep b = BlockRep.loadFrom( parts.get( 3 ), save );
			board.addBlock( b, v );
		}

		board.setDestructables( (List<Material>)Config.BLOCKS_DESTRUCTABLE
				.getValue( save ) );
		board.setDropping( (List<Material>)Config.BLOCKS_DROPPING
				.getValue( save ) );
		plugin.getLogger().info( "Arena loaded" );
		return board;
	}

	@Override
	public void convert( Version version ) {
		switch ( version ) {
		case PAST:
		case V0_0_1:
		case V0_0_2:
		case V0_0_2a:
		case V0_0_3:
		case V0_0_3a:
		case V0_0_3b:
		case V0_0_3c:
		case V0_0_3d:
		case V0_0_3_SNAPSHOT:
		case V0_1_0:
		case V0_1_0_SNAPSHOT:
		case V0_1_0_SNAPSHOT_2:
		case V0_1_0_SNAPSHOT_3:
		case V0_1_0a:
		case V0_1_0b:
			break;
		case FUTURE:
			plugin.getLogger().info(
					"Unknown version " + getVersionRaw() + " in "
							+ file.getName() );
			break;
		}
	}

	public static void convertOldArenas() {
		File[] files = plugin.getDataFolder().listFiles( new FilenameFilter() {
			@Override
			public boolean accept( File file, String name ) {
				return name.endsWith( ".board" );
			}
		} );
		for ( File file : files ) {
			plugin.getLogger().info( "converting " + file.getName() );
			try {
				convertOldArena( file );
				if ( !file.delete() )
					plugin.getLogger().info(
							"Couldn't delete " + file.getName()
									+ ". Please delete it manually" );
			} catch ( Exception e ) {
				e.printStackTrace();
				plugin.getLogger().info( "Couldn't convert " + file.getName() );
			}
			plugin.getLogger().info(
					"converted " + file.getName() + " successfully" );
		}
	}

	public static void convertOldArena( File file ) throws IOException {
		FileReader fr = new FileReader( file );
		String contents = "";
		while ( true ) {
			int read = fr.read();
			if ( read == -1 )
				break;
			else
				contents += (char)read;
		}
		fr.close();
		CompressedSection oldCS = new CompressedSection( ':' );
		oldCS.setValue( contents );
		List<String> data = oldCS.readParts();
		YamlConfiguration save = new YamlConfiguration();
		CompressedSection newCS = new CompressedSection( ';' );
		String name = file.getName().split( "\\.board" )[0];
		save.set( "name", name );
		int x = Integer.parseInt( data.get( 0 ), 10 );
		int y = Integer.parseInt( data.get( 1 ), 10 );
		int z = Integer.parseInt( data.get( 2 ), 10 );
		save.set( "size.x", x );
		save.set( "size.y", y );
		save.set( "size.z", z );

		int count = 3;
		for ( int i = 0; i < x; i++ ) {
			for ( int j = 0; j < y; j++ ) {
				for ( int k = 0; k < z; k++ ) {
					String material = data.get( count++ );
					String d = data.get( count++ );
					newCS.addParts( material + ',' + d + ',' );
				}
			}
		}
		save.set( "blocks.standard", newCS.toString() );
		newCS.reset();
		while ( true ) {
			if ( count >= data.size() )
				break;
			String material = data.get( count++ );
			String d = data.get( count++ );
			String xx = data.get( count++ );
			String yy = data.get( count++ );
			String zz = data.get( count++ );
			newCS.addParts( xx + ':' + yy + ':' + zz + ':' + material + ',' + d
					+ ',' + ':' );
		}
		save.set( "blocks.special", newCS.toString() );
		save.save( new File( plugin.getDataFolder(), name + ".arena" ) );
	}
}
