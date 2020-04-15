package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.events.BmGameTerminatedIntent;
import io.github.mdsimmo.bomberman.utils.Box;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.plugin.Plugin;

/**
 * Protects an arena from getting damaged from the game.
 *
 * It is up to Server Owners to protect the arena from griefers
 */
public class GameProtection implements Listener {

	private static final Plugin plugin = Bomberman.instance;

    public static void protect(Game game, Box bounds) {
        GameProtection protection = new GameProtection(game, bounds);
        Bukkit.getPluginManager().registerEvents(protection, plugin);
    }

    private final Game game;
	private final Box bounds;

    private GameProtection(Game game, Box bounds) {
		this.game = game;
		this.bounds = bounds;
	}

    @EventHandler
	public void onGameTerminate(BmGameTerminatedIntent e) {
    	if (e.getGame() == game) {
			HandlerList.unregisterAll(this);
		}
	}

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBurn(BlockBurnEvent e) {
        if (bounds.contains(e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockIgnite(BlockIgniteEvent e) {
        if (e.getCause() == BlockIgniteEvent.IgniteCause.SPREAD
                && bounds.contains(e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onFireSpread(BlockSpreadEvent e) {
        if (bounds.contains(e.getBlock().getLocation()))
            e.setCancelled(true);
    }
}
