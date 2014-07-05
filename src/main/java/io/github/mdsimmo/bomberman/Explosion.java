package io.github.mdsimmo.bomberman;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Explosion implements Listener, Runnable {

	private Location spawn;
	private int strength;
	private Game game;
	private JavaPlugin plugin = Bomberman.instance;
	private HashMap<Location, Boolean> fire = new HashMap<>();
	private PlayerRep rep;
	
	public Explosion(Game game, Location spawn, PlayerRep rep) {
		this.spawn = spawn;
		this.game = game;
		this.rep = rep;
		spawn.getBlock().setType(Material.AIR);
		strength = rep.bombStrength();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 20);
		spawn.getWorld().playSound(spawn, Sound.EXPLODE, 1, (float) Math.random()+0.5f);
		createFire();
	}

	private void createFire() {
		int i = 1;
		for (int j = -1; j <= 1; j++) {
			createFire(0, j, 0);
			while (createFire(0, j, i) && i < strength) {i++;}
			i = 1;
			while (createFire(0, j, -i) && i < strength) {i++;}
			i = 1;
			while (createFire(i, j, 0) && i < strength) {i++;}
			i = 1;
			while (createFire(-i, j, 0) && i < strength) {i++;}
			i = 1;
		}
	}

	/**
	 * creates fire at the given location if it can.
	 * Returns true if the fireball should continue
	 */
	private boolean createFire(int x, int y, int z) {
		Location l = spawn.clone().add(z, y, x);
		Block b = l.getBlock();
		if (!b.getType().isSolid() || b.getType() == Material.DIRT) {
			boolean solid = false;
			if (b.getType().isSolid())
				solid = true;
			
			b.setType(Material.FIRE);
			fire.put(l, solid);
			
			return !solid;
		}
		return false;
	}

	@Override
	public void run() {
		for (Location l : fire.keySet()) {
			l.getBlock().setType(Material.AIR);
			if (fire.get(l))
				game.drop(l);
		}
		rep.player.getInventory().addItem(new ItemStack(Material.TNT));
	}
	
}
