package io.github.mdsimmo.bomberman;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.plugin.Plugin;

public class SaveWriter extends FileWriter {

	private static Plugin plugin = Bomberman.instance;
	
	public SaveWriter(File file) throws IOException {
		super(file);
	}
	
	public SaveWriter(String filename) throws IOException {
		super(plugin.getDataFolder() + "/" + filename);
	}
	
	public void writePart(Object part) throws IOException {
		write(part + ":");
	}
}
