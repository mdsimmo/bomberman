package io.github.mdsimmo.bomberman.arenabuilder;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.utils.Box;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class ArenaDetector implements Runnable {

	private static final Plugin plugin = Bomberman.instance;
	private static final Material[] bannedArray = { Material.AIR,
			Material.DIRT, Material.GRASS, Material.STONE, Material.BEDROCK,
			Material.GRAVEL, Material.SAND, Material.LONG_GRASS,
			Material.LEAVES, Material.LEAVES_2, Material.SANDSTONE,
			Material.WATER, Material.LAVA, Material.NETHERRACK, };
	private static final Set<Material> banned = new HashSet<Material>(
			Arrays.asList( bannedArray ) );
	private static final int MAX_BUILD_SIZE = Config.MAX_STRUCTURE.getValue();
	private static final int BUILD_RATE = Config.BUILD_RATE.getValue();

	public interface BoundingListener {
		public void onBoundingDetected( Box box );
	}

	private int taskID = -1;
	private BoundingListener callBack;
	/**
	 * Blocks that have been found and all blocks around it have been added to
	 * {@link #toCheck}
	 */
	private Set<Location> enclosed;
	/**
	 * Blocks that need to have their surroundings checked
	 */
	private Set<Location> toCheck;
	/**
	 * A list to add elements to while toCheck is being iterated over
	 */
	private Set<Location> toCheckBuffer;

	/**
	 * Gets the bounds of the structure over several ticks. Will inform the
	 * BoundingLister of what the size is when finished.
	 * 
	 * @throws IllegalStateException
	 *             if this is already working
	 */
	public void getBoundingStructure( Block target, BoundingListener callBack ) {
		if ( this.taskID != -1 )
			throw new IllegalStateException(
					"Already busy detecting a structure" );
		this.callBack = callBack;
		enclosed = new HashSet<>();
		toCheck = new HashSet<>();
		toCheckBuffer = new HashSet<>();
		
		// set the starting point
		toCheck.add( target.getLocation() );

		// start the thread
		this.taskID = plugin.getServer().getScheduler()
				.scheduleSyncRepeatingTask( plugin, this, 1, 1 );

		// add a starting point
		plugin.getLogger().info( "Creating an arena" );
	}

	/**
	 * Called when the bounding box is found. Cancels the task and informs the
	 * listener
	 * 
	 * @param box
	 *            the box that was found
	 */
	private void finish( Box box ) {
		plugin.getServer().getScheduler().cancelTask( taskID );
		taskID = -1;
		if ( callBack != null )
			callBack.onBoundingDetected( box );
		System.out.println( "Arena creation finished" );
	}

	public void run() {
		if ( !findMoreBlocks() ) {
			// pick up on next tick
			return;
		}

		// find the outer bounds
		Box bounds = getBounds( enclosed );

		// let the garbage collector clean up
		enclosed = null;
		toCheck = null;
		toCheckBuffer = null;

		// notify listener we're finished
		finish( bounds );
	}

	/**
	 * finds more blocks in the structure. Should be called over and over until
	 * it returns that all blocks have been found
	 * 
	 * @return true if all blocks have been found
	 */
	private boolean findMoreBlocks() {
		// check if build size has been exceeded
		if ( MAX_BUILD_SIZE > 0 && enclosed.size() > MAX_BUILD_SIZE ) {
			finish( null );
			return true;
		}

		int blocksCheckedThisTick = 0;

		while ( toCheck.size() > 0 ) {
			for ( Iterator<Location> i = toCheck.iterator(); i.hasNext(); ) {

				// check if we've done to much work done this tick
				if ( blocksCheckedThisTick > BUILD_RATE ) {
					blocksCheckedThisTick = 0;
					return false;
				}
				blocksCheckedThisTick++;

				// add the block
				Location loc = i.next();
				i.remove();
				enclosed.add( loc );
				addConnected( loc );
			}
			// make sure the finishing tick is a fresh tick
			return false;
		}

		// Check for more not checked blocks
		if ( toCheckBuffer.size() > 0 ) {
			toCheck.addAll( toCheckBuffer );
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
	private void addConnected( Location loc ) {
		World world = loc.getWorld();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		for ( int i = -1; i <= 1; i++ ) {
			for ( int j = -1; j <= 1; j++ ) {
				for ( int k = -1; k <= 1; k++ ) {
					Block b = world.getBlockAt( x + i, y + j, z + k );
					Location loc2 = b.getLocation();
					if ( !banned.contains( b.getType() )
							&& !enclosed.contains( loc2 )
							&& !toCheck.contains( loc2 )
							&& !toCheckBuffer.contains( loc2 ) ) {
						toCheckBuffer.add( loc2 );
					}
				}
			}
		}
	}

	/**
	 * Gets the smallest Box that can contain all the blocks.
	 * 
	 * @param blocks
	 *            All the blocks
	 * @return The bounding box
	 */
	private Box getBounds( Set<Location> locs ) {
		if ( locs.size() == 0 )
			throw new IllegalArgumentException(
					"getBounds must have at least one block" );
		int minx, maxx, miny, maxy, minz, maxz;
		maxx = maxy = maxz = Integer.MIN_VALUE;
		minx = miny = minz = Integer.MAX_VALUE;
		for ( Location l : locs ) {
			minx = (int)Math.min( l.getX(), minx );
			maxx = (int)Math.max( l.getX(), maxx );
			miny = (int)Math.min( l.getY(), miny );
			maxy = (int)Math.max( l.getY(), maxy );
			minz = (int)Math.min( l.getZ(), minz );
			maxz = (int)Math.max( l.getZ(), maxz );
		}
		return new Box( locs.iterator().next().getWorld(), minx, miny, minz,
				maxx - minx + 1, maxy - miny + 1, maxz - minz + 1 );
	}

}
