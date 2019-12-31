package io.github.mdsimmo.bomberman.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class Utils {

	private static StringBuffer buffer = new StringBuffer(); 
	
	public static <T> T random(Collection<T> collection) {
		int random = (int) (Math.random() * collection.size());
		Iterator<T> iterator = collection.iterator();
		int i = 0;
		T obj = null;
		while (iterator.hasNext()) {
			obj = iterator.next();
			i++;
			if (i >= random)
				return obj;
		}
		return null;
	}
	
	/**
	 * Turns the list into a string. Each argument is seperated by a space
	 * @param list the list to become a string
	 * @return the string list
	 */
	public static String listToString(List<String> list) {
		buffer.setLength(0);
		for (String s : list) {
			if (buffer.length() != 0)
				buffer.append(' ');
			buffer.append(s);
		}
		return buffer.toString();
	}

	/**
	 * Gets the block that the player is looking at
	 * @param player the player
	 * @param range the range that the block must be in
	 * @return the block found. null if no block found in the range
	 */
	public static final Block getTarget(Player player, int range) {
		BlockIterator iter = new BlockIterator(player, range);
		Block lastBlock = iter.next();
		while (iter.hasNext()) {
			lastBlock = iter.next();
			if (lastBlock.getType() == Material.AIR)
				continue;
			return lastBlock;
		}
		return null;
	}

	/**
	 * Gets the title of the file. Eg, "readme.txt" will return "readme"
	 * @param file the file's name
	 * @return the files title
	 */
	public static String getFileTitle(String file) {
		String[] parts = file.split("\\.");
		if (parts.length == 0)
			return file;
		else
			return parts[0];
	}
	
	public static String readFile( File file ) throws IOException {
		try {
			return new String( Files.readAllBytes( file.toPath() ) );
		} catch ( Exception e ) {
			throw new IOException( "Error reading file: " + file, e );
		}
	}
}
