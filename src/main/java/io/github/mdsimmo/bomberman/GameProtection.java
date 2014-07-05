package io.github.mdsimmo.bomberman;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
	public void onExplosion(EntityExplodeEvent e) {
		// protect the blocks
		if (!game.isPlaying)
			return;
		List<Block> blockListCopy = new ArrayList<Block>();
        blockListCopy.addAll(e.blockList());
        for (Block block : blockListCopy) {
            if (game.containsLocation(block.getLocation())) 
            	e.blockList().remove(block);
        }	
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		PlayerRep rep = game.getPlayerRep(e.getPlayer());
		if (rep != null && rep.isPlaying) {
			if (game.containsLocation(e.getBlock().getLocation())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockCobust(BlockIgniteEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		if (entity instanceof Player) {
			PlayerRep rep = game.getPlayerRep((Player)entity);
			if (rep != null && rep.isPlaying) {
				e.getEntity().setFireTicks(0);
				e.setCancelled(true);
				if (e.getCause() == DamageCause.FIRE)
					rep.damage();
			}
		}
	}
}
