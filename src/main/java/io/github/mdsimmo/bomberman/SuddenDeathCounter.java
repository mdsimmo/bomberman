package io.github.mdsimmo.bomberman;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class SuddenDeathCounter {

	private static Plugin plugin = Bomberman.instance;
	private Game game;
	int sdID, toID;
	SuddenDeath sd = new SuddenDeath();
	Timeout to = new Timeout();

	public SuddenDeathCounter(Game game) {
		if (game == null)
			throw new NullPointerException("Game cannot be null");
		this.game = game;
		sd.start();
		to.start();
	}

	private class SuddenDeath implements Runnable {
		int suddenDeath;
			
		public void start() {
			if (game.getSuddenDeath() >= 0) {
				suddenDeath = game.getSuddenDeath();
				sdID = plugin.getServer().getScheduler()
						.scheduleSyncRepeatingTask(plugin, this, 0, 20);
			}
		}
		
		@Override
		public void run() {
			if (!game.isPlaying)
				plugin.getServer().getScheduler().cancelTask(sdID);
			
			suddenDeath--;
			
			if (suddenDeath == 30)
				Bomberman.sendMessage(game.players,
						"Sudden death in %d seconds!", suddenDeath);
			if (suddenDeath <= 10) {
				if (suddenDeath == 10)
					Bomberman.sendMessage(game.observers,
							"Sudden death in %d seconds!", suddenDeath);
				else if (0 < suddenDeath && suddenDeath <= 5) {
					Bomberman.sendMessage(game.players, "%d", suddenDeath);
				} else if (suddenDeath == 0) {
					Bomberman.sendMessage(game.observers,
							ChatColor.RED + "Sudden death!");
					game.setSuddenDeath(true);
					plugin.getServer().getScheduler().cancelTask(sdID);
				}
			}
		}
		
	}
	
	private class Timeout implements Runnable {
		int timeout;
		
		public void start() {
			if (game.getTimeout() >= 0) {
				timeout = game.getTimeout();
				toID = plugin.getServer().getScheduler()
						.scheduleSyncRepeatingTask(plugin, to, 0, 20);
			}
		}
		
		@Override
		public void run() {
			if (!game.isPlaying)
				plugin.getServer().getScheduler().cancelTask(toID);
			
			timeout--;
			
			if (timeout == 30)
				Bomberman.sendMessage(game.players,
						"Game over in %d seconds!",	timeout);
			if (timeout == 10)
				Bomberman.sendMessage(game.observers,
						"Game over in %d seconds!",	timeout);
			else if (0 < timeout && timeout <= 5)
				Bomberman.sendMessage(game.players, "%d", timeout);
			else if (timeout == 0) {
				Bomberman.sendMessage(game.observers, ChatColor.RED + "Game timed out!");
				game.stop();
				BoardGenerator.switchBoard(game.board, game.board, game.box);
				plugin.getServer().getScheduler().cancelTask(toID);
			}
		}
	}
	
	public int getSuddenDeath() {
		return sd.suddenDeath;
	}
	
	public int getTimeOut() {
		return to.timeout;
	}
}
