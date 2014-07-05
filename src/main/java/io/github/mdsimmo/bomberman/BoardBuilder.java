package io.github.mdsimmo.bomberman;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class BoardBuilder implements Runnable {
	private static Plugin plugin = Bomberman.instance;
	private Board board;
	private Location location;
	private int ticks = 0;
	private int count = 0;
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
			if (count > ticks*500)
				return;
			Location l = location.clone().add(count/(board.ySize*board.zSize), (count/board.zSize)%board.ySize, count%board.zSize);
			board.blocks.get(count).setBlock(l.getBlock());
			count++;
			if (count >= board.blocks.size()) {
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
