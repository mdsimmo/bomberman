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
	
	/**
	 * Converts a phrase into being a Message which is in the player's language
	 * @param phrase the phrase to convert
	 * @param rep the {@link PlayerRep} receiving the message
	 * @return The created message
	 */
	public static Message getMessage( Phrase phrase, PlayerRep rep ) {
		return getMessage( phrase, rep.getLanguage(), rep.getPlayer() );
	}

	/**
	 * Converts a phrase into the given {@link Language} 
	 * @param phrase the phrase to convert
	 * @param lang the {@link Language} to convert into. Should be the language of {@code sender}.
	 * 			null for the default English.
	 * @param sender the {@link CommandSender} receiving to message
	 * @return the created message
	 */
	public static Message getMessage( Phrase phrase, Language lang, CommandSender sender ) {
		if ( lang == null )
			lang = Language.getLanguage( "english" );
		return new Message( sender, lang.translate( phrase ) );
	}
	
	/**
	 * Converts a phrase into the language spoken by {@code sender} 
	 * @param phrase the phrase to convert
	 * @param sender the {@link CommandSender} receiving to message
	 * @return the created message
	 */
	public static Message getMessage( Phrase phrase, CommandSender sender ) {
		return getMessage( phrase, PlayerRep.getLanguage( sender ), sender );
	}
}