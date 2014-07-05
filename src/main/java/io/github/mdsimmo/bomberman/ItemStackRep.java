package io.github.mdsimmo.bomberman;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemStackRep {
	
	@SuppressWarnings("deprecation")
	public static void saveStack(SaveWriter sw, ItemStack stack) throws IOException {
		if (stack == null) {
			sw.writePart(stack);
			return;
		}
		sw.writePart(stack.getType());
		sw.writePart(stack.getAmount());
		sw.writePart(stack.getData().getData());
		sw.writePart(stack.getDurability());
		sw.writePart(stack.getItemMeta().getDisplayName());
		for (Enchantment e : stack.getEnchantments().keySet()) {
			sw.writePart(e.getName());
			sw.writePart(stack.getEnchantmentLevel(e));
		}
		sw.writePart("end");
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack readStack(SaveReader sr) throws IOException {
		String first = sr.readPart();
		if (first.equals("null"))
			return null;
		Material type = Material.valueOf(first);
		ItemStack stack = new ItemStack(type);
		stack.setAmount(sr.readInt());
		stack.getData().setData(sr.readByte());
		stack.setDurability(sr.readShort());
		stack.getItemMeta().setDisplayName(sr.readPart());
		Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
		
		while (true) {
			String part = sr.readPart();
			if (part.equals("end"))
				break;
			Enchantment enchant = Enchantment.getByName(part);
			enchantments.put(enchant, sr.readInt());
		}
		stack.addEnchantments(enchantments);
		return stack;
	}	
}

