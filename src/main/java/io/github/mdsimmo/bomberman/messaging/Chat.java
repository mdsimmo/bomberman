package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd.Permission;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public abstract class Chat {

	public static void sendMessage( PlayerRep rep, Message message ) {
		sendMessage( rep.getPlayer(), message );
	}

	public static void sendMessage( CommandSender sender, Message message ) {
		if ( message.isBlank() )
			return;
		messageRaw(
				sender,
				Text.MESSAGE_FORMAT.getMessage( sender )
						.put( "message", message ).toString() );
	}

	public static void sendMap( CommandSender sender,
			Map<Message, Message> points ) {
		for ( Map.Entry<Message, Message> point : points.entrySet() ) {
			if ( point.getValue().isBlank() || point.getKey().isBlank() )
				continue;
			messageRaw(
					sender,
					Text.MAP_FORMAT.getMessage( sender )
							.put( "title", point.getKey() )
							.put( "value", point.getValue() ).toString() );
		}
	}

	public static void sendList( CommandSender sender, List<Message> list ) {
		for ( Message line : list ) {
			if ( line.isBlank() )
				continue;
			messageRaw( sender,
					Text.LIST_FORMAT.getMessage( sender ).put( "value", line )
							.toString() );
		}
	}

	private static void messageRaw( CommandSender sender, String message ) {
		if ( Permission.OBSERVER.isAllowedBy( sender ) )
			sender.sendMessage( message );
	}

	public static void sendHeading( CommandSender sender, Message type,
			Message title ) {
		if ( title == null || title.isBlank() || type == null || type.isBlank() )
			return;
		messageRaw( sender,
				Text.HEADING_FORMAT.getMessage( sender ).put( "type", type )
						.put( "title", title ).toString() );
	}
}