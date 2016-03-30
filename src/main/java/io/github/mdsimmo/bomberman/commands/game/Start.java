package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.Game.State;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Start extends GameCommand {

	public Start(Cmd parent) {
		super(parent);
	}

	@Override
	public Phrase nameShort() {
		return Text.START_NAME;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}
	
	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
			return false;
		
		if ( game.state == State.PLAYING || game.state == State.ENDING )
			Chat.sendMessage(getMessage(Text.GAME_ALREADY_STARTED, sender).put( "game", game));
		else {
			if (game.startGame())
				Chat.sendMessage(getMessage(Text.GAME_START_SUCCESS, sender).put( "game", game));
			else
				Chat.sendMessage(getMessage(Text.GAME_MORE_PLAYERS, sender).put( "game", game));
		}
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

	@Override
	public Phrase extraShort() {
		return Text.START_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.START_EXAMPLE;		
	}

	@Override
	public Phrase descriptionShort() {
		return Text.START_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.START_USAGE;
	}
}
