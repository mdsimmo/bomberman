package io.github.mdsimmo.bomberman.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockLocation {

	public static BlockLocation getLocation( World world, int x, int y, int z ) {
		return new BlockLocation( world, x, y, z );
	}

	public static BlockLocation getLocation( Block block ) {
		return new BlockLocation( block.getWorld(), block.getX(), block.getY(),
				block.getZ() );
	}

	public static BlockLocation getLocation( Location location ) {
		return new BlockLocation( location.getWorld(), location.getBlockX(),
				location.getBlockY(), location.getBlockZ() );
	}

	public final World world;
	public final int x, y, z;

	private BlockLocation( World world, int x, int y, int z ) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Block getBlock() {
		return world.getBlockAt( x, y, z );
	}

	@Override
	public boolean equals( Object obj ) {
		if ( obj instanceof BlockLocation ) {
			BlockLocation l = (BlockLocation)obj;
			return world == l.world && x == l.x && y == l.y && z == l.z;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 19;
		hash = 19 * hash + x;
		hash = 19 * hash + y;
		hash = 19 * hash + z;
		hash = 19 * hash + world.hashCode();
		return hash;
	}
	
	@Override
	public String toString() {
		return '[' + world.getName() + ", " + x + ", " + y + ", " + z + ']';
	}
	
}
