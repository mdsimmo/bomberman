package io.github.mdsimmo.bomberman.utils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public final class Box {

	public final Dim loc;
	public final Dim size;
	public final World world;

	public Box(World world, Dim loc, Dim size) {
		this.world = world;
		this.loc = loc;
		this.size = size;
	}

	public Box(World world, int x, int y, int z, int xSize, int ySize, int zSize) {
		this(world, new Dim(x, y, z), new Dim(xSize, ySize, zSize));
	}

	public Box (Location l, Vector size) {
		this(l, size.getBlockX(), size.getBlockY(), size.getBlockZ());
	}

	public Box(Location l, int xSize, int ySize, int zSize) {
		this(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), xSize, ySize, zSize);
	}
	
	public Box(Location min, Location max) {
		this(min,
				max.getBlockX() - min.getBlockX(),
				max.getBlockY() - min.getBlockY(),
				max.getBlockZ() - min.getBlockZ());
	}

	public Box(Location min, Dim size) {
		this(min.getWorld(), new Dim(min.getBlockX(), min.getBlockY(), min.getBlockZ()), size);
	}


	public boolean contains( Location l ) {
		return contains( l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ() );
	}
	
	public boolean contains( World world, int x, int y, int z ) {
		if ( !world.equals( this.world ) )
			return false;
		return x >= loc.x && x < loc.x + size.x
			&& y >= loc.y && y < loc.y + size.y
			&& z >= loc.z && z < loc.z + size.z;
	}
	
	public Location corner() {
		return new Location(world, loc.x, loc.y, loc.z);
	}
	
	public Location fromCorner(int xAdd, int yAdd, int zAdd) {
		return new Location(world, loc.x + xAdd, loc.y + yAdd, loc.z + zAdd);
	}
	
	public Location fromCorner(Vector v) {
		return new Location(world, loc.x + v.getX(), loc.y + v.getY(), loc.z + v.getZ());
	}

	public List<Entity> getEntities() {
		List<Entity> entities = new ArrayList<>();
		// the "+ 16" is to make sure the chunks at the edge are also included
		for (int i = loc.x; i < (loc.x + size.x + 16); i += 16) {
			for (int k = loc.z; k < (loc.z + size.z + 16); k += 16) {
				Chunk chunk = world.getBlockAt(i, 1, k).getChunk();
				for (Entity entity : chunk.getEntities()) {
					if (contains(entity.getLocation()))
						entities.add(entity);
				}
			}
		}
		return entities;
	}

	@Override
	public String toString() {
		return "corner at: (" + loc + "); size: " + size;
	}

	@Override
	public int hashCode() {
		int hash = 31;
		hash = hash * 31 + world.hashCode();
		hash = hash * 31 + loc.hashCode();
		hash = hash * 31 + size.hashCode();
		return hash;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Box) {
			Box other = (Box) obj;
			return other.world.equals(world) && other.loc.equals(loc) && other.size.equals(size);
		} else {
			return false;
		}
	}
}
