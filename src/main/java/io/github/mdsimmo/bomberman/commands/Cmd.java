package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Formattable;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public abstract class Cmd implements Formattable {

	public enum Permission {

		OBSERVER(
				"bomberman.observer" ),
		PLAYER(
				"bomberman.player" ),
		GAME_OPERATE(
				"bomberman.operator" ),
		GAME_DICTATE(
				"bomberman.dictator" ),
		ARENA_EDITING(
				"bomberman.arena" ),
		PROTECTION_VOID(
				"bomberman.void-protection" );

		public final String	permission;

		Permission( String permission ) {
			this.permission = permission;
		}

		public boolean isAllowedBy( CommandSender sender ) {
			return sender.hasPermission( permission );
		}
	}

	protected Cmd parent;

	public Cmd( Cmd parent ) {
		this.parent = parent;
	}

	/**
	 * Gets the commands name. <br>
	 * This should not include the path (eg, "fare" instead of "bm.game.set.fare")<br>
	 * Do not put any spaces
	 * 
	 * @return the name
	 */
	public abstract Message name( CommandSender sender );

	/**
	 * Gets a list of values to return. <br>
	 * This list will have the cmd.startsWith(...) stuff automatically applied
	 * 
	 * @param sender
	 *            the sender sending the message
	 * @param args
	 *            the current arguments typed
	 * @return the options
	 */
	public abstract List<String> options( CommandSender sender,
			List<String> args );

	/**
	 * Execute the command
	 * 
	 * @param sender
	 *            the sender
	 * @param args
	 *            the arguments
	 * @return true if correctly typed. False will display info
	 */
	public abstract boolean run( CommandSender sender, List<String> args );

	public boolean execute( CommandSender sender, List<String> args ) {
		if ( isAllowedBy( sender ) ) {
			if ( run( sender, args ) )
				return true;
			else {
				if ( args.size() == 0 ) {
					// assume asking for help
					help( sender );
					return true;
				} else
					return false;
			}

		} else {
			denyPermission( sender );
			return true;
		}
	}

	public void denyPermission( CommandSender sender ) {
		Chat.sendMessage( getMessage( Text.DENY_PERMISSION, sender ) );
	}

	/**
	 * Sends help to the sender
	 * 
	 * @param sender
	 *            the player to help
	 */
	public void help( CommandSender sender ) {
		Chat.sendHeading( Text.HELP.getMessage( sender ), name( sender ) );
		Map<Message, Message> help = info( sender );
		Message temp = extra( sender );
		if ( temp != null ) {
			help.put( getMessage( Text.EXTRA, sender ), temp );
		}
		temp = example( sender );
		if ( temp != null )
			help.put( getMessage( Text.EXAMPLE, sender ), temp );
		Chat.sendMap( help );
	}

	public abstract Message extra( CommandSender sender);

	public abstract Message example( CommandSender sender );

	/**
	 * Some info about the command
	 * 
	 * @param sender
	 *            the sender
	 * @return the (colored) info
	 */
	public Map<Message, Message> info( CommandSender sender ) {
		Map<Message, Message> info = new LinkedHashMap<Message, Message>();
		info.put( getMessage( Text.DESCTIPTION, sender ),
				description( sender ) );
		info.put( getMessage( Text.USAGE, sender ), usage( sender ) );
		return info;
	}

	/**
	 * @param sender
	 *            the player asking for the description
	 * @param args
	 *            the arguments the player typed
	 * @return A sentence describing the command
	 */
	public abstract Message description( CommandSender sender );

	/**
	 * The command's syntax
	 * 
	 * @param sender
	 *            the sender
	 * @param args
	 *            the args the sender used
	 * @return How to use the command
	 */
	public abstract Message usage( CommandSender sender );

	/**
	 * @return the permission needed to run this command
	 */
	public abstract Permission permission();

	/**
	 * gets if the given sender has permission to run this command
	 * 
	 * @param sender
	 *            the sender
	 * @return true if they have permission
	 */
	public boolean isAllowedBy( CommandSender sender ) {
		return permission().isAllowedBy( sender );
	}

	/**
	 * short for path(" ");
	 * 
	 * @param sender
	 *            who is being messaged
	 */
	public String path( CommandSender sender ) {
		return path( " ", sender );
	}

	/**
	 * gets the path to the command
	 * 
	 * @param seperator
	 *            what to separate parent/child commands by
	 * @return the path
	 */
	private String path( String seperator, CommandSender sender ) {
		String path = "";
		if ( parent != null )
			path += parent.path( seperator, sender ) + seperator;
		path += name( sender ).toString();
		return path;
	}

	public void incorrectUsage( CommandSender sender, List<String> args ) {
		Message message = Text.INCORRECT_USAGE.getMessage( sender );
		message.put( "command", this );
		message.put( "attempt", Utils.listToString( args ) );
		Chat.sendMessage( message );
	}

	public Message getMessage( Text text, CommandSender sender ) {
		return text.getMessage( sender ).put( "command", this );
	}
	
	@Override
	public String format( Message message, String value ) {
		if ( value == null )
			value = "name";
		CommandSender sender = message.getSender();
		switch (value) {
		case "name":
			return name( sender ).toString();
		case "path":
			return path( sender );
		case "usage":
			return usage( sender ).toString();
		case "description":
			return description( sender ).toString();
		default:
			throw new RuntimeException( "Unknown value " + value );
		}
	}
}
