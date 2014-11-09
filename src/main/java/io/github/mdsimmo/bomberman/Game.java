package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.Bomb.DeathBlock;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.save.GameSaver;
import io.github.mdsimmo.bomberman.utils.Box;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Game {

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
				sendMessages(
						Text.GAME_STARTING_PLAYERS,
						Text.GAME_STARTING_OBSERVERS,
						Text.GAME_STARTING_ALL,
						Game.this, count);
			}

			if (count > 0) {
				// Keep the timer running until it ends
				taskId = plugin.getServer().getScheduler()
						.scheduleSyncDelayedTask(plugin, this, 20);

				// Notify waiting players every 15 seconds until count <= 15,
				// notify every 5 until count <= 3,
				// notify every second last 3 seconds
				if (count % 15 == 0 || (count < 15 && count % 5 == 0)
						|| count <= 3) {
					sendMessages(
							Text.GAME_COUNT_PLAYERS,
							Text.GAME_COUNT_OBSERVERS,
							Text.GAME_COUNT_ALL,
							Game.this, count);
				}
			} else {
				sendMessages(
						Text.GAME_STARTED_PLAYERS,
						Text.GAME_STARTED_OBSERVERS,
						Text.GAME_STARTED_ALL,
						Game.this, count);
							
				isPlaying = true;
				deathCounter = new SuddenDeathCounter(Game.this);
				deathCounter.start();
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
			GameSaver.loadGame(f);
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
	private Material bombMaterial;
	private int bombs;
	private GameStarter countdownTimer = null;
	public List<DeathBlock> deathBlocks = new ArrayList<>();
	private double dropChance;
	private List<ItemStack> drops;
	public Map<Block, Bomb> explosions = new HashMap<>();
	private ItemStack fare;
	public boolean isPlaying;
	private int lives;
	public Box box;
	private int minPlayers;
	public String name;
	public ArrayList<PlayerRep> observers = new ArrayList<>();
	public Board oldBoard;
	public ArrayList<PlayerRep> players = new ArrayList<>();
	private boolean pot;
	private int power;
	private Material powerMaterial;
	private ItemStack prize;
	private boolean protection;
	private boolean protectFire;
	private boolean protectPlace;
	private boolean protectBreak;
	private boolean protectExplosion;
	private boolean protectDamage;
	private boolean protectPVP;
	private GameProtection protector;
	private int potionDuration;
	private GameSaver save;
	private int suddenDeath;
	private SuddenDeathCounter deathCounter;
	private boolean suddenDeathStarted = false;
	private int timeout;
	private List<ItemStack> initialitems;
	private ArrayList<PlayerRep> winners = new ArrayList<>();

	public Game(String name, Box box) {
		this.name = name;
		this.box = box;
		initVars();
		protector = new GameProtection(this);
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
		winners.remove(rep);
		winners.add(0, rep);
	}

	/**
	 * call when a player dies
	 */
	public void alertRemoval(PlayerRep rep) {
		players.remove(rep);
		if (isPlaying) {
			addWinner(rep);
			if (!checkFinish())
				sendMessages(Text.PLAYER_KILLED_PLAYERS, Text.PLAYER_KILLED_OBSERVERS, Text.PLAYER_KILLED_ALL, this, rep);
		} else {
			sendMessages(Text.PLAYER_LEFT_PLAYERS, Text.PLAYER_LEFT_OBSERVERS, Text.PLAYER_LEFT_ALL, this, rep);
		}
		if (players.size() < minPlayers && getCountdownTimer() != null) {
			getCountdownTimer().destroy();
			sendMessages(Text.COUNT_STOPPED_PLAYERS, Text.COUNT_STOPPED_OBSERVERS, Text.COUNT_STOPPED_ALL, Game.this);
		}
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
			Block block = box.corner().add(v).getBlock();
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
			sendMessages(Text.GAME_OVER_PLAYERS, Text.GAME_OVER_OBSERVERS, Text.GAME_COUNT_ALL, this);
			winnersDisplay();

			// reset the game
			BoardGenerator.switchBoard(this.board, this.board, box);
			stop();

			return true;
		}
		return !isPlaying;
	}

	public void destroy() {
		gameRegistry.remove(name.toLowerCase());
		stop();
		BoardGenerator.switchBoard(board, oldBoard, box);
		HandlerList.unregisterAll(protector);
		File f = new File(plugin.getDataFolder(), name + ".game");
		BoardGenerator.remove(oldBoard.name);
		f.delete();
		f = new File(plugin.getDataFolder(), name + ".old.arena");
		f.delete();
		for (PlayerRep rep : PlayerRep.allPlayers()) {
			if (rep.getGameActive() == this)
				rep.setGameActive(null);
			if (rep.getEditting() == this)
				rep.discardChanges(false);
		}
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
	 * Initialises the players inventory for a game handling player's handicaps
	 * and things <br>
	 * make sure the player's inventory is cleared before calling this.
	 * 
	 * @param rep
	 *            the PlayerRep to initialise
	 */
	public void initialise(PlayerRep rep) {
		rep.player.getInventory().clear();
		Stats stat = getStats(rep);
		for (ItemStack stack : initialitems) {
			ItemStack s = stack.clone();
			s.setAmount(Math.max(s.getAmount() - stat.hadicapLevel, 1));
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
		GameSaver config = new GameSaver(this);
		this.save = config;
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
		potionDuration = Config.POTION_DURATION.getValue(config);
		bombMaterial = Material.getMaterial((String)Config.BOMB_MATERIAL.getValue(config));
		powerMaterial = Material.getMaterial((String)Config.POWER_MATERIAL.getValue(config));
	}

	public boolean isSuddenDeath() {
		return suddenDeathStarted;
	}

	// anounce scores
	private void winnersDisplay() {
		Chat.sendText(observers, Text.SCORE_ANNOUNCE, this);
		Map<Message, Message> map = new LinkedHashMap<Message, Message>();
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
			map.put(new Message(null, place), new Message(rep.player, "{0}"));
		}
		for (PlayerRep rep : observers) {
			Chat.sendMap(rep.player, map);
		}
		Chat.sendText(observers, Text.SCORE_SEE_SCORES, this);
	}
	
	public List<Message> scoreDisplay() {
		List<Message> list = new ArrayList<Message>(players.size());
		for (PlayerRep rep : players) {
			Stats stats = this.getStats(rep);
			list.add(Text.SCORE_DISPLAY.getMessage(rep.getLanguage(), rep.player, this, rep, (int)rep.player.getHealth(), stats.kills, stats.deaths, stats.hitsTaken, stats.hitsGiven, stats.suicides, stats.hadicapLevel));
		}
		return list;
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
			if (countdownTimer == null)
				countdownTimer = new GameStarter(delay);
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
		for (Stats stat : stats.values()) {
			stat.reset();
		}
		suddenDeathStarted = false;
	}

	private HashMap<PlayerRep, Stats> stats = new HashMap<>();

	public Stats getStats(PlayerRep rep) {
		Stats stat = stats.get(rep);
		if (stat == null)
			return new Stats(rep);
		else
			return stat;
	}
	
	public class Stats {

		public final PlayerRep rep;
		public int deaths = 0;
		public int kills = 0;
		public int hitsTaken = 0;
		public int hitsGiven = 0;
		public int suicides = 0;
		public int hadicapLevel = 0;

		public Stats(PlayerRep rep) {
			this.rep = rep;
			stats.put(rep, this);
		}
		
		public void reset() {
			deaths = kills = hitsGiven = hitsTaken = suicides = 0;
		}
	}

	public void setHandicap(PlayerRep rep, int handicap) {
		getStats(rep).hadicapLevel = handicap;
	}

	public void saveGame() {
		save.save();
	}
	
	public Material getBombMaterial() {
		return bombMaterial;
	}
	
	public Material getPowerMaterial() {
		return powerMaterial;
	}
	
	public int getPotionDuration() {
		return potionDuration;
	}
	
	public void sendMessages(Text pText, Text oText, Text aText, Object ... objs) {
		for (Player player : plugin.getServer().getOnlinePlayers()) { 
			PlayerRep rep = PlayerRep.getPlayerRepSoft(player);
			if (!observers.contains(rep) && !players.contains(rep))
				Chat.sendText(player, aText, objs);
		}
		for (PlayerRep rep : observers) {
			if (!players.contains(rep))
				Chat.sendText(rep, oText, objs);
		}
		Chat.sendText(players, pText, objs);
	}
}
