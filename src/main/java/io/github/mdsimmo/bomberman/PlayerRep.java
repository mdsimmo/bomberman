package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.Game.Stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

/**
 * This is a class that holds all the extra data needed for a Bomberman player <br>
 * When initialised, the player will automatically be made to join the game
 */
public class PlayerRep implements Listener {

	private static JavaPlugin plugin = Bomberman.instance;
	private static HashMap<Player, PlayerRep> lookup = new HashMap<>();
	
	public static PlayerRep getPlayerRep(Player player) {
		PlayerRep rep = lookup.get(player);
		if (rep == null)
			return new PlayerRep(player);
		else
			return rep;
	}
	
	public static List<PlayerRep> allPlayers() {
		return new ArrayList<>(lookup.values());
	}
	
	public final Player player;
	private ItemStack[] spawnInventory;
	private Location spawn;
	private int spawnHunger;
	private Game game;
	private Game gamePlaying = null;
	private int immunity = 0;
	private Game editGame = null;
	private LinkedHashMap<Block, BlockRep> changes;
	
	public PlayerRep(Player player) {
		this.player = player;
		lookup.put(player, this);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	/**
	 * Gets the game that the player changed last
	 * @return the active game
	 */
	public Game getGameActive() {
		return game;
	}
	
	/**
	 * Sets the game that the player last did something to
	 * @param game the nre active game
	 */
	public void setGameActive(Game game) {
		this.game = game;
		if (game != null && !game.observers.contains(this))
			game.observers.add(this);
	}
	
	/**
	 * @return The game currently being played. If not playing, then null.
	 */
	public Game getGamePlaying() {
		return gamePlaying;
	}
	
	/**
	 * @return the Player this PlayerRep represents
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Join the active game
	 * @return true if joined successfully
	 */
	public boolean joinGame() {
		if (game == null) {
			Bomberman.sendMessage(this, "You must specify a game to join");
			return false;
		}
		if (gamePlaying != null) {
			Bomberman.sendMessage(this, "You're already part of %g", gamePlaying);
			return false;
		}
		this.spawn = player.getLocation();
		Vector gameSpawn = game.findSpareSpawn();
		if (gameSpawn == null) {
			Bomberman.sendMessage(this, "Game %g is full.", game);
			return false;
		}
		if (game.getFare() != null) {
			if (player.getInventory().containsAtLeast(game.getFare(), game.getFare().getAmount())
					|| player.getGameMode() == GameMode.CREATIVE)
				player.getInventory().removeItem(game.getFare());
			else {
				Bomberman.sendMessage(this, "You need %i to join", game.getFare());
				return false;
			}
		}
		Bomberman.sendMessage(game.observers, "%p joined game %g", this, game);
		player.teleport(game.box.corner().add(gameSpawn));
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(game.getLives());
		player.setMaxHealth(game.getLives());
		player.setHealthScale(game.getLives() * 2);
		player.setExhaustion(0);
		spawnHunger = player.getFoodLevel();
		player.setFoodLevel(10000); // just a big number
		removeEffects();
		spawnInventory = player.getInventory().getContents();
		game.initialise(this); 
		game.addPlayer(this);
		gamePlaying = game;
		return true;
	}

	/**
	 * Kills the player and notifies the joined game
	 * @return true if killed successfully
	 */
	public boolean kill() {
		if (gamePlaying == null)
			return false;
		player.getInventory().setContents(spawnInventory);
		gamePlaying.alertRemoval(this);
		gamePlaying = null;
		player.setMaxHealth(20);
		player.setHealth(20);
		player.setHealthScale(20);
		player.setFoodLevel(spawnHunger);
		player.teleport(spawn);
		removeEffects();
		return true;
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if (e.isCancelled())
			return;
		if (e.getPlayer() == player) {
			Block b = e.getBlock();
			// create a bomb when placing tnt
			if (gamePlaying != null) {
				if (b.getType() == gamePlaying.getBombMaterial() && gamePlaying.isPlaying) {
					new Bomb(gamePlaying, this, e.getBlock());
					return;
				}
			}
			// track edit mode changes
			if (editGame != null) {
				if (editGame.box.contains(e.getBlock().getLocation()))
					changes.put(e.getBlock(), BlockRep.createBlock(e.getBlockReplacedState()));
				else {
					e.setCancelled(true);
					Bomberman.sendMessage(this, "Cannot build outside while in editmode");
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getPlayer() == player && editGame != null) {
			Block b = e.getBlock();
			if (editGame.box.contains(b.getLocation()))
				changes.put(e.getBlock(), BlockRep.createBlock(e.getBlock()));
			else {
				e.setCancelled(true);
				Bomberman.sendMessage(this, "Cannot destroy blocks outside while in editmode");
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (p == this.player && gamePlaying != null && !gamePlaying.isPlaying) {
			// stop the player from moving
			Location from = e.getFrom();
			double xfrom = e.getFrom().getX();
			double yfrom = e.getFrom().getY();
			double zfrom = e.getFrom().getZ();
			double xto = e.getTo().getX();
			double yto = e.getTo().getY();
			double zto = e.getTo().getZ();
			if (!(xfrom == xto && yfrom == yto && zfrom == zto)) {
				p.teleport(from);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (player == e.getPlayer() && gamePlaying != null && !gamePlaying.isPlaying)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerTelepot(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		if (p == this.player && gamePlaying != null  && !gamePlaying.box.contains(e.getTo())) {
			e.setCancelled(true);
			Bomberman.sendMessage(p, ChatColor.RED + "Cannot teleport while part of a game");
		}
	}

	public int bombStrength() {
		int strength = 0;
		if (gamePlaying == null)
			return 0;
		for (ItemStack stack : player.getInventory().getContents()) {
			if (stack != null && stack.getType() == gamePlaying.getPowerMaterial()) {
				strength += stack.getAmount();
			}
		}
		return Math.max(strength, 1);
	}

	public void damage(PlayerRep attacker) {
		boolean dead = false;
		if (immunity > 0)
			return;
		if (player.getHealth() > 1)
			player.damage(1);
		else
			dead = true;
		new Immunity();

		Stats playerStats = gamePlaying.getStats(this);
		Stats attackerStats = gamePlaying.getStats(attacker);

		attackerStats.hitsGiven++;
		playerStats.hitsTaken++;

		if (!dead) {
			if (attacker == this) {
				Bomberman.sendMessage(player, "You hit yourself!");
			} else {
				Bomberman.sendMessage(player,
						"You were hit by %p", attacker);
				Bomberman.sendMessage(attacker, "You hit %p", player);
			}
		} else {
			playerStats.deaths++;
			attackerStats.kills++;
			if (attacker == this) {
				Bomberman.sendMessage(player, ChatColor.RED
						+ "You killed yourself!");
				playerStats.suicides++;
			} else {
				Bomberman.sendMessage(player, ChatColor.RED + "Killed by %p", attacker);
				Bomberman.sendMessage(attacker, ChatColor.YELLOW + "You killed %p", player);
			}
		}
		
		if (dead)
			kill();
	}

	private class Immunity implements Runnable {

		public Immunity() {
			immunity++;
			plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, this, 20);
		}

		@Override
		public void run() {
			immunity--;
		}
	}

	@EventHandler
	public void onPlayerRegen(EntityRegainHealthEvent e) {
		if (e.getEntity() == player && gamePlaying != null) {
			if (e.getRegainReason() == RegainReason.MAGIC)
				if (gamePlaying.isSuddenDeath())
					Bomberman.sendMessage(this, "No regen in sudden death!");
				else
					player.setHealth(Math.min(player.getHealth() + 1,
							player.getMaxHealth()));
					
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		if (e.getPlayer() == player) {
			kill();
		}
	}
	
	
	// an attempt at making potion effects shorter
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDrinkPotion(PlayerItemConsumeEvent e) {
		if (e.getPlayer() == player && gamePlaying != null) {
			if (e.getItem().getType() == Material.POTION) {
				Potion potion = Potion.fromItemStack(e.getItem());
				if (potion.getType() == PotionType.INSTANT_HEAL
						|| potion.getType() == PotionType.INSTANT_DAMAGE) {
					// instant potions don't need the duration changed
				} else {
					e.setCancelled(true);
					// hack to fix bug that creates a ghost bottle
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							player.updateInventory();
						}
					});
					
					player.addPotionEffect(new PotionEffect(potion.getType().getEffectType(), 20*game.getPotionDuration(), 1), true);
				}
				
				// remove the bottle
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						player.setItemInHand(null);
					}
				});
			}
		}
	}

	public boolean startEditMode() {
		if (gamePlaying != null || editGame != null || game == null)
			return false;
		else {
			editGame = game;
			if (changes == null)
				changes = new LinkedHashMap<>();
			else
				changes.clear();
			return true;
		}
	}
	
	public boolean saveChanges() {
		if (editGame == null)
			return false;
		for (Block b : changes.keySet()) {
			Vector v = b.getLocation().subtract(editGame.box.x, editGame.box.y, editGame.box.z).toVector();
			if (editGame.box.contains(b.getLocation()))
				editGame.board.addBlock(BlockRep.createBlock(b), v);
		}
		BoardGenerator.saveBoard(editGame.board);
		editGame = null;
		return true;
	}
	
	public boolean discardChanges(boolean remove) {
		if (editGame == null)
			return false;
		if (remove) { 
			for (Map.Entry<Block, BlockRep> entry : changes.entrySet()) {
				Block current = entry.getKey();
				BlockRep previous = entry.getValue();
				previous.setBlock(current);
			}
		}
		editGame = null;
		return true;
	}
	
	public boolean isPlaying() {
		return gamePlaying != null;
	}
	
	public String getName() {
		return player.getName();
	}
	
	public boolean isEditting() {
		return editGame != null;
	}
	
	public Game getEditting() {
		return editGame;
	}
	private void removeEffects() {
		if (plugin.isEnabled())
			plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							player.setFireTicks(0);
							for (PotionEffect effect : player.getActivePotionEffects()) {
							    plugin.getLogger().info(effect.getType().toString());
								player.removePotionEffect(effect.getType());
							}
						}
					});
	}
}
