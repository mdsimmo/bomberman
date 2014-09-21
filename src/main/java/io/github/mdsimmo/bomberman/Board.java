package io.github.mdsimmo.bomberman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.util.Vector;

public class Board {

	public final String name;
	public final int xSize;
	public final int ySize;
	public final int zSize;
	private Vector shift = new Vector();
	private BlockRep[][][] blocks;
	public HashMap<Vector, BlockRep> delayed = new HashMap<>();
	public ArrayList<Vector> spawnPoints = new ArrayList<>();
	private List<Material> destructables = Config.BLOCKS_DESTRUCTABLE.getValue();
	private List<Material> droppingBlocks = Config.BLOCKS_DROPPING.getValue();
	
	public Board(String name, int xSize, int ySize, int zSize) {
		this.name = name;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		blocks = new BlockRep[xSize][ySize][zSize];
	}
	
	/**
	 * adds the block to the arena
	 * @param block the block to add
	 * @param place the position to add it at
	 */
	public void addBlock(BlockRep block, Vector place) {
		if (block.getMaterial().isSolid() || block.getMaterial() == Material.AIR) {
			int x = place.getBlockX();
			int y = place.getBlockY();
			int z = place.getBlockZ();
			blocks[x][y][z] = block;
			delayed.remove(place);
		} else {
			blocks[place.getBlockX()][place.getBlockY()][place.getBlockZ()] = BlockRep.createBlank();
			delayed.put(place, block);
		}
		if (block.getMaterial() == Material.WOOL) {
			spawnPoints.add(place.add(new Vector(0.5, 1, 0.5)));
		}
	}
	
	public BlockRep getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}
	
	public BlockRep getBlock(int i) {
		int x = i/(ySize*zSize);
		int y = (i/zSize)%ySize;
		int z = i%zSize;
		return getBlock(x, y, z);
	}
	
	public Vector countToVector(int i) {
		int x = i/(ySize*zSize);
		int y = (i/zSize)%ySize;
		int z = i%zSize;
		return new Vector(x, y, z);
	}
	
	public void setShift(int x, int y, int z) {
		shift.setX(x);
		shift.setY(y);
		shift.setZ(z);
	}
	
	public void addShift(int x, int y, int z) {
		setShift(x+shift.getBlockX(), y+shift.getBlockY(), z+shift.getBlockZ());
	}
	
	public Vector getShift() {
		return shift.clone();
	}
	
	public boolean isDestructable(Material m) {
		return destructables.contains(m) || isDropping(m);
	}
	
	public boolean isDropping(Material m) {
		return droppingBlocks.contains(m);
	}
	
	public void setDestructables(List<Material> list) {
		this.destructables = list;
	}
	
	public void setDropping(List<Material> list) {
		this.droppingBlocks = list;
	}
}
