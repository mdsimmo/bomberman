package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.Bomberman;

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
	
	public boolean excecute(CommandSender sender, List<String> args) {
		if (isAllowedBy(sender)) {
			if (run(sender, args))
				return true;
			else {
				if (args.size() == 0) {
					// assume asking for help
					displayHelp(sender, args);
					return true;
				} else
					return false;
			}
				
		} else {
			denyPermission(sender);
			return true;
		}
	}
	
	public void denyPermission(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You do not have permission!");
	}
	
	/**
	 * displays the help
	 * @param sender person to send to
	 */
	public void displayHelp(CommandSender sender, List<String> args) {
		Bomberman.sendHeading(sender, "Help: /" + name());
		sender.sendMessage(info(sender));
	}
	
	/**
	 * Some info about the command
	 * @param sender the sender
	 * @return the (coloured) info
	 */
	public String info(CommandSender sender) {
		return ChatColor.GOLD + "Description: " + ChatColor.WHITE + description() + " \n"
				+ ChatColor.GOLD + "Usage: " + ChatColor.WHITE + usage(sender) + "\n";
	}
	/**
	 * @return A sentence describing the command
	 */
	public abstract String description();
	
	/**
	 * @param sender TODO
	 * @return How to use the command
	 */
	public abstract String usage(CommandSender sender);
	
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
	public void incorrectUsage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Incorrect usage!");
	}
}