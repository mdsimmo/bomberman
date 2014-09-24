package io.github.mdsimmo.bomberman.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Box {

	public double x, y, z;
	public double xSize, ySize, zSize;
	public final World world;

	public Box(World world, double x, double y, double z, double xSize, double ySize, double zSize) {
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
		x = l.getX();
		y = l.getY();
		z = l.getZ();
		xSize = size.getX();
		ySize = size.getY();
		zSize = size.getZ();
	}

	public Box(Location l, double xSize, double ySize, double zSize) {
		world = l.getWorld();
		x = l.getX();
		y = l.getY();
		z = l.getZ();
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
	}
	
	public Box(Location min, Location max) {
		this(min, max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ());
	}


	public boolean contains(Location l) {
		if (l.getWorld().equals(world)) {
			return     l.getX() >= x && l.getX() < x + xSize
					&& l.getY() >= y && l.getY() < y + ySize
					&& l.getZ() >= z && l.getZ() < z + zSize;
		} else
			return false;
	}
	
	public Location corner() {
		return new Location(world, x, y, z);
	}
	
	public Location fromCorner(double xAdd, double yAdd, double zAdd) {
		return new Location(world, x + xAdd, y + yAdd, z + zAdd);
	}
	
	public Location fromCorner(Vector v) {
		return new Location(world, x + v.getX(), y + v.getY(), z + v.getZ());
	}

	public List<Entity> getEntities() {
		List<Entity> entities = new ArrayList<>();
		// the "+ 16" is to make sure the chunks at the edge are also included
		for (int xx = (int) x; xx < x + xSize + 16; xx += 16) {
			for (int zz = (int) z; zz < z + zSize + 16; zz += 16) {
				Chunk chunk = world.getChunkAt((int) x, (int) z);
				for (Entity entity : chunk.getEntities()) {
					if (contains(entity.getLocation()))
						entities.add(entity);
				}
			}
		}
		return entities;
	}
}
