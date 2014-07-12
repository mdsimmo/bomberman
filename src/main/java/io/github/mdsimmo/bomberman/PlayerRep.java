package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.Bomb.DeathBlock;

import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
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
			for (PlayerRep rep : game.observers)
				rep.player.sendMessage(player.getName() + " joined");
			// player.teleport() might seem better, but the players are teleported ~+-0.5 blocks out (and end up in the walls)
			plugin.getServer().dispatchCommand(player, "tp " + (gameSpawn.getBlockX()+game.loc.getBlockX()) + " " + (double)(gameSpawn.getBlockY()+game.loc.getBlockY()) + " " + (gameSpawn.getBlockZ()+game.loc.getBlockZ()));
		}

		player.setHealth(game.lives);
		player.setMaxHealth(game.lives);
		player.setHealthScale(game.lives * 2);
		spawnInventory = player.getInventory().getContents();
		player.getInventory().setContents(
				new ItemStack[] { new ItemStack(Material.TNT, game.bombs),
						new ItemStack(Material.BLAZE_POWDER, game.power) });

		isPlaying = true;
		game.players.add(this);
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
	/*@EventHandler
	public void onPlayerDrinkPotion(PlayerItemConsumeEvent e) {
		if (e.getPlayer() == player && isPlaying) {
			if (e.getItem().getType() == Material.POTION) {
				//PotionMeta meta = (PotionMeta)e.getItem().getItemMeta();
				Potion potion = Potion.fromItemStack(e.getItem());
				Collection<PotionEffect> effects = potion.getEffects();
				plugin.getLogger().info("" + effects);
				effects.clear();
				//effects.add(new PotionEffect(potion.getType().getEffectType(), 60, 1));
				//plugin.getLogger().info("" + effects);
				
				
				
				// remove the empty bottle
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						player.setItemInHand(null);
					}
				});
			}
		}
	}*/
}
