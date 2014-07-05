package io.github.mdsimmo.bomberman;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class Game implements Listener {

	private static HashMap <String, Game> gameRegestry = new HashMap<>();
	private static Plugin plugin = Bomberman.instance;

	/**
	 * finds the game associated with the given name
	 */
	public static Game findGame(String name) {
		return gameRegestry.get(name);
	}
	
	public static List<Game> allGames() {
		List<Game> games = new ArrayList<>();
		for (String name : gameRegestry.keySet()) {
			games.add(findGame(name));
		}
		return games;
	}
	
	/**
	 * Regsters the game
	 * @param game The game to register
	 */
	public static void register(Game game) {
		gameRegestry.put(game.name, game);
	}
	
	public static void deregister(Game game) {
		gameRegestry.remove(game.name);
		EntityDamageEvent.getHandlerList();
		for (PlayerRep rep : game.observers) {
			rep.reset();
			game.updateGame();
			HandlerList.unregisterAll(rep);
		}
		HandlerList.unregisterAll(game.protector);
		File f = new File(plugin.getDataFolder()+"/"+game.name+".game");
		f.delete();
		f = new File(plugin.getDataFolder()+"/"+game.name+".old.board");
		f.delete();
	}
	
	public static void loadGames() {
		File[] files = plugin.getDataFolder().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (name.endsWith(".game"));
			}
		});
		for (File f : files) {
			loadGame(f.getName().split(".game")[0]);
		}
	}
	
	public static void loadGame(String name) {
		try {
			SaveReader sr = new SaveReader(name+".game"); 
			int x = sr.readInt();
			int y = sr.readInt();
			int z = sr.readInt();
			World w = plugin.getServer().getWorld(sr.readPart());
			Game game = new Game(name, new Location(w, x, y, z));
			game.board = BoardGenerator.loadBoard(sr.readPart());
			game.oldBoard = BoardGenerator.loadBoard(sr.readPart());			
			register(game);
			sr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveGame() {
		try {
			SaveWriter sw = new SaveWriter(name + ".game");
			sw.writePart(loc.getBlockX());
			sw.writePart(loc.getBlockY());
			sw.writePart(loc.getBlockZ());
			sw.writePart(loc.getWorld().getName());
			sw.writePart(board.name);
			sw.writePart(oldBoard.name);
			sw.close();
			BoardGenerator.saveBoard(oldBoard);
			for (PlayerRep rep : (List<PlayerRep>)players.clone())
				rep.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected String name; 
	protected Location loc;
	protected Board oldBoard;
	protected boolean isPlaying;
	private GameProtection protector;
	public ItemStack stake = Config.stake;
	private ItemStack[] drops = {
			new ItemStack(Material.TNT),
			new ItemStack(Material.BLAZE_POWDER),
			new Potion(PotionType.INSTANT_HEAL, 1).toItemStack(1),
			new Potion(PotionType.INVISIBILITY, 1).toItemStack(1),
			new Potion(PotionType.SPEED, 2).toItemStack(1),
	};
	protected ArrayList<PlayerRep> observers = new ArrayList<>();
	protected ArrayList<PlayerRep> players = new ArrayList<>();
	public Stack<Player> winners = new Stack<>();
	public Board board;
	public int bombs = Config.bombs;
	public int power = Config.power;
	public int lives = Config.lives;
	
	public Game(String name, Location loc) {
		this.name = name;
		this.loc = loc;
		protector = new GameProtection(this);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public boolean containsLocation(Location l) {
		return (l.getBlockX() >= loc.getX() && l.getBlockX() < loc.getBlockX() + board.xSize)
				&& (l.getBlockY() >= loc.getY() && l.getBlockY() < loc.getBlockY() + board.ySize)
				&& (l.getBlockZ() >= loc.getZ() && l.getBlockZ() < loc.getBlockZ() + board.zSize);
	}
	
	public Vector findSpareSpawn() {
		if (board.spawnPoints.size() > 0)
			return board.spawnPoints.remove(0);
		else 
			return null;
	}
	
	public void startGame() {
		new GameStarter();
	}
	
	private class GameStarter implements Runnable {
		int count = 3;
		
		public GameStarter() {
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this);
		}
		
		public void run() {
			if (count > 0) {
				for (PlayerRep rep : players) {
					rep.player.sendMessage("" + count);
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 20);
				}
			} else {
				for (PlayerRep rep : observers) {
					rep.player.sendMessage(ChatColor.YELLOW + "Game started!");
					isPlaying = true;
				}
			}
			count--;
		}
		
	}
	
	public void drop(Location l) {
		if (Math.random() < 0.1) {
			int rand = (int)(Math.random()*drops.length);
			l.getWorld().dropItem(l, drops[rand]);
		}
	}
	
	public PlayerRep getPlayerRep(Player player) {
		for (PlayerRep p : observers) {
			if (p.player == player) {
				return p;
			}
		}
		return null;
	}
	
	/**
	 * updates the status of the game.
	 */
	public void updateGame() {
		if (players.size() <= 1) {
			isPlaying = false;
			// add the surviver to winners
			for (PlayerRep rep : players) {
				winners.add(rep.player);
				rep.reset();
			}
			players.clear();
			Player topPlayer = winners.lastElement();
			topPlayer.getInventory().addItem(new ItemStack(Material.DIAMOND, 3));
			
			players.clear();
			for (PlayerRep rep : observers) {
				rep.player.sendMessage(ChatColor.YELLOW + "The game is over!");
				rep.player.sendMessage(scoreDisplay());
			}
		}
	}
	
	
	public String scoreDisplay() {
		 String display = "The winners are:\n";
		 int place = 1;
		 while (winners.size() > 0) {
			 Player player = winners.pop();
			 display += " " + place++ + ": " + player.getName();
		 }
		 return display;
	}
	
	/**
	 * call when a player dies
	 */
	public void alertRemoval(PlayerRep player) {
		winners.add(player.player);
		for (PlayerRep rep : observers) {
			rep.player.sendMessage(ChatColor.YELLOW + player.player.getName() + " is out");
		}
		updateGame();
	}
}
