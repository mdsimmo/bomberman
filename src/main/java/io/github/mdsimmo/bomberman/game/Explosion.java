package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.arena.Arena;
import io.github.mdsimmo.bomberman.events.ExplosionEvent;
import io.github.mdsimmo.bomberman.game.gamestate.GamePlayingState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Explosion implements Listener {

	private static final Plugin plugin = Bomberman.instance;

	public static boolean spawnExplosion(Game game, Location center, Player cause, int strength) {

		// Find where the explosion should expand to
		Set<Block> fire = createFire(center, game.getArena(), strength);

		// Let others know
		ExplosionEvent event = new ExplosionEvent(game, cause, fire);
		if (event.isCancelled())
			return false;

		// Make the blocks fire
		Set<Block> exploding = new HashSet<>(event.getIgnited());
		for (Block b : fire) {
			b.setType(Material.FIRE);
		}

		// Schedule the blocks to return to air
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			for (Block b : exploding) {
				b.setType(Material.AIR);
			}
		}, 20);

		return true;
	}

	private final Game game;
	private final Player cause;
	private final Set<Block> blocks;

	private Explosion(Game game, Set<Block> blocks, Player cause) {
		this.game = game;
		this.cause = cause;
		this.blocks = blocks;

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			HandlerList.unregisterAll(this);
		}, 20);
		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

			for (Player p : game.players) {
				if (touching(player)) {

				}
			}

			// Replace the TNT item
			// TODO check that the player is still playing on explosion
			//if (player.isPlaying() && player.getState().getGame() == game)
			cause.getInventory().addItem(game.getSettings().bombItem.clone());
		}, 1, 1);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAnotherExplosion(ExplosionEvent e) {
		// Don't double remove blocks
		blocks.removeAll(e.getIgnited());
	}

	/**
	 * creates fire in the '+' pattern
	 */
	private static Set<Block> createFire(Location center, Arena arena, int strength) {
		Set<Block> blocks = new HashSet<>();
		blocks.addAll(createFire(center, arena, strength, 0, 1));
		blocks.addAll(createFire(center, arena, strength, 0, -1));
		blocks.addAll(createFire(center, arena, strength, 1, 0));
		blocks.addAll(createFire(center, arena, strength, -1, 0));
		return blocks;
	}

	/**
	 * creates a line of fire in the given x, z direction;
	 *
	 * @param xstep the unit to step in the x direction
	 * @param zstep the unit to step in the z direction
	 */
	private static Set<Block> createFire(Location center, Arena arena, int strength, int xstep, int zstep) {
		Set<Block> blocks = new HashSet<>();
		for (int i = 0; i <= strength; i++) {
			blocks.addAll(createFire(center, arena, strength, i * xstep, 1, i * zstep));
			blocks.addAll(createFire(center, arena, strength, i * xstep, -1, i * zstep));
			createFire(center, arena, strength, i * xstep, 0, i * zstep)
				return;
		}
	}

	/**
	 * creates fire at the given location if it can.
	 *
	 * @return true if the fire-ball should stop
	 */
	private static S createFire(Location center, Arena arena, int strength, int x, int y, int z) {
		Location l = center.clone().add(z, y, x);
		Block b = l.getBlock();

		// destroy dirt (or other blocks that can be blown up)
		if (arena.isDestructable(b.getType())) {
			return true;
		}

		// create fire on non solid blocks
		return b.getType().isSolid();

		// TODO explode other tnts
		/*for (Block otherTnt : new HashSet<>(game.tnts)) {
			if (otherTnt.equals(b)) {
				Bomb other = game.explosions.get(otherTnt);
				plugin.getServer().getScheduler()
						.cancelTask(other.eTaskId);
				other.run();
				return true;
			}
		}*/
		// not solid so stop
	}
}