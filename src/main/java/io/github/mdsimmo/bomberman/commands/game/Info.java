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
		add( list, game, sender, Text.INFO_1, Text.INFO_1_RESULT );
		add( list, game, sender, Text.INFO_2, Text.INFO_2_RESULT );
		add( list, game, sender, Text.INFO_3, Text.INFO_3_RESULT );
		add( list, game, sender, Text.INFO_4, Text.INFO_4_RESULT );
		add( list, game, sender, Text.INFO_5, Text.INFO_5_RESULT );
		add( list, game, sender, Text.INFO_6, Text.INFO_6_RESULT );
		add( list, game, sender, Text.INFO_7, Text.INFO_7_RESULT );
		add( list, game, sender, Text.INFO_8, Text.INFO_8_RESULT );
		add( list, game, sender, Text.INFO_9, Text.INFO_9_RESULT );
		add( list, game, sender, Text.INFO_10, Text.INFO_10_RESULT );
		add( list, game, sender, Text.INFO_11, Text.INFO_11_RESULT );
		add( list, game, sender, Text.INFO_12, Text.INFO_12_RESULT );
		add( list, game, sender, Text.INFO_13, Text.INFO_13_RESULT );
		add( list, game, sender, Text.INFO_14, Text.INFO_14_RESULT );
		add( list, game, sender, Text.INFO_15, Text.INFO_15_RESULT );
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
