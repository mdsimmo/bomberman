package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public abstract class CommandGroup extends Cmd {

	List<Cmd> children = new ArrayList<>();

	/**
	 * Adds some child commands
	 * 
	 * @param children
	 *            the child commands
	 */
	public void addChildren( Cmd... children ) {
		this.children.addAll( Arrays.asList( children ) );
	}

	public CommandGroup( Cmd parent ) {
		super( parent );
		setChildren();
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		if ( args.size() != 1 )
			return null;
		List<String> options = new ArrayList<>();
		for ( Cmd c : children ) {
			if ( c.isAllowedBy( sender ) )
				options.add( c.name( sender ).toString() );
		}
		return options;
	}

	@Override
	public Message extra( CommandSender sender ) {
		return null;
	}

	@Override
	public Message example( CommandSender sender ) {
		return null;
	}

	@Override
	public Map<Message, Message> info( CommandSender sender ) {
		Map<Message, Message> info = new LinkedHashMap<Message, Message>();
		info.put( getMessage( Text.DESCTIPTION, sender ), description( sender ) );
		info.put( getMessage( Text.COMMANDS, sender ), usage( sender ) );
		return info;
	}

	@Override
	public Message usage( CommandSender sender ) {
		String usage = "\n";
		for ( Cmd c : children ) {
			if ( !c.isAllowedBy( sender ) )
				continue;

			if ( c instanceof CommandGroup )
				usage += "    " + c.name( sender ) + " [...]\n";
			else
				usage += "    " + c.name( sender ) + "\n";
		}
		return new Message( sender, usage );
	}

	/**
	 * sets what children this group has
	 */
	public abstract void setChildren();

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() == 0 ) {
			help( sender );
			return true;
		} else {
			for ( Cmd c : children ) {
				if ( c.name( sender ).toString()
						.equalsIgnoreCase( args.get( 0 ) ) ) {
					args.remove( 0 );
					return c.execute( sender, args );
				}
			}
			Chat.sendMessage( sender, getMessage( Text.UNKNOWN_COMMAND, sender )
					.put( "attempt", args.get( 0 ) ) );
			help( sender );
			return true;
		}
	}

	public Cmd getCommand( CommandSender sender, List<String> args ) {
		System.out.println( "size " + args.size() + args );
		if ( args.size() == 0 )
			return this;
		for ( Cmd cmd : children ) {
			if ( cmd.name( sender ).toString().equals( args.get( 0 ) )
					&& cmd.isAllowedBy( sender ) ) {
				args.remove( 0 );
				if ( cmd instanceof CommandGroup )
					return ( (CommandGroup)cmd ).getCommand( sender, args );
				else
					return cmd;
			}
		}
		return this;
	}

}
