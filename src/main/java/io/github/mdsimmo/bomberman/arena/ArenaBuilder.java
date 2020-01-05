package io.github.mdsimmo.bomberman.arena;

import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.utils.Box;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

final class ArenaBuilder {
	private static int buildRate = Config.BUILD_RATE.getValue();
	private final Arena arena;
	private final Location location;
	private final Runnable listener;
		
	ArenaBuilder(Arena arena, Location location, Runnable l) {
		this.arena = arena;
		this.location = location;
		this.listener = l;

	}

	void start(Plugin plugin) {
		BukkitScheduler scheduler = plugin.getServer().getScheduler();
		AtomicInteger id = new AtomicInteger();

		// destroy delicate things
		id.set(scheduler.scheduleSyncRepeatingTask(plugin,
				new DestroyerTask(() -> {
					scheduler.cancelTask(id.get());

					// build the new arena
					id.set(scheduler.scheduleSyncRepeatingTask(plugin,
							new BuilderTask(() -> scheduler.cancelTask(id.get())), 1, 1));

				}), 1, 1));
	}

	/**
	 * Class which destroys all the touchy blocks in an arena
	 */
	private class DestroyerTask implements Runnable {

		final Runnable callback;
		final Box box;

		private DestroyerTask(Runnable callback) {
			this.callback = callback;

			box = new Box(location, arena.size);
		}

		@Override
		public void run() {
			for (int i = 0; i < arena.size.x; ++i) {
				for (int j = 0; j < arena.size.y; ++j) {
					for (int k = 0; k < arena.size.z; ++k) {
						Location l = location.clone().add(i, j, k);
						Block b = l.getBlock();
						Material m = b.getType();
						if (m.hasGravity() || !m.isSolid()) {
							b.setType(Material.AIR);
						}
					}
				}
			}

			listener.run();
		}
	}

	private class BuilderTask implements Runnable {
		final Runnable callback;

		int ticks = 0;    // the number of times this task has been ran
		int count = 0;    // the current block count

		private final HashMap<Location, BlockData> delayed = new HashMap<>();

		private BuilderTask(Runnable callback) {
			this.callback = callback;
		}

		@Override
		public void run() {
			// limit the maximum number of blocks placed in a single tick
			++ticks;
			int maxCount = ticks * buildRate;

			for (; count < maxCount; ++count) {
				if (count >= arena.size.x * arena.size.y * arena.size.z) {
					// finishing touches
					delayed.forEach((loc, data) -> loc.getBlock().setBlockData(data));

					// cancel out of the job
					listener.run();
					return;
				}

				// Make sure to build from bottom up (so sand doesn't fall)
				int y = count / (arena.size.x * arena.size.z);
				int x = (count / arena.size.z) % arena.size.x;
				int z = count % arena.size.z;

				Location l = location.clone().add(x, y, z);
				BlockData data = arena.getBlock(x, y, z);

				// Only place the solid blocks so non-solid blocks don't pop off
				if (data.getMaterial().isSolid()) {
					l.getBlock().setBlockData(data);
				} else {
					delayed.put(l, data);
				}
			}
		}
	}
}
