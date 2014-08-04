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
			for (PlayerRep rep : game.players)
				rep.player.sendMessage(ChatColor.YELLOW + "Game over in 30 seconds!");
		if (timeout == 10)
			for (PlayerRep rep : game.observers)
				rep.player.sendMessage(ChatColor.RED + "Game over in 10 seconds!");
		else if (timeout < 10 && timeout > 0)
			for (PlayerRep rep : game.players)
				rep.player.sendMessage(" " + timeout);
		else if (timeout == 0) {
			for (PlayerRep rep : game.observers)
				rep.player.sendMessage(ChatColor.RED + "Game over!");
			game.terminate();
		}
		
		if (suddenDeath == 30)
			for (PlayerRep rep : game.players)
				rep.player.sendMessage(ChatColor.YELLOW + "Sudden death in 30 seconds!");
		if (suddenDeath == 10)
			for (PlayerRep rep : game.observers)
				rep.player.sendMessage(ChatColor.RED + "Sudden death in 10 seconds!");
		else if (suddenDeath < 10 && suddenDeath > 0)
			for (PlayerRep rep : game.players)
				rep.player.sendMessage("" + suddenDeath);
		else if (suddenDeath == 0) {
			for (PlayerRep rep : game.observers)
				rep.player.sendMessage(ChatColor.RED + "Sudden death!");
			game.setSuddenDeath(true);
		}
	}
}
