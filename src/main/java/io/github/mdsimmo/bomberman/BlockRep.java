package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.Board.CompressedSection;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

public class BlockRep {
	
	public Material material = Material.AIR;
	public byte data = 0;
	public ItemStack[] chestContents;
	
	/**
	 * creates a block rep of air
	 */
	public BlockRep() {
	}
	
	/**
	 * creates a block representing the given block
	 * @param b the block to represent
	 */
	public BlockRep(Block b) {
		this(b.getState());
	}

	
	@SuppressWarnings("deprecation")
	public BlockRep(BlockState b) {
		material = b.getType();
		data = b.getData().getData();
	}

	/**
	 * Sets the given block to be the same as this block
	 * @param b the block to set
	 */
	@SuppressWarnings("deprecation")
	public void setBlock(Block b) {
		/*if (material == Material.DIRT && Math.random() < 0.25) {
			for (int i = -1; i <= 1; i++) {
				Block relative = b.getRelative(0, i, 0);
				if (relative.getType() == Material.DIRT) {
					relative.setType(Material.AIR);
					b.getRelative(0, i, 0).setData((byte)0);
				}
			}
		} else {*/
			b.setType(material);
			b.setData(data);
		//}
	}
	
	private static CompressedSection sub = new CompressedSection(',');

	@Override
	public String toString() {
		// write a the internal data
		sub.reset();
		sub.addParts(material.toString());
		sub.addParts(data);
		
		if (material == Material.CHEST)
			sub.addParts("THIS IS A CHEST");
		
		return sub.getValue();
	}
	
	/**
	 * Loads a block from a string
	 * @param section the string to load from. This must be exactly as it is from saveTo()
	 * @return the loaded block
	 */
	public static BlockRep loadFrom(String section) {
		sub.setValue(section);
		List<String> sections = sub.readParts();
		BlockRep rep = new BlockRep();
		rep.material = Material.getMaterial(sections.get(0));
		rep.data = Byte.parseByte(sections.get(1), 10);
		
		if (rep.material == Material.CHEST)
			System.out.println(sections.get(2));
		
		return rep;
	}
}
