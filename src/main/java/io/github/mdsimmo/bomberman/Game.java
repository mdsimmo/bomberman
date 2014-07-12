package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.Bomb.DeathBlock;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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

	private static HashMap<String, Game> gameRegestry = new HashMap<>();
	private static Plugin plugin = Bomberman.instance;

	/**
	 * finds the game associated with the given name
	 */
	public static Game findGame(String name) {
		return gameRegestry.get(name);
	}

	public static List<String> allGames() {
		List<String> games = new ArrayList<>();
		for (String name : gameRegestry.keySet()) {
			games.add(name);
		}
		return games;
	}

	/**
	 * Regsters the game
	 * 
	 * @param game
	 *            The game to register
	 */
	public static void register(Game game) {
		gameRegestry.put(game.name, game);
	}

	public void deregister() {
		gameRegestry.remove(name);
		EntityDamageEvent.getHandlerList();
		terminate();
		for (PlayerRep rep : new ArrayList<PlayerRep>(observers)) {
			HandlerList.unregisterAll(rep);
		}
		HandlerList.unregisterAll(protector);
		File f = new File(plugin.getDataFolder() + "/" + name + ".game");
		f.delete();
		f = new File(plugin.getDataFolder() + "/" + name + ".old.board");
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
			SaveReader sr = new SaveReader(name + ".game");
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
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			plugin.getLogger().info("error while saving " + name);
		}
		
		if (BoardGenerator.loadBoard(oldBoard.name) == null)
			BoardGenerator.saveBoard(oldBoard);
		for (PlayerRep rep : new ArrayList<PlayerRep>(players))
			rep.kill();
	}

	protected String name;
	protected Location loc;
	protected Board oldBoard;
	protected boolean isPlaying;
	private GameProtection protector;
	public ItemStack stake = Config.stake;
	private ItemStack[] drops = { 
			/*new ItemStack(Material.TNT),
			new ItemStack(Material.TNT),
			new ItemStack(Material.TNT),
			new ItemStack(Material.TNT),
			new ItemStack(Material.BLAZE_POWDER),
			new ItemStack(Material.BLAZE_POWDER),
			new ItemStack(Material.BLAZE_POWDER),
			new Potion(PotionType.INSTANT_HEAL, 1).toItemStack(1),
			new Potion(PotionType.INSTANT_HEAL, 1).toItemStack(1),*/
			new Potion(PotionType.SPEED, 2).toItemStack(1)};
	protected ArrayList<PlayerRep> observers = new ArrayList<>();
	public ArrayList<PlayerRep> players = new ArrayList<>();
	public Board board;
	public int bombs = Config.bombs;
	public int power = Config.power;
	public int lives = Config.lives;
	public List<DeathBlock> deathBlocks = new ArrayList<>();
	public Map<Block, Bomb> explosions = new HashMap<>();
	
	public Game(String name, Location loc) {
		this.name = name;
		this.loc = loc;
		protector = new GameProtection(this);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public boolean containsLocation(Location l) {
		return (l.getBlockX() >= loc.getX() && l.getBlockX() < loc.getBlockX()
				+ board.xSize)
				&& (l.getBlockY() >= loc.getY() && l.getBlockY() < loc
						.getBlockY() + board.ySize)
				&& (l.getBlockZ() >= loc.getZ() && l.getBlockZ() < loc
						.getBlockZ() + board.zSize);
	}

	public Vector findSpareSpawn() {
		for (Vector v : board.spawnPoints) {
			plugin.getLogger().info("spawn: " + v);
			if (blockEmpty(v))
				return v;
		}
		return null;
	}

	/**
	 * gets if there are any players <b>in</b> the block given by the vector (from game corner)
	 * @return true if no player is in the block
	 */
	private boolean blockEmpty(Vector v) {
		for (PlayerRep rep : players) {
			Block under = rep.player.getLocation().getBlock();
			Block block = loc.clone().add(v).getBlock();
			if (block.equals(under))
				return false;
		}
		return true;
	}
	
	/**
	 * Starts the game
	 * @return true if the game was started succesfully
	 */
	public boolean startGame() {
		if (players.size() > 0) {
			new GameStarter();
			return true;
		} else {
			return false;
		}
	}

	private class GameStarter implements Runnable {
		int count = 3;

		public GameStarter() {
			plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, this);
		}

		public void run() {
			if (count > 0) {
				for (PlayerRep rep : players)
					rep.player.sendMessage("" + count);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 20);
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
		//if (Math.random() < 0.1) {
			int rand = (int) (Math.random() * drops.length);
			l.getWorld().dropItem(l, drops[rand]);
		//}
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
	 * Terminates the game. <br>
	 * Kicks all playes out. Doesn't give awards 
	 */
	public void terminate() {
		isPlaying = false;
		for (PlayerRep rep : new ArrayList<PlayerRep>(players)) {
			rep.kill();
		}
	}
	
	/**
	 * updates the status of the game.
	 */
	public void checkFinish() {
		if (players.size() <= 1 && isPlaying) {
			isPlaying = false;
			
			ArrayList<PlayerRep> winners = new ArrayList<>();
			for (PlayerRep rep : observers) {
				if (rep.deathTime != -1)
					addWinner(winners, rep);
			}
			
			// kill the remaining survivors and add them to the winners
			for (PlayerRep rep : new ArrayList<PlayerRep>(players)) {
				rep.kill();
				winners.add(0, rep);
			}
			Player topPlayer = winners.get(0).player;
			topPlayer.getInventory()
					.addItem(stake);
			System.out.println(winners);
			
			players.clear();
			for (PlayerRep rep : observers) {
				rep.player.sendMessage(ChatColor.YELLOW + "The game is over!");
				rep.player.sendMessage(scoreDisplay(winners));
			}
			BoardGenerator.switchBoard(this.board, this.board, loc);
		}
	}
	
	private void addWinner(ArrayList<PlayerRep> winners, PlayerRep rep) {
		for (int i = 0; i < winners.size(); i++) {
			if (rep.deathTime > winners.get(i).deathTime) {
				winners.add(i, rep);
				return;
			}
		}
		winners.add(rep);
	}

	public String scoreDisplay(ArrayList<PlayerRep> winners) {
		String display = "The leaders are:\n";
		int i = 0;
		while (i < winners.size() && i < 8) {
			Player player = winners.get(i).player;
			i++;
			String place;
			switch (i) {
			case 1:
				place = "1st";
				break;
			case 2:
				place = "2nd";
				break;
			case 3:
				place = "3rd";
				break;
			default:
				place = i+"th";
			}			
			display += " " + place + ": " + player.getName() + "\n";
		}
		return display;
	}

	/**
	 * call when a player dies
	 */
	public void alertRemoval(PlayerRep player) {
		for (PlayerRep rep : observers) {
			rep.player.sendMessage(ChatColor.YELLOW + player.player.getName()
					+ " is out");
		}
		checkFinish();
	}
}
