package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public class GameList extends Cmd {

	public GameList( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.GAMELIST_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() != 0 )
			return false;
		List<String> games = Game.allGames();
		if ( games.size() == 0 ) {
			Chat.sendMessage( getMessage( Text.GAMELIST_NO_GAMES, sender ) );
		} else {
			Chat.sendHeading( Text.LIST.getMessage( sender ), Text.GAME.getMessage( sender ) );
			Map<Message, Message> list = new LinkedHashMap<>();
			for ( String name : games ) {
				Game game = Game.findGame( name );
				Message key = getMessage( Text.GAMELIST_KEY, sender ).put( "game", game );
				Message value = getMessage( Text.GAMELIST_VALUE, sender ).put( "game", game );
				list.put( key, value );
			}
			Chat.sendMap( list );
		}
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public Message example( CommandSender sender ) {
		return getMessage( Text.GAMELIST_EXAMPLE, sender );
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.GAMELIST_EXTRA, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.GAMELIST_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.GAMELIST_USAGE, sender );
	}

}
