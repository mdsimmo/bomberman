package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.save.CompressedSection;
import io.github.mdsimmo.bomberman.save.Save;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class BlockRep {

	private static class InventoryRep extends BlockRep {

		private ItemStack[] contents;

		public InventoryRep(BlockState state) {
			super(state);
			contents = ((InventoryHolder)state).getInventory().getContents();
		}

		public InventoryRep(List<String> data, Save save) {
			super(data, save);
			
			if (data.size() >= 3) {
				String expandedSection = data.get(2);
				@SuppressWarnings("unchecked")
				List<ItemStack> contents = (List<ItemStack>) save.getList(expandedSection);
				this.contents = contents.toArray(new ItemStack[0]);
			} else {
				contents = new ItemStack[]{};
			}
		}

		@Override
		public String save(Save save) {
			super.save(save);
			String expandedSection = getExpandedSection(save);
			sub.addParts(expandedSection);
			save.set(expandedSection, Arrays.asList(contents));
			return sub.toString();
		}
		
		@Override
		public void setBlock(Block b) {
			super.setBlock(b);
			InventoryHolder inv = (InventoryHolder)b.getState();
			inv.getInventory().setContents(contents);
			b.getState().update();
		}
	}

	private static class SignRep extends BlockRep {
		private String[] text;

		public SignRep(BlockState state) {
			super(state);
			text = ((Sign) state).getLines();
		}

		public SignRep(List<String> data, Save save) {
			super(data, save);

			if (data.size() >= 3) {
				String expandedSection = data.get(2);
				List<String> list = (List<String>) save.getStringList(expandedSection);
				text = list.toArray(new String[0]);
			} else {
				text = new String[]{};
			}
		}

		@Override
		public String save(Save save) {
			super.save(save);
			String expandedSection = getExpandedSection(save);
			sub.addParts(expandedSection);
			save.set(expandedSection, Arrays.asList(text));
			return sub.toString();
		}
		
		@Override
		public void setBlock(Block b) {
			super.setBlock(b);
			Sign sign = (Sign)b.getState();
			for (int i = 0; i < text.length; i++)
				sign.setLine(i, text[i]);
			sign.update();
		}
	}
	
	private static HashMap<Save, Integer> ids = new HashMap<>();
	public static final String EXTRA_PATH = "blocks.extra";
	private static BlockRep blank = new BlockRep(null);
	private static CompressedSection sub = new CompressedSection(',');
	
	public static BlockRep createBlock(BlockState state) {
		if (state instanceof InventoryHolder)
			return new InventoryRep(state);
		if (state instanceof Sign)
			return new SignRep(state);
		return new BlockRep(state);
	}
	
	public static BlockRep createBlock(Block b) {
		if (b == null)
			return createBlank();
		else
			return createBlock(b.getState());
	}
	
	public static BlockRep createBlank() {
		return blank;
	}

	private Material material = Material.AIR;
	private byte data = 0;

	@SuppressWarnings("deprecation")
	private BlockRep(BlockState state) {
		if (state == null)
			return;
		material = state.getType();
		data = state.getData().getData();
	}

	private BlockRep(List<String> data, Save save) {
		material = Material.getMaterial(data.get(0));
		this.data = Byte.parseByte(data.get(1), 10);
	}

	/**
	 * Sets the given block to be the same as this block
	 * 
	 * @param b
	 *            the block to set
	 */
	@SuppressWarnings("deprecation")
	public void setBlock(Block b) {
		b.setType(material);
		b.setData(data);
	}

	public String save(Save save) {
		// write a the internal data
		sub.reset();
		sub.addParts(material.toString());
		sub.addParts(data);
		return sub.toString();
	}

	/**
	 * Loads a block from a string
	 * 
	 * @param section
	 *            the string to load from. This must be exactly as it is from
	 *            saveTo()
	 * @param the
	 *            save file being read from
	 * @return the loaded block
	 */
	public static BlockRep loadFrom(String section, Save save) {
		sub.setValue(section);
		List<String> data = sub.readParts();
		Material m = Material.getMaterial(data.get(0));
		if (isSign(m))
			return new SignRep(data, save);
		if (isInventory(m))
			return new InventoryRep(data, save);
		return new BlockRep(data, save);
	}

	public static boolean isSign(Material m) {
		return m == Material.SIGN 
				|| m == Material.SIGN_POST
				|| m == Material.WALL_SIGN;
	}

	public static boolean isInventory(Material m) {
		return m == Material.BEACON
				|| m == Material.BREWING_STAND 
				|| m == Material.CHEST
				|| m == Material.DISPENSER
				|| m == Material.DROPPER
				|| m == Material.FURNACE
				|| m == Material.HOPPER;
	}

	public Material getMaterial() {
		return material;
	}
	
	private static String getExpandedSection(Save save) {
		if (!ids.containsKey(save))
			ids.put(save, 0);
		Integer id = ids.get(save);
		String section = EXTRA_PATH + ".data-" + id;
		id++;
		return section;
	}
}
