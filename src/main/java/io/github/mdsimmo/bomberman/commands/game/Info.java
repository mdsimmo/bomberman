package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public class Info extends GameCommand {

	public Info( Cmd parent ) {
		super( parent );
	}

	@Override
	public Phrase nameShort() {
		return Text.INFO_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public List<String> shortOptions( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean runShort( CommandSender sender, List<String> args, Game game ) {
		if ( args.size() != 0 )
			return false;

		Chat.sendHeading( Text.INFO.getMessage( sender ), new Message( sender, game.name ) );
		Map<Message, Message> list = new LinkedHashMap<>();
		add( list, game, sender, Text.INFO_STATUS, Text.INFO_STATUS_RESULT );
		add( list, game, sender, Text.INFO_PLAYERS, Text.INFO_PLAYERS_RESULT );
		add( list, game, sender, Text.INFO_MIN_PLAYERS, Text.INFO_MIN_PLAYERS_RESULT);
		add( list, game, sender, Text.INFO_MAX_PLAYERS, Text.INFO_MAX_PLAYERS_RESULT);
		add( list, game, sender, Text.INFO_INIT_BOMBS, Text.INFO_INIT_BOMBS_RESULT );
		add( list, game, sender, Text.INFO_INIT_LIVES, Text.INFO_INIT_LIVES_RESULT);
		add( list, game, sender, Text.INFO_INIT_POWER, Text.INFO_INIT_POWER_RESULT);
		add( list, game, sender, Text.INFO_FARE, Text.INFO_FARE_RESULT);
		add( list, game, sender, Text.INFO_PRIZE, Text.INFO_PRIZE_RESULT);
		add( list, game, sender, Text.INFO_SUDDENDEATH, Text.INFO_SUDDENDEATH_RESULT);
		add( list, game, sender, Text.INFO_TIMEOUT, Text.INFO_TIMEOUT_RESULT );
		add( list, game, sender, Text.INFO_ARENA, Text.INFO_ARENA_RESULT);
		Chat.sendMap( list );
		return true;
	}

	private void add( Map<Message, Message> map, Game game, CommandSender sender, Phrase a, Phrase b ) {
		Message messageA = getMessage( a, sender ).put( "game", game );
		Message messageB = getMessage( b, sender ).put( "game", game );
		map.put( messageA, messageB );
	}
	
	@Override
	public Phrase extraShort() {
		return Text.INFO_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.INFO_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.INFO_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.INFO_USAGE;
	}

}
