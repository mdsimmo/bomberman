package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.commands.Cmd.Permission;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public abstract class Chat {

	public static void sendMessage( Message message  ) {
		if ( message.isBlank() )
			return;
		messageRaw( Text.MESSAGE_FORMAT.getMessage( message.getSender() ).put( "message",
				message ) );
	}

	public static void sendMap( Map<Message, Message> points  ) {
		for ( Map.Entry<Message, Message> point : points.entrySet() ) {
			if ( point.getValue().isBlank() || point.getKey().isBlank() )
				continue;
			messageRaw( Text.MAP_FORMAT.getMessage( point.getKey().getSender() )
					.put( "title", point.getKey() )
					.put( "value", point.getValue() ) );
		}
	}

	public static void sendList( List<Message> list  ) {
		for ( Message line : list ) {
			if ( line.isBlank() )
				continue;
			messageRaw( Text.LIST_FORMAT.getMessage( line.getSender() ).put( "value",
					line ) );
		}
	}

	public static void messageRaw( Message message ) {
		CommandSender sender = message.getSender();
		if ( Permission.OBSERVER.isAllowedBy( sender ) )
			sender.sendMessage( message.toString() );
	}

	public static void sendHeading( Message type , Message title  ) {
		messageRaw( Text.HEADING_FORMAT.getMessage( title.getSender() ).put( "type", type )
				.put( "title", title ) );
	}
}