package io.github.mdsimmo.bomberman.arenabuilder;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Config;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class ArenaBuilder implements Runnable {
	private static Plugin plugin = Bomberman.instance;
	private static int buildRate = Config.BUILD_RATE.getValue();
	private Board board;
	private Location location;
	private int ticks = 0, count = 0;
	private int id;
	
		
	public ArenaBuilder(Board board, Location location) {
		this.board = board;
		this.location = location;
		id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
	}
	
	@Override
	public void run() {
		ticks++;
		while (true) {
			if (buildRate > 0 && count > ticks * buildRate)
				return;
			Location l = location.clone().add(board.countToVector(count));
			board.getBlock(count).setBlock(l.getBlock());
			count++;
			if (count >= board.xSize*board.ySize*board.zSize) {
				// finishing touches
				for (Vector v : board.delayed.keySet()) {
					l = location.clone().add(v);
					board.delayed.get(v).setBlock(l.getBlock());
				}
				plugin.getServer().getScheduler().cancelTask(id);
				return;
			}				
		}	
	}
}
