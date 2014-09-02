package io.github.mdsimmo.bomberman.save;

import io.github.mdsimmo.bomberman.BlockRep;
import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class BoardSaver extends Save{

	private Board board;
	
	public BoardSaver(Board board) {
		super(board.name + ".arena");
		this.board = board;
	}
	
	public BoardSaver(File file) {
		super(file); 
	}

	@Override
	public void save() {
		set("name", board.name);
		set("size.x", board.xSize);
		set("size.y", board.ySize);
		set("size.z", board.zSize);
		
		// save standard blocks
		CompressedSection section = new CompressedSection();
		for (int i = 0; i < board.xSize; i++) {
			for (int j = 0; j < board.ySize; j++) {
				for (int k = 0; k < board.zSize; k++) {
					BlockRep rep = board.getBlock(i, j, k);
					section.addParts(rep.toString());
				}
			}
		}
		set("blocks.standard", section);
		
		// save special blocks
		section.reset();
		for (Map.Entry<Vector, BlockRep> entry : board.delayed.entrySet()) {
			BlockRep rep = entry.getValue();
			Vector v = entry.getKey();
			CompressedSection special = new CompressedSection(':');
			special.addParts(v.getBlockX(), v.getBlockY(), v.getBlockZ());
			special.addParts(rep.toString());
			section.addParts(special);
		}
		set("blocks.special", section);
		super.save();
	}
	
	@SuppressWarnings("unchecked")
	public static Board loadBoard(File file) throws IOException {
		if (!file.exists())
			return null;
		BoardSaver save = new BoardSaver(file);
		save.convert(save.getVersion("version"));
		int x = save.getInt("size.x");
		int y = save.getInt("size.y");
		int z = save.getInt("size.z");
		
		Board board = new Board(save.getString("name"), x, y, z);
		
		// Read out normal blocks
		CompressedSection blocks = new CompressedSection();
		blocks.setValue(save.getString("blocks.standard"));
		List<String> sections = blocks.readParts();
		// decode read string
		int count = 0;
		for (int i = 0; i < board.xSize; i++) {
			for (int j = 0; j < board.ySize; j++) {
				for (int k = 0; k < board.zSize; k++) {
					BlockRep block = BlockRep.loadFrom(sections.get(count++));
					board.addBlock(block, new Vector(i, j, k));
				}
			}
		}
		
		// read out the delayed blocks
		blocks.setValue(save.getString("blocks.special"));
		sections = blocks.readParts();
		CompressedSection special = new CompressedSection(':');
		for (String s : sections) {
			special.setValue(s);
			List<String> parts = special.readParts();
			int x2 = Integer.parseInt(parts.get(0), 10);
			int y2 = Integer.parseInt(parts.get(1), 10);
			int z2 = Integer.parseInt(parts.get(2), 10);
			Vector v = new Vector(x2, y2, z2);
			BlockRep b = BlockRep.loadFrom(parts.get(3));
			board.addBlock(b, v);
		}
		
		board.setDestructables((List<Material>) Config.BLOCKS_DESTRUCTABLE.getValue(save));
		board.setDropping((List<Material>) Config.BLOCKS_DROPPING.getValue(save));
		return board;
	}
	
	@Override
	public void convert(Version version) {
		switch (version) {
		case FUTURE:
			plugin.getLogger().info("Unkowen verion " + version + " in " + file.getName());
			break;
		case V0_0_3a:
		case V0_0_3:
			break;
		case PAST:
		default:
			break;
		}
	}
	
	public static void convertArenas() {
		File[] files = plugin.getDataFolder().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".board");
			}
		});
		for (File file : files) {
			plugin.getLogger().info("converting " + file.getName());
			try {
				// the actual conversion bit
				Board board = loadOldArena(file);
				BoardGenerator.saveBoard(board);
				if (!file.delete())
					plugin.getLogger().info("Couldn't delete " + file.getName() + ". Please delete it manually");
			} catch (Exception e) {
				e.printStackTrace();
				plugin.getLogger().info("Couldn't convert " + file.getName());
			}
			plugin.getLogger().info("converted " + file.getName() + " successfully");
		}
	}
	
	private static Board loadOldArena(File file) throws Exception {
		SaveReader sr = new SaveReader(file);
		String name = file.getName().split("\\.board")[0];
		Board board = new Board(name, sr.readInt(), sr.readInt(), sr.readInt());
		for (int i = 0; i < board.xSize; i++) {
			for (int j = 0; j < board.ySize; j++) {
				for (int k = 0; k < board.zSize; k++) {
					BlockRep block = loadBlock(sr);
					board.addBlock(block, new Vector(i, j, k));
				}
			}
		}
		// read out the delayed blocks
		while (true) {
			try {
				BlockRep block = loadBlock(sr);
				int x = sr.readInt();
				int y = sr.readInt();
				int z = sr.readInt();
				board.addBlock(block, new Vector(x, y, z));
			} catch (NullPointerException e) {
				// caused when BlockRep tries to read past the end of the file
				break;
			} catch (IllegalArgumentException e) {
				plugin.getLogger().info("IllegalArgumentException while converting: " + name);
				break;
			}
		}
		sr.close();
		return board;
	}
	
	private static BlockRep loadBlock(SaveReader sr) throws IOException {
		BlockRep rep = new BlockRep();
		rep.material = Material.valueOf(sr.readPart());
		rep.data = Byte.parseByte(sr.readPart(), 10);
		return rep;
	}
	
	private static class SaveReader extends FileReader {

		private static Plugin plugin = Bomberman.instance;
		
		public SaveReader(File file) throws FileNotFoundException {
			super(file);
		}
		
		public SaveReader(String filename) throws FileNotFoundException {
			super(plugin.getDataFolder() + "/" + filename);
		}

		/**
		 * reads the next part of the file.
		 * @return the read part. null if the end is reached
		 */
		public String readPart() throws IOException {
			int read = 0;
			String part = "";
			while (true) {
				read = read();
				if (read == -1)
					return null;
				if (read == ':')
					return part;
				part += (char)read;
			}
		}
		
		public int readInt() throws IOException {
			return Integer.parseInt(readPart(), 10);
		}
	}

}
