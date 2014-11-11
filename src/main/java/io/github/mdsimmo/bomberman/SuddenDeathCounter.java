package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.messaging.Text;

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
	}
	
	public void start() {
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
			
			if (suddenDeath == 30
					|| suddenDeath == 10
					|| (suddenDeath <= 5 && suddenDeath > 0))
				game.sendMessages(
						Text.SUDDENDEATH_COUNT_P,
						Text.SUDDENDEATH_COUNT_O,
						Text.SUDDENDEATH_COUNT_A,
						game, suddenDeath);
			else if (suddenDeath == 0) {
				game.sendMessages(
						Text.SUDDENDEATH_P,
						Text.SUDDENDEATH_O,
						Text.SUDDENDEATH_A,
						game, suddenDeath);
				game.setSuddenDeath(true);
				plugin.getServer().getScheduler().cancelTask(sdID);
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
			
			if (timeout == 30
					|| timeout == 10
					|| (timeout <= 5 && timeout > 0))
				game.sendMessages(
						Text.TIMEOUT_COUNT_P,
						Text.TIMEOUT_COUNT_O,
						Text.TIMEOUT_COUNT_A,
						game, timeout);
			else if (timeout == 0) {
				game.sendMessages(
						Text.TIMEOUT_P,
						Text.TIMEOUT_O,
						Text.TIMEOUT_A,
						game, timeout);
				game.setSuddenDeath(true);
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
