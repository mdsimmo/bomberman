package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.Command.Permission;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;

/**
 * Protects the game from any damage except that which is part of the game <br>
 */
public class GameProtection implements Listener {

	private Game game;
	private Plugin plugin = Bomberman.instance;

	public GameProtection(Game game) {
		this.game = game;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		PlayerRep rep = game.getPlayerRep(e.getPlayer());
		if (rep != null && rep.isPlaying) {
			if (game.containsLocation(e.getBlock().getLocation())) {
				e.setCancelled(true);
				return;
			}
		}
		if (game.getProtected(Config.PROTECT_DESTROYING)
				&& game.containsLocation(e.getBlock().getLocation())
				&& !Permission.PROTECTION_VOID.isAllowedBy(e.getPlayer()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.TNT) {
			PlayerRep rep = game.getPlayerRep(e.getPlayer());
			if (rep != null && rep.isPlaying) {
				if (!game.isPlaying)
					e.setCancelled(true);
				return;
			}
		}
		if (game.getProtected(Config.PROTECT_PLACING)
				&& game.containsLocation(e.getBlock().getLocation())
				&& !Permission.PROTECTION_VOID.isAllowedBy(e.getPlayer()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockCobust(BlockBurnEvent e) {
		if (game.containsLocation(e.getBlock().getLocation())) {
			if (game.isPlaying)
				e.setCancelled(true);
			if (game.getProtected(Config.PROTECT_FIRE)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		if (game.containsLocation(e.getBlock().getLocation())) {
			if (game.isPlaying)
				e.setCancelled(true);
			if (game.getProtected(Config.PROTECT_FIRE)) {
				if (e.getPlayer() != null
						&& !Permission.PROTECTION_VOID.isAllowedBy(e.getPlayer()))
					e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		if (entity instanceof Player) {
			Player player = (Player)entity;
			PlayerRep rep = game.getPlayerRep(player);
			if (rep != null && rep.isPlaying) {
				player.setFireTicks(0);
				e.setCancelled(true);
				return;
			}
			if (game.getProtected(Config.PROTECT_DAMAGE)) {
				if (game.containsLocation(e.getEntity().getLocation())) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	public void onPVP(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player player = (Player) e.getDamager();
			if (game.getProtected(Config.PROTECT_DAMAGE)
					&& game.containsLocation(e.getDamager().getLocation())
					&& Permission.PROTECTION_VOID.isAllowedBy(player))
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent e) {
		if (game.getProtected(Config.PROTECT_EXPLOSIONS)) {
			if (game.containsLocation(e.getEntity().getLocation())) {
				e.setCancelled(true);
			}
		}
	}
}
