package io.github.mdsimmo.bomberman;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.util.Vector;

public class Board {

	public String name;
	public int xSize;
	public int ySize;
	public int zSize;
	public ArrayList<BlockRep> blocks = new ArrayList<>();
	HashMap<Vector, BlockRep> delayed = new HashMap<>();
	public ArrayList<Vector> spawnPoints = new ArrayList<>();
	
	public void saveBoard() throws IOException {
		SaveWriter sw = new SaveWriter(name + ".board");
		sw.writePart(xSize);
		sw.writePart(ySize);
		sw.writePart(zSize);
		for (BlockRep rep : blocks) {
			rep.save(sw);
		}
		for (Vector v : delayed.keySet()) {
			BlockRep rep = delayed.get(v);
			rep.save(sw);
			sw.writePart(v.getBlockX());
			sw.writePart(v.getBlockY());
			sw.writePart(v.getBlockZ());
		}
		
		sw.close();
	}
	
	
	
	public void addBlock(BlockRep block, Vector place) {
		if (block.material.isSolid() || block.material == Material.AIR)
			blocks.add(block);
		else {
			blocks.add(new BlockRep());
			delayed.put(place, block);
		}
		if (block.material == Material.WOOL) {
			spawnPoints.add(place.add(new Vector(0, 1, 0)));
		}
	}
	
	public static Board loadBoard(String name) throws IOException {
		SaveReader sr = new SaveReader(name + ".board");
		Board board = new Board();
		board.name = name;
		board.xSize = sr.readInt();
		board.ySize = sr.readInt();
		board.zSize = sr.readInt();
		for (int i = 0; i < board.xSize; i++) {
			for (int j = 0; j < board.ySize; j++) {
				for (int k = 0; k < board.zSize; k++) {
					BlockRep block = BlockRep.loadBlock(sr);
					board.addBlock(block, new Vector(i, j, k));
				}
			}
		}
		// read out the delayed blocks
		while (true) {
			try {
				BlockRep block = BlockRep.loadBlock(sr);
				int x = sr.readInt();
				int y = sr.readInt();
				int z = sr.readInt();
				board.addBlock(block, new Vector(x, y, z));
			} catch (NullPointerException e) {
				// caused when BlockRep tries to read past the end of the file
				break;
			} catch (IllegalArgumentException e) {
				System.out.println("Possible currupt save file \"" + board.name + ".board\" (or maybe it's just my bad programming)");
				break;
			}
		}
		return board;
	}

}
