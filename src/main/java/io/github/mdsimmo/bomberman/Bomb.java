package io.github.mdsimmo.bomberman;

import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class Bomb implements Runnable {

		private Plugin plugin = Bomberman.instance;
		private PlayerRep rep;
		private Block tnt;
		private Game game;
		
		public Bomb(Game game, PlayerRep rep, Block tnt) {
			this.game = game;
			this.rep = rep;
			this.tnt = tnt;
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 60);
		}
		
		@Override
		public void run() {
			new Explosion(game, tnt.getLocation(), rep);
		}
		
	}