package io.github.mdsimmo.bomberman.arena;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.utils.CompressedList;
import io.github.mdsimmo.bomberman.utils.Box;
import io.github.mdsimmo.bomberman.utils.Dim;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class Arena implements ConfigurationSerializable {

	/**
	 * Makes an arena from the given box. Does not save the arena.
	 * @param box the box that contains the arena's structure
	 * @return The newly generated arena
	 */
	public static Arena from(String name, Box box) {
		BlockData[][][] data = new BlockData[box.size.x][box.size.y][box.size.z];
		for ( int i = 0; i < box.size.x; i++ ) {
			for ( int j = 0; j < box.size.y; j++ ) {
				for ( int k = 0; k < box.size.z; k++ ) {
					Location l = box.corner().add(i, j, k);
					data[i][j][k] = l.getBlock().getBlockData();
				}
			}
		}
		return new Arena(name, box.size, data);
	}

	public final String name;
	public final Dim size;

	private final BlockData[][][] blocks;
	private final ArrayList<Vector> spawns;

	private final List<Material> destructables = Config.BLOCKS_DESTRUCTABLE.getValue();
	private final List<Material> droppingBlocks = Config.BLOCKS_DROPPING.getValue();

	/**
	 * Internal constructor - build new arenas through ArenaGenerator
	 *
	 * @param size   size
	 * @param blocks each individual block (make sure that the block states cannot ever change )
	 */
	private Arena(String name, Dim size, BlockData[][][] blocks) {
		this.size = size;
		this.blocks = blocks;
		this.name = name;

		// find the spawn points
		spawns = new ArrayList<>();
		for (int i = 0; i < blocks.length; ++i) {
			BlockData[][] array_i = blocks[i];
			for (int j = 0; j < array_i.length; ++j) {
				BlockData[] array_j = array_i[j];
				for (int k = 0; k < array_j.length; ++k) {
					BlockData d = array_j[k];
					if (Tag.WOOL.isTagged(d.getMaterial())) {
						spawns.add(new Vector(i, j, k));
					}
				}
			}
		}
	}
		/*
		if ( block.getMaterial() == Material.WOOL ) {
			DyeColor color = DyeColor.getByData( block.getData() );
			List<Vector> list = spawnPoints.get( color );
			if ( list == null )
				spawnPoints.put( color, list = new ArrayList<Vector>() );
			// alter the position so players spawn in the center of the block above.
			// y+=1.1 instead of 1 because players sometimes fall through block
			// (maybe due to some rounding issues?)
			list.add( place.add( new Vector( 0.5, 1.1, 0.5 ) ) );
		}*/

	BlockData getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}

	public boolean isDestructable(Material m) {
		return destructables.contains(m) || isDropping(m);
	}

	public boolean isDropping(Material m) {
		return droppingBlocks.contains(m);
	}

	public List<Vector> spawns() {
		return spawns;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<>();
		data.put("name", name);
		data.put("size", size);
		data.put("data", CompressedList.encode(size.stream().map(l -> getBlock(l.x, l.y, l.z)).iterator(), b -> b.getAsString(true)));
		data.put("dropping", droppingBlocks);
		data.put("destructable", destructables);
		return data;
	}

	@SuppressWarnings({"unchecked", "unused"})
	public static Arena deserialize(Map<String, Object> data) {
		List<Material> dropping = (List<Material>) data.get("dropping");
		List<Material> destructable = (List<Material>) data.get("destructable");
		Dim size = (Dim) data.get("size");
		String name = (String) data.get("name");

		List<BlockData> blockList = CompressedList.decode((String)data.get("data"), s -> Bomberman.instance.getServer().createBlockData(s));
		BlockData[][][] blocks = new BlockData[size.x][size.y][size.z];
		AtomicInteger i = new AtomicInteger();
		size.stream().forEach(l -> blocks[l.x][l.y][l.z] = blockList.get(i.incrementAndGet()-1));
		return new Arena(name, size, blocks);
	}
}