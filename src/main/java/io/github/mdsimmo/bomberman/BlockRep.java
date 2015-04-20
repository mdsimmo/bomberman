package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.save.CompressedSection;
import io.github.mdsimmo.bomberman.save.Save;
import io.github.mdsimmo.bomberman.utils.BlockLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class BlockRep {

	private static class InventoryRep extends BlockRep {

		private ItemStack[] contents;

		public InventoryRep(BlockState state) {
			super(state);
			contents = ((InventoryHolder)state).getInventory().getContents();
		}

		@SuppressWarnings( "unchecked" )
		public InventoryRep(List<String> data, Save save) {
			super(data, save);
			
			if (data.size() >= 3) {
				List<ItemStack> contents;
				ConfigurationSection expandedSection = getExpandedSection( save );
				if ( expandedSection == null ) {
					// an old save. Read list directly from path
					contents = (List<ItemStack>) save.getList( super.expandedSection );
				} else {
					contents = (List<ItemStack>) expandedSection.getList("contents");
				}
				this.contents = contents.toArray(new ItemStack[0]);
			} else {
				contents = new ItemStack[]{};
			}
		}

		@Override
		public void saveExtra(Save save) {
			ConfigurationSection extra = getExpandedSection(save);
			extra.set( "contents", Arrays.asList( contents ) );
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

			ConfigurationSection extra = getExpandedSection( save );
			List<String> list;
			if ( extra == null )
				// an old save. Read list directly from path
				list = save.getStringList( super.expandedSection );
			else
				list = extra.getStringList( "text" );
			text = list.toArray( new String[0] );
		}

		@Override
		public void saveExtra( Save save ) {
			ConfigurationSection expandedSection = getExpandedSection( save );
			expandedSection.set( "text", Arrays.asList(text) );
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
	private List<String> commands;
	String expandedSection = null;

	@SuppressWarnings("deprecation")
	private BlockRep(BlockState state) {
		if (state == null)
			return;
		material = state.getType();
		data = state.getData().getData();
		commands = CommandSign.getCommands( BlockLocation.getLocation( state.getBlock() ) );
	}

	private BlockRep(List<String> data, Save save) {
		material = Material.getMaterial(data.get(0));
		this.data = Byte.parseByte(data.get(1), 10);
		if ( data.size() == 3 )
			this.expandedSection = data.get( 2 );
			
	}

	/**
	 * Sets the given block to be the same as this block
	 * 
	 * @param b
	 *            the block to set
	 */
	@SuppressWarnings("deprecation")
	public void setBlock(Block b) {
		BlockLocation l = BlockLocation.getLocation( b );
		CommandSign.removeSign( l );
		b.setTypeIdAndData( material.getId(), data, true );
		if ( commands != null )
			CommandSign.addCommands( l, commands );
	}

	public final String save(Save save) {
		// write the internal data
		sub.reset();
		sub.addParts(material.toString());
		sub.addParts(data);
		if ( commands != null ) {
			ConfigurationSection s = getExpandedSection( save );
			s.set( "commands", commands );
		}
		
		saveExtra( save );
		
		if ( expandedSection != null )
			sub.addParts( expandedSection );
		return sub.toString();
	}
	
	public void saveExtra( Save save ) {
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
		sub.reset();
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
	
	public byte getData() {
		return data;
	}
	
	/**
	 * Gets a ConfiguarationSection where extra data can be stored
	 * @param save the save file to get the section from
	 * @return the extra save location
	 */
	ConfigurationSection getExpandedSection(Save save) {
		if ( expandedSection != null )
			return save.getConfigurationSection( expandedSection );
		if (!ids.containsKey(save))
			ids.put(save, 0);
		Integer id = ids.get(save);
		expandedSection = EXTRA_PATH + ".data-" + id;
		id++;
		return save.createSection( expandedSection );
	}
}
