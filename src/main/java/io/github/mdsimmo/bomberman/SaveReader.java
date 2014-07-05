package io.github.mdsimmo.bomberman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.plugin.Plugin;

public class SaveReader extends FileReader {

	private static Plugin plugin = Bomberman.instance;
	
	public SaveReader(File file) throws FileNotFoundException {
		super(file);
	}
	
	public SaveReader(String filename) throws FileNotFoundException {
		super(plugin.getDataFolder() + "/" + filename);
	}

	/**
	 * reads the next part of the file.
	 * @return the read part. null if the end is reached
	 */
	public String readPart() throws IOException {
		int read = 0;
		String part = "";
		while (true) {
			read = read();
			if (read == -1)
				return null;
			if (read == ':')
				return part;
			part += (char)read;
		}
	}
	
	public int readInt() throws IOException {
		return Integer.parseInt(readPart(), 10);
	}
	
	public double readDouble() throws IOException {
		return Double.parseDouble(readPart());
	}
	
	public boolean readBoolean() throws IOException {
		return Boolean.parseBoolean(readPart());
	}
	
	public byte readByte() throws IOException {
		return Byte.parseByte(readPart(), 10);
	}

	public short readShort() throws IOException {
		return Short.parseShort(readPart(), 10);
	}	
}
