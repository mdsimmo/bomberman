package io.github.mdsimmo.bomberman.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Box {

	public int x, y, z;
	public int xSize, ySize, zSize;
	public final World world;

	public Box(World world, int x, int y, int z, int xSize, int ySize, int zSize) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
	}
	
	public Box (Location l, Vector size) {
		world = l.getWorld();
		x = l.getBlockX();
		y = l.getBlockY();
		z = l.getBlockZ();
		xSize = size.getBlockX();
		ySize = size.getBlockY();
		zSize = size.getBlockZ();
	}

	public Box(Location l, int xSize, int ySize, int zSize) {
		world = l.getWorld();
		x = l.getBlockX();
		y = l.getBlockY();
		z = l.getBlockZ();
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
	}
	
	public Box(Location min, Location max) {
		this(min, max.getBlockX() - min.getBlockX(),
				max.getBlockY() - min.getBlockY(),
				max.getBlockZ() - min.getBlockZ());
	}


	public boolean contains( Location l ) {
		return contains( l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ() );
	}
	
	public boolean contains( BlockLocation l ) {
		return contains( l.world, l.x, l.y, l.z );
	}
	
	public boolean contains( World world, int x, int y, int z ) {
		if ( !world.equals( this.world ) )
			return false;
		return x >= this.x && x < this.x + xSize 
			&& y >= this.y && y < this.y + ySize
			&& z >= this.z && z < this.z + zSize;
	}
	
	public Location corner() {
		return new Location(world, x, y, z);
	}
	
	public Location fromCorner(int xAdd, int yAdd, int zAdd) {
		return new Location(world, x + xAdd, y + yAdd, z + zAdd);
	}
	
	public Location fromCorner(Vector v) {
		return new Location(world, x + v.getX(), y + v.getY(), z + v.getZ());
	}

	public List<Entity> getEntities() {
		List<Entity> entities = new ArrayList<>();
		// the "+ 16" is to make sure the chunks at the edge are also included
		for (int i = (int) x; i < (x + xSize + 16); i += 16) {
			for (int k = (int) z; k < (z + zSize + 16); k += 16) {
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
		return "corner at: (" + x + ',' + y + ',' + z + "); size: " + xSize + ' ' + ySize + ' ' + zSize;
	}
}
