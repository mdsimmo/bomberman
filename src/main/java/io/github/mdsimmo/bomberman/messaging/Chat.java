package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd.Permission;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public abstract class Chat {

	public static void sendMessage( Message message  ) {
		if ( message.isBlank() )
			return;
		messageRaw( getMessage( Text.MESSAGE_FORMAT, message.getSender() ).put( "message",
				message ) );
	}

	public static void sendMap( Map<Message, Message> points  ) {
		for ( Map.Entry<Message, Message> point : points.entrySet() ) {
			if ( point.getValue().isBlank() || point.getKey().isBlank() )
				continue;
			messageRaw( getMessage( Text.MAP_FORMAT, point.getKey().getSender() )
					.put( "title", point.getKey() )
					.put( "value", point.getValue() ) );
		}
	}

	public static void sendList( List<Message> list  ) {
		for ( Message line : list ) {
			if ( line.isBlank() )
				continue;
			messageRaw( getMessage(  Text.LIST_FORMAT, line.getSender() ).put( "value",
					line ) );
		}
	}

	public static void messageRaw( Message message ) {
		CommandSender sender = message.getSender();
		if ( Permission.OBSERVER.isAllowedBy( sender ) )
			sender.sendMessage( message.toString() );
	}

	public static void sendHeading( Message type , Message title  ) {
		messageRaw( getMessage( Text.HEADING_FORMAT, title.getSender() ).put( "type", type )
				.put( "title", title ) );
	}
	
	public static Message getMessage( Phrase phrase, PlayerRep rep ) {
		return getMessage( phrase, rep.getLanguage(), rep.getPlayer() );
	}

	public static Message getMessage( Phrase phrase, Language lang, CommandSender sender ) {
		if ( lang == null )
			return new Message( sender, phrase.getDefault() );
		else
			return new Message( sender, lang.translate( phrase ) );
	}

	public static Message getMessage( Phrase phrase, CommandSender sender ) {
		return getMessage( phrase, PlayerRep.getLanguage( sender ), sender );
	}
}