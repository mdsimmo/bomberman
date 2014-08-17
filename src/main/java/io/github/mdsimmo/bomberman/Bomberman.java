package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.CommandHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	/**
	 * Puts the given objects into the string and colors them. <br>
	 * use '%' to specify where to put the objects in the string.
	 * use '%b' for a Board, 'c' for a command (as a String or a Command), 'p' for a Player or PlayerRep and 'g' for a Game. 
	 * @param s the string to color
	 * @param objects the objects 
	 * @return the formated string
	 */
	public static String format(String s, Object ... objects) {
		String formated = "";
		int objectIndex = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '%':
				char lastChar = s.charAt(Math.max(0, i-1));
				if (lastChar == '\\') {
					formated += c;
					break;
				}
				String part;
				Object o = objects[objectIndex]; 
				switch (s.charAt(i+1)) {
				case 'p':
					if (o instanceof Player)
						part = PlayerRep.getPlayerRep((Player)o).getName();
					else if (o instanceof PlayerRep)
						part = ((PlayerRep)o).getName();
					else
						part = (String)o;
					formated += ChatColor.YELLOW + part + ChatColor.RESET;
					break;
				case 'g':
					if (o instanceof Game) 
						part = ((Game) o).name;
					else
						part = (String) o;
					formated += ChatColor.YELLOW + part + ChatColor.RESET;
					break;
				case 'b':
					if (o instanceof Board) 
						part = ((Board) o).name;
					else
						part = (String) o;
					formated += ChatColor.YELLOW + part + ChatColor.RESET;
					break;
				case 'c':
					if (o instanceof Command) {
						Command cmd = (Command)o;
						part = "/" + cmd.path();
					} else
						part = (String)o;
					formated += ChatColor.AQUA + part + ChatColor.RESET;
					break;
				default:
					throw new IllegalArgumentException("Can only use 'c', 'b', 'g' or 'p' after '%'");
				}
				i++;
				objectIndex++;
				break;
			default:
				formated += c;
				break;
			}
		}
		return formated;
		
	}
	
	public static void sendMessage(Player[] playerList, String message, Object ... objs) {
        for(Player p : playerList) {
            sendMessage(p, message, objs);
        }
    }
    
    public static void sendMessage(ArrayList<PlayerRep> playerList, String message, Object ... objs) {
        for(PlayerRep p : playerList) {
            sendMessage(p, message, objs);
        }
    }
    
    public static void sendMessage(PlayerRep rep, String message, Object ... objs) {
        sendMessage(rep.player, message, objs);
    }
    
    public static void sendMessage(CommandSender sender, String message, Object ... objs) {
        sender.sendMessage(format(ChatColor.GREEN + "[BomberMan] " + ChatColor.RESET + message, objs));
    }
    
    public static void sendMessage(CommandSender sender, Map<String, String> points, Object ... objs) {
    	for (Map.Entry<String, String> point : points.entrySet()) {
    		sender.sendMessage(format("   " + ChatColor.GOLD + point.getKey() + ": " + ChatColor.RESET + point.getValue(), objs));
    	}
    }
    
    public static void sendMessage(CommandSender sender, List<String> list, Object ... objs) {
    	for (String line : list) {
    		sender.sendMessage(format("   " + ChatColor.RESET + line, objs));
    	}
    }
    
	public static String heading (String text) {
		String head = ChatColor.YELLOW + "--------- "
				+ ChatColor.RESET + text + " " + ChatColor.YELLOW;
		for (int i = text.length(); i < 38; i++) {
			head += "-";
		}
		return head;
	}
	
	public static void sendHeading(CommandSender sender, String text) {
		sender.sendMessage(heading(text));
	}
	
	@Override
	public void onEnable() {
		instance = this;
		new CommandHandler();
		convertArenas();
		Game.loadGames();
	}
	
	@Override
	public void onDisable() {
		for (String game : Game.allGames()) {
			Game.findGame(game).stop();
			Game.findGame(game).saveGame();
		}
	}
	
	private void convertArenas() {
		String[] files = getDataFolder().list(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".board");
			}
		});
		for (String filename : files) {
			getLogger().info("converting " + filename);
			File file = new File(getDataFolder(), filename);
			try {
				Board board = loadArena(file);
				BoardGenerator.saveBoard(board);
				board = null;
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
				getLogger().info("Couldn't convert " + file.getName());
			}
		}
	}
	
	// TODO remove every thing below this after the next release
	private Board loadArena(File file) throws Exception {
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
				getLogger().info("IllegalArgumentException while converting: " + name);
				break;
			}
		}
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
