package io.github.mdsimmo.bomberman;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class BoardGenerator {
	
	private static HashMap<String, Board> loadedBoards = new HashMap<>();
	private static Plugin plugin = Bomberman.instance;
	
	/**
	 * Loads the default board. If the needed save file is not made, it will create it.
	 */
	public static Board loadDefault() {
		if (loadBoard(Config.defaultBoard) == null) {
			try {
				plugin.getLogger().info("Couldn't find \"" + Config.defaultBoard + ".board\". Using \"default.board\"");
				InputStream inputStream = plugin.getResource("default.board");
				File f = new File(plugin.getDataFolder() + "/default.board");
	 			// write the inputStream to a FileOutputStream
				FileOutputStream fos = new FileOutputStream(f);
				int read = 0;
				byte[] bytes = new byte[1024];
			
				while ((read = inputStream.read(bytes)) != -1)
					fos.write(bytes, 0, read);

				fos.flush();
				fos.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return loadBoard("default");
	}	
	
	/**
	 * destroys the current board and replaces it with the next board. <br>
	 * Use this method to destroy and create board;<br>
	 * Note: this method should be used twice, once to the original board, then to the new board.
	 * @param current the board currently in the world
	 * @param next the board that you want
	 * @param location the location of the boards
	 */
	public static void switchBoard(Board current, Board next, Location location) {
		// destroy delayed blocks first
		for (Vector v : current.delayed.keySet()) {
			new BlockRep().setBlock(location.clone().add(v).getBlock());
		}
		// build other blocks
		new BoardBuilder(next, location);
	}
	
	
	/**
	 * Loads a board style out of cache/save files.
	 * @param name the style name
	 * @return the loaded board
	 */
	public static Board loadBoard(String name){
		try {
			if (loadedBoards.containsKey(name))
				return loadedBoards.get(name);
			return Board.loadBoard(name);
		} catch (IOException e) {
			return null;
		}
	}
	
	public static List<String> allBoards() {
		List<String> boards = new ArrayList<>();
		File[] files = plugin.getDataFolder().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (name.endsWith(".board"));
			}
		});
		for (File f : files) {
			boards.add(f.getName().split(".board")[0]);
		}
		return boards;
	}
	
	public static void saveBoard(Board board) {
		try {
			loadedBoards.put(board.name, board);
			board.saveBoard();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Material[] bannedArray = {
		Material.AIR,
		Material.DIRT,
		Material.GRASS,
		Material.STONE,
		Material.BEDROCK,
		Material.GRAVEL,
		Material.SAND,
		Material.LONG_GRASS,
		Material.SANDSTONE,
		Material.WATER,
		Material.LAVA,
	};
	private static List<Material> banned = Arrays.asList(bannedArray);
	private static ArrayList<Block> checked = new ArrayList<>();
	private static ArrayList<Block> toCheck = new ArrayList<>();
	
	/** 
	 * Gets the blocks surrounding the given block
	 */
	private static void getConnected(Block block) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					Location l = block.getLocation().add(i, j, k);
					Block b2 = l.getBlock();
					if (!banned.contains(b2.getType()) && !checked.contains(b2) && !toCheck.contains(b2)) {
						toCheck.add(b2);
					}
				}
			}
		}
	}
	
	/**
	 * Gets the bounds of the structure.
	 * @return an array with the minimum and maximm points. 
	 */
	@SuppressWarnings("unchecked")
	public static Location[] getBoundingStructure(Player p, String style) {
		@SuppressWarnings("deprecation")
		Block target = p.getTargetBlock(null, 100);
		
		getConnected(target);
		while (toCheck.size() > 0) {
			ArrayList<Block> toCheckClone = (ArrayList<Block>)toCheck.clone();
			checked.addAll(toCheck);
			toCheck.clear();
			for (Block block : toCheckClone) {
				getConnected(block);
			}
		}
		
		Location lTarget = target.getLocation();
		int minx, maxx, miny, maxy, minz, maxz;
		minx = maxx = lTarget.getBlockX();
		miny = maxy = lTarget.getBlockY();
		minz = maxz = lTarget.getBlockZ();
		for (Block b : checked) {
			Location l = b.getLocation();
			minx = (int)Math.min(l.getX(), minx);
			maxx = (int)Math.max(l.getX(), maxx);
			miny = (int)Math.min(l.getY(), miny);
			maxy = (int)Math.max(l.getY(), maxy);
			minz = (int)Math.min(l.getZ(), minz);
			maxz = (int)Math.max(l.getZ(), maxz);
		}
		
		checked.clear();
		toCheck.clear();
		
		return new Location[] {
				new Location(target.getWorld(), minx, miny, minz),
				new Location(target.getWorld(), maxx, maxy, maxz)
		};
	}
	
	/**
	 * Creates a board style 
	 */
	public static Board createStyle(String style, Location min, Location max) {
		int xSize = max.getBlockX() - min.getBlockX();
		int ySize = max.getBlockY() - min.getBlockY();
		int zSize = max.getBlockZ() - min.getBlockZ();
		return createStyle(style, min, xSize, ySize, zSize);
	}

	/**
	 * Creates a board style 
	 */
	public static Board createStyle(String style, Location loc, int xSize, int ySize
			, int zSize) {
		Board board = new Board();
		board.name = style;
		board.xSize = xSize;
		board.ySize = ySize;
		board.zSize = zSize;		
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				for (int k = 0; k < zSize; k++) {
					Vector v = new Vector(i, j, k);
					BlockRep block = new BlockRep(loc.clone().add(v).getBlock());
					board.addBlock(block, v);
				}
			}
		}
		return board;
	}
}