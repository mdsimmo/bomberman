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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Game implements Listener {

	class GameStarter implements Runnable {
		int count = 3;
		private int taskId;

		public GameStarter() {
			taskId = plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, this);
		}

		public GameStarter(int delay) {
			count = delay;
			taskId = plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, this);
		}

		public void destroy() {
			plugin.getServer().getScheduler().cancelTask(taskId);
			countdownTimer = null;
		}

		public int getTaskId() {
			return taskId;
		}

		public void run() {
			// Let online players know about the fun :)
			if (count == autostartDelay) {
				Bomberman.sendMessage(plugin.getServer().getOnlinePlayers(),
						"Game " + ChatColor.YELLOW + name + ChatColor.WHITE
								+ " starting in " + count + " seconds!");
			}

			if (count > 0) {
				// Keep the timer running until it ends
				taskId = plugin.getServer().getScheduler()
						.scheduleSyncDelayedTask(plugin, this, 20);

				// Notify waiting players every 15 seconds until count <= 15,
				// notify every 5 until count <= 3,
				// notify every second last 3 seconds
				if (count % 15 == 0 || (count < 15 && count % 5 == 0)
						|| count <= 3)
					Bomberman.sendMessage(observers, "Game starting in "
							+ count + "...");
			} else {
				Bomberman.sendMessage(observers, ChatColor.YELLOW
						+ "Game started!");
				isPlaying = true;
				if (suddenDeath >= 0 || timeout >= 0)
					new SuddenDeathCounter(Game.this);
				// Cleanup and destroy the countdown timer
				destroy();
			}
			count--;
		}

	}

	private static HashMap<String, Game> gameRegistry = new HashMap<>();

	private static Plugin plugin = Bomberman.instance;

	public static List<String> allGames() {
		List<String> games = new ArrayList<>();
		for (String name : gameRegistry.keySet()) {
			games.add(name);
		}
		return games;
	}

	/**
	 * finds the game associated with the given name
	 */
	public static Game findGame(String name) {
		return gameRegistry.get(name.toLowerCase());
	}

	public static File getSaveFile(String name) {
		return new File(plugin.getDataFolder(), name.toLowerCase() + ".game");
	}

	public static void loadGame(String name) {
		YamlConfiguration save = YamlConfiguration
				.loadConfiguration(getSaveFile(name));
		name = save.getString("name");
		int x = save.getInt("location.x");
		int y = save.getInt("location.y");
		int z = save.getInt("location.z");
		World w = plugin.getServer().getWorld(save.getString("location.world"));
		Game game = new Game(name, new Location(w, x, y, z));
		game.save = save;
		game.board = BoardGenerator.loadBoard(save.getString("arena.current"));
		game.oldBoard = BoardGenerator.loadBoard(save.getString("arena.old"));

		game.initVars();

		register(game);
	}

	public static void loadGames() {
		File data = plugin.getDataFolder();
		if (!data.exists())
			data.mkdirs();
		File[] files = data.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (name.endsWith(".game"));
			}
		});
		for (File f : files) {
			loadGame(f.getName().split(".game")[0]);
		}
	}

	/**
	 * Registers the game
	 * 
	 * @param game
	 *            The game to register
	 */
	public static void register(Game game) {
		gameRegistry.put(game.name.toLowerCase(), game);
	}

	private boolean autostart;
	private int autostartDelay;
	public Board board;
	private int bombs;
	private GameStarter countdownTimer = null;
	public List<DeathBlock> deathBlocks = new ArrayList<>();
	private double dropChance;
	private List<ItemStack> drops;
	public Map<Block, Bomb> explosions = new HashMap<>();
	private ItemStack fare;
	public boolean isPlaying;
	private int lives;
	public Location loc;
	private int minPlayers;
	public String name;
	public ArrayList<PlayerRep> observers = new ArrayList<>();
	public Board oldBoard;
	public ArrayList<PlayerRep> players = new ArrayList<>();
	private boolean pot;
	private int power;
	private ItemStack prize;
	private boolean protection;
	private boolean protectFire;
	private boolean protectPlace;
	private boolean protectBreak;
	private boolean protectExplosion;
	private boolean protectDamage;
	private boolean protectPVP;
	private GameProtection protector;
	private YamlConfiguration save = new YamlConfiguration();
	private int suddenDeath;
	private boolean suddenDeathStarted = false;
	private int timeout;
	private List<ItemStack> initialitems;
	private ArrayList<PlayerRep> winners = new ArrayList<>();

	public Game(String name, Location loc) {
		this.name = name;
		this.loc = loc;
		initVars();
		protector = new GameProtection(this);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void addPlayer(PlayerRep rep) {
		players.add(rep);
		if (!observers.contains(rep))
			observers.add(rep);
		if (autostart) {
			if (findSpareSpawn() == null) {
				startGame();
			} else if (this.players.size() >= this.minPlayers) {
				startGame(autostartDelay, false);
			}
		}

	}

	private void addWinner(PlayerRep rep) {
		winners.add(0, rep);
	}

	/**
	 * call when a player dies
	 */
	public void alertRemoval(PlayerRep rep) {
		addWinner(rep);
		players.remove(rep);
		if (!checkFinish()) {
			Bomberman.sendMessage(observers, ChatColor.YELLOW + rep.getName()
					+ ChatColor.WHITE + " is out.");
		}
		if (players.size() <= minPlayers && getCountdownTimer() != null) {
			getCountdownTimer().destroy();
			for (PlayerRep p : players) {
				Bomberman
						.sendMessage(p.player,
								"Not enough players remaining. The countdown timer has been stopped.");
			}
		}
	}

	public void announceQueue() {
		Bomberman.sendMessage(plugin.getServer().getOnlinePlayers(), "Game "
				+ ChatColor.YELLOW + name + ChatColor.WHITE
				+ " is starting soon. Type " + ChatColor.AQUA + "/join-game "
				+ name + ChatColor.WHITE + " to play!");
	}

	/**
	 * gets if there are any players <b>in</b> the block given by the vector
	 * (from game corner)
	 * 
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
	 * updates the status of the game.
	 * 
	 * @return true if the game has finished;
	 */
	public boolean checkFinish() {
		if (players.size() <= 1 && isPlaying) {
			isPlaying = false;

			// kill the survivors
			for (PlayerRep rep : new ArrayList<>(players))
				rep.kill();

			// get the total winnings
			if (pot == true)
				if (fare == null)
					prize = null;
				else
					prize = new ItemStack(fare.getType(), fare.getAmount()
							* winners.size());

			// give the winner the prize
			if (prize != null) {
				Player topPlayer = winners.get(0).player;
				topPlayer.getInventory().addItem(prize);
			}

			// display the scores
			Bomberman.sendMessage(observers, ChatColor.YELLOW
					+ "The game is over!");
			Bomberman.sendMessage(observers, scoreDisplay(winners));

			// reset the game
			BoardGenerator.switchBoard(this.board, this.board, loc);
			stop();

			return true;
		}
		return !isPlaying;
	}

	public boolean containsLocation(Location l) {
		return (l.getBlockX() >= loc.getX() && l.getBlockX() < loc.getBlockX()
				+ board.xSize)
				&& (l.getBlockY() >= loc.getY() && l.getBlockY() < loc
						.getBlockY() + board.ySize)
				&& (l.getBlockZ() >= loc.getZ() && l.getBlockZ() < loc
						.getBlockZ() + board.zSize);
	}

	public void destroy() {
		gameRegistry.remove(name.toLowerCase());
		stop();
		HandlerList.unregisterAll(protector);
		HandlerList.unregisterAll(this);
		Bomberman.sendMessage(observers, "Game %g destroyed", this);
		File f = new File(plugin.getDataFolder() + "/" + name + ".game");
		f.delete();
		f = new File(plugin.getDataFolder() + "/" + name + ".old.board");
		f.delete();
		for (PlayerRep rep : PlayerRep.allPlayers())
			if (rep.getGameActive() == this)
				rep.setGameActive(null);
	}

	public void drop(Location l, Material type) {
		if (Math.random() < dropChance && board.isDropping(type)) {
			int sum = 0;
			for (ItemStack stack : drops)
				sum += stack.getAmount();
			double rand = Math.random() * sum;
			for (ItemStack stack : drops) {
				rand -= stack.getAmount();
				if (rand <= 0) {
					ItemStack drop = stack.clone();
					drop.setAmount(1);
					l.getWorld().dropItem(l, drop);
					return;
				}
			}
		}
	}

	public Vector findSpareSpawn() {
		for (Vector v : board.spawnPoints) {
			if (blockEmpty(v))
				return v;
		}
		return null;
	}

	public boolean getAutostart() {
		return autostart;
	}

	public int getAutostartDelay() {
		return autostartDelay;
	}

	public int getBombs() {
		return bombs;
	}

	public GameStarter getCountdownTimer() {
		return countdownTimer;
	}

	public ItemStack getFare() {
		return fare;
	}

	public int getLives() {
		return lives;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public boolean getPot() {
		return pot;
	}

	public int getPower() {
		return power;
	}

	public ItemStack getPrize() {
		return prize;
	}

	public int getSuddenDeath() {
		return suddenDeath;
	}

	public int getTimeout() {
		return timeout;
	}

	/**
	 * initialises the players inventory for a game handelling player's handycas
	 * and things <br>
	 * make sure the player's inventory is cleared before calling this.
	 * 
	 * @param rep
	 *            the PlayerRep to initialise
	 */
	public void initialise(PlayerRep rep) {
		rep.player.getInventory().clear();
		Stats stat = Stats.get(rep);
		for (ItemStack stack : initialitems) {
			ItemStack s = stack.clone();
			s.setAmount(s.getAmount() - stat.hadicapLevel);
			rep.player.getInventory().addItem(s);
		}
		if (stat.hadicapLevel >= 1)
			rep.player.setHealth(Math.max(rep.player.getHealth()
					- stat.hadicapLevel, 1));
		if (stat.hadicapLevel >= 2)
			rep.player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
					stat.hadicapLevel * 20 * 60, stat.hadicapLevel));
	}

	public void initVars() {
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(getSaveFile(name));
		prize = Config.PRIZE.getValue(config);
		pot = Config.POT.getValue(config);
		fare = Config.FARE.getValue(config);
		bombs = Config.BOMBS.getValue(config);
		power = Config.POWER.getValue(config);
		lives = Config.LIVES.getValue(config);
		minPlayers = Config.MIN_PLAYERS.getValue(config);
		autostart = Config.AUTOSTART.getValue(config);
		autostartDelay = Config.AUTOSTART_DELAY.getValue(config);
		drops = Config.DROPS_ITEMS.getValue(config);
		dropChance = Config.DROPS_CHANCE.getValue(config);
		protection = Config.PROTECT.getValue(config);
		protectBreak = Config.PROTECT_DESTROYING.getValue(config);
		protectPlace = Config.PROTECT_PLACING.getValue(config);
		protectFire = Config.PROTECT_FIRE.getValue(config);
		protectExplosion = Config.PROTECT_EXPLOSIONS.getValue(config);
		protectDamage = Config.PROTECT_DAMAGE.getValue(config);
		protectPVP = Config.PROTECT_PVP.getValue(config);
		suddenDeath = Config.SUDDEN_DEATH.getValue(config);
		timeout = Config.TIME_OUT.getValue(config);
		initialitems = Config.INITIAL_ITEMS.getValue(config);
	}

	public boolean isSuddenDeath() {
		return suddenDeathStarted;
	}

	public void saveGame() {
		try {
			save.set("name", name);
			save.set("location.world", loc.getWorld().getName());
			save.set("location.x", loc.getBlockX());
			save.set("location.y", loc.getBlockY());
			save.set("location.z", loc.getBlockZ());
			save.set("arena.current", board.name);
			save.set("arena.old", oldBoard.name);

			save.save(getSaveFile(name));
		} catch (IOException e) {
			e.printStackTrace();
		}

		BoardGenerator.saveBoard(oldBoard);
	}

	public String scoreDisplay(ArrayList<PlayerRep> winners) {
		String display = "The scores are:\n";
		int i = 0;
		while (i < winners.size() && i < 8) {
			PlayerRep rep = winners.get(i);
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
				place = i + "th";
			}
			display += " " + place + ": " + rep.player.getName() + " ("
					+ Stats.get(rep).kills + " kills)\n";
		}
		return display;
	}

	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
		save.set(Config.AUTOSTART.getPath(), autostart);
	}

	public void setAutostartDelay(int delay) {
		this.autostartDelay = delay;
		save.set(Config.AUTOSTART_DELAY.getPath(), delay);
	}

	public void setBombs(int bombs) {
		this.bombs = bombs;
		save.set(Config.BOMBS.getPath(), bombs);
	}

	public void setFare(ItemStack fare) {
		this.fare = fare;
		save.set(Config.FARE.getPath(), fare);
	}

	public void setLives(int lives) {
		this.lives = lives;
		save.set(Config.LIVES.getPath(), lives);
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
		save.set(Config.MIN_PLAYERS.getPath(), minPlayers);
	}

	public void setPot(boolean pot) {
		this.pot = pot;
		if (pot)
			save.set(Config.PRIZE.getPath(), true);
		else
			save.set(Config.PRIZE.getPath(), prize);
	}

	public void setPower(int power) {
		this.power = power;
		save.set(Config.POWER.getPath(), power);
	}

	public void setPrize(ItemStack prize) {
		this.prize = prize;
		pot = false;
		save.set(Config.PRIZE.getPath(), prize);
	}

	public void setPrize(ItemStack prize, boolean pot) {
		setPrize(prize);
		setPot(pot);
	}

	public boolean getProtected(Config protection) {
		if (!this.protection)
			return false;
		switch (protection) {
		case PROTECT:
			return this.protection;
		case PROTECT_DESTROYING:
			return protectBreak;
		case PROTECT_EXPLOSIONS:
			return protectExplosion;
		case PROTECT_FIRE:
			return protectFire;
		case PROTECT_PLACING:
			return protectPlace;
		case PROTECT_DAMAGE:
			return protectDamage;
		case PROTECT_PVP:
			return protectPVP;
		default:
			throw new IllegalArgumentException(
					"must use one of the protection options");
		}
	}

	public void setProteced(Config protection, boolean enable) {
		switch (protection) {
		case PROTECT:
			this.protection = enable;
			break;
		case PROTECT_DESTROYING:
			protectBreak = enable;
			break;
		case PROTECT_EXPLOSIONS:
			protectExplosion = enable;
			break;
		case PROTECT_FIRE:
			protectFire = enable;
			break;
		case PROTECT_PLACING:
			protectPlace = enable;
			break;
		case PROTECT_DAMAGE:
			protectDamage = enable;
			break;
		case PROTECT_PVP:
			protectPVP = enable;
			break;
		default:
			throw new IllegalArgumentException(
					"must use one of the protection options");
		}
		save.set(protection.getPath(), enable);
	}

	public void setSuddenDeath(boolean started) {
		if (started == true)
			for (PlayerRep rep : players)
				rep.player.setHealth(1d);
		suddenDeathStarted = started;
	}

	public void setSuddenDeath(int time) {
		suddenDeath = time;
		save.set(Config.SUDDEN_DEATH.getPath(), time);
	}

	public void setTimeout(int time) {
		timeout = time;
		save.set(Config.TIME_OUT.getPath(), time);
	}

	/**
	 * Starts the game with a default delay of 3 seconds
	 * 
	 * @return true if the game was started successfully
	 */
	public boolean startGame() {
		return startGame(3, true);
	}

	/**
	 * Starts the game with a given delay
	 * 
	 * @return true if the game was started successfully
	 */
	public boolean startGame(int delay, boolean override) {
		if (players.size() >= minPlayers) {
			if (override) {
				if (countdownTimer != null)
					countdownTimer.destroy();
				countdownTimer = new GameStarter(delay);
			}
			if (countdownTimer == null) {
				countdownTimer = new GameStarter(delay);
				announceQueue();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Stops the game and kicks all the players out. <br>
	 * Does not give awards.
	 */
	public void stop() {
		isPlaying = false;
		for (PlayerRep rep : new ArrayList<PlayerRep>(players))
			rep.kill();
		winners.clear();
	}

	public void damagePlayer(PlayerRep player, PlayerRep attacker) {
		if (!player.damage())
			return;

		Stats playerStats = Stats.get(player);
		Stats attackerStats = Stats.get(attacker);

		attackerStats.hitGiven++;
		playerStats.hitTaken++;

		if (player.isPlaying()) {
			if (player == attacker) {
				Bomberman.sendMessage(player, "You hit yourself!");
			} else {
				Bomberman.sendMessage(player,
						"You were hit by " + attacker.getName());
				Bomberman.sendMessage(attacker, "You hit " + player.getName());
			}
		} else {
			playerStats.deaths++;
			attackerStats.kills++;
			if (player == attacker) {
				Bomberman.sendMessage(player, ChatColor.RED
						+ "You killed yourself!");
				playerStats.suicides++;
			} else {
				Bomberman.sendMessage(player, ChatColor.RED + "Killed by "
						+ attacker.getName());
				Bomberman.sendMessage(attacker, ChatColor.GREEN + "You killed "
						+ player.getName());
			}
		}
	}

	@SuppressWarnings("unused")
	private static class Stats {

		private static HashMap<PlayerRep, Stats> stats = new HashMap<>();

		public static Stats get(PlayerRep rep) {
			Stats stat = stats.get(rep);
			if (stat == null)
				return new Stats(rep);
			else
				return stat;
		}

		public final PlayerRep rep;
		public int deaths = 0;
		public int kills = 0;
		public int hitTaken = 0;
		public int hitGiven = 0;
		public int suicides = 0;
		public int hadicapLevel = 0;

		public Stats(PlayerRep rep) {
			this.rep = rep;
		}
	}

	public void setHandicap(PlayerRep rep, int handicap) {
		Stats.get(rep).hadicapLevel = handicap;
	}
}
