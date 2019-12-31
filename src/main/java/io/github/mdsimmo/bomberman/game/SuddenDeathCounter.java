package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.game.Game.State;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;

public class SuddenDeathCounter {

	private static Plugin plugin = Bomberman.instance;
	private Game game;
	private int sdID, toID;
	private SuddenDeath sd = new SuddenDeath();
	private Timeout to = new Timeout();

	public SuddenDeathCounter(Game game) {
		if (game == null)
			throw new NullPointerException("Game cannot be null");
		this.game = game;
	}
	
	public void start() {
		sd.start();
		to.start();
	}

	public class SuddenDeath implements Runnable {
		public int suddenDeath;
			
		public void start() {
			if (game.getSuddenDeath() >= 0) {
				suddenDeath = game.getSuddenDeath();
				sdID = plugin.getServer().getScheduler()
						.scheduleSyncRepeatingTask(plugin, this, 0, 20);
			}
		}
		
		@Override
		public void run() {
			if ( game.state != State.PLAYING )
				plugin.getServer().getScheduler().cancelTask(sdID);
			
			suddenDeath--;
			if (suddenDeath == 30
					|| suddenDeath == 10
					|| (suddenDeath <= 5 && suddenDeath > 0)) {
				Map<String, Object> values = new HashMap<>();
				values.put( "time", suddenDeath );
				game.messagePlayers(
						Text.SUDDENDEATH_COUNT_P,
						Text.SUDDENDEATH_COUNT_O,
						Text.SUDDENDEATH_COUNT_A, values );
			} else if (suddenDeath == 0) {
				game.messagePlayers(
						Text.SUDDENDEATH_P,
						Text.SUDDENDEATH_O,
						Text.SUDDENDEATH_A, null );
				game.setSuddenDeath(true);
				plugin.getServer().getScheduler().cancelTask(sdID);
			}
		}
		
	}
	
	public class Timeout implements Runnable {
		public int timeout;
		
		public void start() {
			if (game.getTimeout() >= 0) {
				timeout = game.getTimeout();
				toID = plugin.getServer().getScheduler()
						.scheduleSyncRepeatingTask(plugin, to, 0, 20);
			}
		}
		
		@Override
		public void run() {
			if ( game.state != State.PLAYING )
				plugin.getServer().getScheduler().cancelTask(toID);
			
			timeout--;
			
			if (timeout == 30
					|| timeout == 10
					|| (timeout <= 5 && timeout > 0)) {
				Map<String, Object> values = new HashMap<>();
				values.put( "time", timeout );
				game.messagePlayers(
						Text.TIMEOUT_COUNT, values);
			} else if (timeout == 0) {
				game.messagePlayers(Text.TIMEOUT,null);
				game.stop();
				plugin.getServer().getScheduler().cancelTask(toID);
			}
		}
	}
}
