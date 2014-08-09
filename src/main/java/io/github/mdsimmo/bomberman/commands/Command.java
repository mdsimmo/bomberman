package io.github.mdsimmo.bomberman.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class Command {

	public enum Permission {
		
		OBSERVER("bomberman.observer"),
		PLAYER("bomberman.player"),
		GAME_OPERATE("bomberman.operator"),
		GAME_DICTATE("bomberman.dictator"),
		ARENA_EDITING("bomberman.arena");
		
		public final String permission;
		
		Permission(String permission) {
			this.permission = permission;
		}
	}
	
	private Command parent;
	
	public Command(Command parent) {
		this.parent = parent;
	}
	/**
	 * Gets the commands name. <br>
	 * This should be the relative name (eg, fare instead of bm.game.set.fare)<br>
	 * Do not put any spaces
	 *  
	 * @return the name
	 */
	public abstract String name();
	
	/**
	 * Gets a list of values to return. <br>
	 * This list will have the cmd.startsWith(...) stuff automatically applied   
	 * 
	 * @param sender the sender sending the message
	 * @param args the current arguments typed
	 * @return the options
	 */
	public abstract List<String> options (CommandSender sender, List<String> args);
	
	/**
	 * Excecute the command
	 * @param sender the sender
	 * @param args the arguments
	 * @return true if corectly typed. False will display info
	 */
	public abstract boolean run (CommandSender sender, List<String> args);
	
	public String heading (String text) {
		String head = ChatColor.YELLOW + "---------"
				+ ChatColor.WHITE + " /" + name() + " " + ChatColor.YELLOW;
		for (int i = name().length(); i < 36; i++) {
			head += "-";
		}
		return head;
	}
	
	/**
	 * displays the help
	 * @param sender person to send to
	 */
	public void displayHelp(CommandSender sender, List<String> args) {
		sender.sendMessage(heading(name()));
		sender.sendMessage(info());
	}
	
	/**
	 * @return Some info about the command
	 */
	public String info() {
		return ChatColor.GOLD + "Description: " + ChatColor.WHITE + description() + " \n"
				+ ChatColor.GOLD + "Usage: " + ChatColor.WHITE + usage() + "\n";
	}
	/**
	 * @return A sentence describing the command
	 */
	public abstract String description();
	
	/**
	 * @return How to use the command
	 */
	public abstract String usage();
	
	/**
	 * @return the permission needed to run this command
	 */
	public abstract Permission permission();
	
	/**
	 * gets if the given sender has permission to run this command
	 * @param sender the sender
	 * @return true if they have permission
	 */
	public boolean isAllowedBy(CommandSender sender) {
		return sender.hasPermission(permission().permission);
	}
	
	/**
	 * short for path(" ");
	 */
	public String path() {
		return path(" ");
	}
	
	/**
	 * gets the path to the command
	 * @param seperator what to seperate parent/child commands by
	 * @return the path
	 */
	public String path(String seperator) {
		String path = "";
		if (parent != null)
			path += parent.path(seperator);
		path += name() + seperator;
		return path;
	}
}