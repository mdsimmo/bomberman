package io.github.mdsimmo.bomberman;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class SuddenDeathCounter implements Runnable {

	private static Plugin plugin = Bomberman.instance;
	private Game game;
	int timeout;
	int suddenDeath;
	int taskId;
	
	public SuddenDeathCounter(Game game) {
		this.game = game;
		taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20);
		timeout = game.getTimeout();
		suddenDeath = game.getSuddenDeath();
	}
	
	@Override
	public void run() {
		if (!game.isPlaying)
			plugin.getServer().getScheduler().cancelTask(taskId);
			
		timeout--;
		suddenDeath--;
		if (timeout == 30)
		    Bomberman.sendMessage(game.players, "Game over in "
                    + ChatColor.YELLOW + timeout + ChatColor.WHITE
                    + " seconds!");
		if (timeout == 10)
		    Bomberman.sendMessage(game.observers, "Game over in "
                    + ChatColor.YELLOW + timeout + ChatColor.WHITE
                    + " seconds!");
		else if (timeout < 10 && timeout > 0)
		    Bomberman.sendMessage(game.players, "" + timeout);
		else if (timeout == 0) {
		    Bomberman.sendMessage(game.observers, "Game over!");
			game.terminate();
		}
		
		if (suddenDeath == 30)
		    Bomberman.sendMessage(game.players, "Sudden death in "
                    + ChatColor.YELLOW + suddenDeath + ChatColor.WHITE
                    + " seconds!");
		
		if (suddenDeath <= 10) {
		    if (suddenDeath == 10)
		        Bomberman.sendMessage(game.observers, "Sudden death in " + ChatColor.YELLOW
                        + suddenDeath + ChatColor.WHITE + " seconds!");
		    else if (suddenDeath > 0) {
		        Bomberman.sendMessage(game.players, "" + suddenDeath);
		    } else {
		        Bomberman.sendMessage(game.observers, ChatColor.RED + "Sudden death!");
	            game.setSuddenDeath(true);
	            plugin.getServer().getScheduler().cancelTask(taskId);
		    }
		}
	}
}
