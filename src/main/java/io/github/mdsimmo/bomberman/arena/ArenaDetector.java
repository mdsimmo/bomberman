package io.github.mdsimmo.bomberman.arena;

import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.utils.Box;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class ArenaDetector {

	private static final Material[] bannedArray = { Material.AIR,
			Material.DIRT, Material.GRASS, Material.STONE, Material.BEDROCK,
			Material.GRAVEL, Material.SAND, /*Material.LEGACY_LONG_GRASS,
			Material.LEGACY_LEAVES, Material.LEGACY_LEAVES_2, */Material.SANDSTONE,
			Material.WATER, Material.LAVA, Material.NETHERRACK, };
	private static final Set<Material> banned = new HashSet<>(
			Arrays.asList(bannedArray));
	private static final int MAX_BUILD_SIZE = Config.MAX_STRUCTURE.getValue();
	private static final int BUILD_RATE = Config.BUILD_RATE.getValue();

	private final Location target;

	ArenaDetector(Location target) {
		this.target = target;
	}

	public void start(Plugin plugin, Consumer<Box> callBack) {
		BukkitScheduler scheduler = plugin.getServer().getScheduler();

		AtomicInteger id = new AtomicInteger();
		Runnable detector = new DetectorTask(box -> {
			scheduler.cancelTask(id.get());
			plugin.getLogger().info( "Arena creation finished" );
			callBack.accept(box);
		});

		// start the thread
		id.set(scheduler.scheduleSyncRepeatingTask( plugin, detector, 1, 1 ));

		// add a starting point
		plugin.getLogger().info( "Creating an arena" );
	}

	private class DetectorTask implements Runnable {

		/**
		 * Blocks that have been found and all blocks around it have been added to
		 * {@link #toCheck}
		 */
		private final Set<Location> enclosed = new HashSet<>();
		/**
		 * Blocks that need to have their surroundings checked
		 */
		private final Set<Location> toCheck = new HashSet<>();
		/**
		 * A list to add elements to while toCheck is being iterated over
		 */
		private final Set<Location> toCheckBuffer = new HashSet<>();

		private final Consumer<Box> callback;

		private DetectorTask(Consumer<Box> callback) {
			this.callback = callback;
			toCheck.add(target);
		}

		@Override
		public void run() {
			if (!findMoreBlocks()) {
				// pick up on next tick
				return;
			}

			// find the outer bounds
			Box bounds = getBounds(enclosed);

			// notify listener we're finished
			callback.accept(bounds);
		}

		/**
		 * finds more blocks in the structure. Should be called over and over until
		 * it returns that all blocks have been found
		 *
		 * @return true if all blocks have been found
		 */
		private boolean findMoreBlocks() {
			// check if build size has been exceeded
			if (MAX_BUILD_SIZE > 0 && enclosed.size() > MAX_BUILD_SIZE) {
				return true;
			}

			int blocksCheckedThisTick = 0;

			if (toCheck.size() > 0) {
				for (Iterator<Location> i = toCheck.iterator(); i.hasNext(); ) {

					// check if we've done to much work done this tick
					if (blocksCheckedThisTick > BUILD_RATE) {
						return false;
					}
					blocksCheckedThisTick++;

					// add the block
					Location loc = i.next();
					i.remove();
					enclosed.add(loc);
					addConnected(loc);
				}
				// make sure the finishing tick is a fresh tick
				return false;
			}

			// Check for more not checked blocks
			if (toCheckBuffer.size() > 0) {
				toCheck.addAll(toCheckBuffer);
				toCheckBuffer.clear();
				// iterate over toChecked on next tick
				return false;
			}

			// finished
			return true;
		}

		/**
		 * Adds the blocks surrounding the given block to <code>toCheck</code>
		 * unless it is already in any of the lists
		 */
		private void addConnected(Location loc) {
			World world = loc.getWorld();
			assert world != null;
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = -1; k <= 1; k++) {
						Block b = world.getBlockAt(x + i, y + j, z + k);
						Location loc2 = b.getLocation();
						if (!banned.contains(b.getType())
								&& !enclosed.contains(loc2)
								&& !toCheck.contains(loc2)) {
							toCheckBuffer.add(loc2);
						}
					}
				}
			}
		}

		/**
		 * Gets the smallest Box that can contain all the blocks.
		 *
		 * @param locs All the block locations
		 * @return The bounding box
		 */
		private Box getBounds(Set<Location> locs) {
			if (locs.size() == 0)
				throw new IllegalArgumentException(
						"getBounds must have at least one block");
			int minx, maxx, miny, maxy, minz, maxz;
			maxx = maxy = maxz = Integer.MIN_VALUE;
			minx = miny = minz = Integer.MAX_VALUE;
			for (Location l : locs) {
				minx = (int) Math.min(l.getX(), minx);
				maxx = (int) Math.max(l.getX(), maxx);
				miny = (int) Math.min(l.getY(), miny);
				maxy = (int) Math.max(l.getY(), maxy);
				minz = (int) Math.min(l.getZ(), minz);
				maxz = (int) Math.max(l.getZ(), maxz);
			}
			return new Box(locs.iterator().next().getWorld(), minx, miny, minz,
					maxx - minx + 1, maxy - miny + 1, maxz - minz + 1);
		}
	}

}
