package io.github.mdsimmo.bomberman;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class BoardBuilder implements Runnable {
	private static Plugin plugin = Bomberman.instance;
	private Board board;
	private Location location;
	private int ticks = 0, count = 0;
	private int id;
	
		
	public BoardBuilder(Board board, Location location) {
		this.board = board;
		this.location = location;
		id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
	}
	
	@Override
	public void run() {
		ticks++;
		while (true) {
			if (count > ticks * (int)Config.BUILD_RATE.getValue())
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
