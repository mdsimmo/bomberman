package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;

public abstract class Command {

	public enum Permission {
		
		OBSERVER("bomberman.observer"),
		PLAYER("bomberman.player"),
		GAME_OPERATE("bomberman.operator"),
		GAME_DICTATE("bomberman.dictator"),
		ARENA_EDITING("bomberman.arena"), 
		PROTECTION_VOID("bomberman.void-protection");
		
		public final String permission;
		
		Permission(String permission) {
			this.permission = permission;
		}
		
		public boolean isAllowedBy(CommandSender sender) {
			return sender.hasPermission(permission);
		}
	}
	
	protected Command parent;
	
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
	@Nonnull
	public abstract Text name();
	
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
	
	public boolean execute(CommandSender sender, List<String> args) {
		if (isAllowedBy(sender)) {
			if (run(sender, args))
				return true;
			else {
				if (args.size() == 0) {
					// assume asking for help
					help(sender, args);
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
		Chat.sendMessage(sender, getMessage(Text.DENY_PERMISSION, sender));
	}
	
	/**
	 * Sends help to the sender
	 * @param sender the player to help
	 * @param args the arguments the player typed
	 */
	public void help(CommandSender sender, List<String> args) {
		Chat.sendHeading(sender, getMessage(Text.HELP, sender));
		Map<Message, Message> help = info(sender, args);
		Message temp = extra(sender, args);
		if (temp != null)
			help.put(getMessage(Text.EXTRA, sender), temp);
		temp = example(sender, args);
		if (temp != null)
			help.put(getMessage(Text.EXAMPLE, sender), temp);
		Chat.sendMap(sender, help);
	}
	
	public abstract Message extra(CommandSender sender, List<String> args);
	
	public abstract Message example(CommandSender sender, List<String> args);
	
	/**
	 * Some info about the command
	 * @param sender the sender
	 * @return the (coloured) info
	 */
	public Map<Message, Message> info(CommandSender sender, List<String> args) {
		Map<Message, Message> info = new LinkedHashMap<Message, Message>();
		info.put(getMessage(Text.DESCTIPTION, sender), description(sender, args));
		info.put(getMessage(Text.USAGE, sender), usage(sender, args));
		return info;
	}
	/**
	 * @param sender the player asking for the description
	 * @param args the arguments the player typed
	 * @return A sentence describing the command
	 */
	public abstract Message description(CommandSender sender, List<String> args);
	
	/**
	 * The command's syntax
	 * @param sender the sender
	 * @param args the args the sender used
	 * @return How to use the command
	 */
	public abstract Message usage(CommandSender sender, List<String> args);
	
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
		return permission().isAllowedBy(sender);
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
			path += parent.path(seperator) + seperator;
		path += name().getMessage(null).toString();
		return path;
	}
	public void incorrectUsage(CommandSender sender, List<String> args) {
		Chat.sendMessage(sender, getMessage(Text.INCORRECT_USAGE, sender, path(), Utils.listToString(args)));
	}
	
	public Message getMessage(Text text, CommandSender sender, Object ... objects) {
		Object objs[] = new Object[objects.length+2];
		objs[0] = name().getMessage(sender).toString();
		objs[1] = path();
		for (int i = 0; i < objects.length; i++) {
			objs[i+2] = objects[i];
		}
		return text.getMessage(sender, objs);
	}
}
