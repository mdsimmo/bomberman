package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.Bomb.DeathBlock;

import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

/**
 * This is a class that holds all the extra data needed for a bomberman player <br>
 * When initialised, the player will automatically be made to join the game
 */
public class PlayerRep implements Listener {

	private static JavaPlugin plugin = Bomberman.instance;
	public Player player;
	public ItemStack[] spawnInventory;
	public Location spawn;
	public Game game;
	public boolean isPlaying = false;
	public int immunity = 0;
	public long deathTime = -1;
	public int kills = 0;
	
	public PlayerRep(Player player, Game game) {
		this.player = player;
		this.game = game;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		game.observers.add(this);
	}

	public void joinGame() {
		this.spawn = player.getLocation();
		Vector gameSpawn = game.findSpareSpawn();
		if (gameSpawn == null) {
			player.sendMessage("game full");
			return;
		} else {
			if (game.getFare() != null) {
				if (player.getInventory().contains(game.getFare().getType(), game.getFare().getAmount())
						|| player.getGameMode() == GameMode.CREATIVE)
					player.getInventory().removeItem(game.getFare());
				else {
					player.sendMessage("You need at least " + game.getFare().getAmount() + " " + game.getFare().getType().toString().toLowerCase());
					return;
				}
			}
			for (PlayerRep rep : game.observers)
				rep.player.sendMessage(player.getName() + " joined");
			player.teleport(game.loc.clone().add(gameSpawn));
		}
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(game.getLives());
		player.setMaxHealth(game.getLives());
		player.setHealthScale(game.getLives() * 2);
		spawnInventory = player.getInventory().getContents();
		player.getInventory().setContents(
				new ItemStack[] { new ItemStack(Material.TNT, game.getBombs()),
						new ItemStack(Material.BLAZE_POWDER, game.getPower()) });

		isPlaying = true;
		game.addPlayer(this);
	}

	/**
	 * Removes the player from the game and restores the player to how they were
	 * before joining
	 */
	public void kill(boolean alert) {
		if (isPlaying) {
			deathTime = Calendar.getInstance().getTimeInMillis();
			isPlaying = false;
			game.players.remove(this);
			player.getInventory().setContents(spawnInventory);
			player.setMaxHealth(20);
			player.setHealth(20);
			player.setHealthScale(20);
			player.teleport(spawn);
			// needed to prevent crash when reloading
			if (plugin.isEnabled())
				plugin.getServer().getScheduler()
						.scheduleSyncDelayedTask(plugin, new Runnable() {

							@Override
							public void run() {
								player.setFireTicks(0);
							}
						});
			if (alert)
				game.alertRemoval(this);
			
			if (game.players.size() <= 1 && game.getCountdownTimer() != null) {
			    game.getCountdownTimer().destroy();
			    for (PlayerRep p : game.players) {
			        p.player.sendMessage(ChatColor.GREEN + "[BomberMan] " + ChatColor.WHITE + "Not enough players remaining. The countdown timer has been stopped.");
			    }
			}
		}
	}
	
	/**
	 * compleatly destroys this object.
	 */
	public void destroy() {
		kill(false);
		game.players.remove(this);
		game.observers.remove(this);
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onPlayerPlaceTNT(BlockPlaceEvent e) {
		if (e.isCancelled())
			return;
		Block b = e.getBlock();
		if (e.getPlayer() == player && isPlaying) {
			if (b.getType() == Material.TNT && game.isPlaying) {
				new Bomb(game, this, e.getBlock());
			}
		}
	}

	@EventHandler
	public void playerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (p == this.player && isPlaying && !game.isPlaying) {
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

	public int bombStrength() {
		int strength = 0;
		for (ItemStack stack : player.getInventory().getContents()) {
			if (stack != null && stack.getType() == Material.BLAZE_POWDER) {
				strength += stack.getAmount();
			}
		}
		return strength;
	}

	public void damage(DeathBlock db) {
		if (immunity <= 0) {
			db.cause.kills++;
			if (player.getHealth() > 1) {
				player.damage(1);
				Player cause = db.cause.player;
				if (cause == player)
					player.sendMessage("You hit yourself!");
				else {
					player.sendMessage("Hit by " + db.cause.player.getName());
					cause.sendMessage("You hit " + player.getName());
				}
				new Immunity();
			} else {
				Player cause = db.cause.player;
				if (cause == player)
					player.sendMessage(ChatColor.RED + "You killed yourself!");
				else {
					player.sendMessage(ChatColor.RED + "Killed by " + cause.getName());
					cause.sendMessage(ChatColor.GREEN + "You killed " + player.getName());
				}
				kill(true);
			}
		}
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
		if (e.getEntity() == player && isPlaying) {
			if (e.getRegainReason() == RegainReason.MAGIC)
				if (game.isSuddenDeath())
					player.sendMessage("No regen in sudden death!");
				else
					player.setHealth(Math.min(player.getHealth() + 1,
							player.getMaxHealth()));
					
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		if (e.getPlayer() == player) {
			kill(true);
		}
	}
	
	
	// an attempt at making potion effects shorter
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDrinkPotion(PlayerItemConsumeEvent e) {
		if (e.getPlayer() == player && isPlaying) {
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
					
					player.addPotionEffect(new PotionEffect(potion.getType().getEffectType(), 200, 1), true);
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
}
