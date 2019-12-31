package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.arena.ArenaGenerator;
import io.github.mdsimmo.bomberman.arena.ArenaGenerator.BuildListener;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Reset extends GameCommand {

	public Reset( Cmd parent ) {
		super( parent );
	}

	@Override
	public Phrase nameShort() {
		return Text.RESET_NAME;
	}

	@Override
	public List<String> shortOptions( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean runShort( final CommandSender sender, List<String> args,
			final Game game ) {
		if ( args.size() != 0 )
			return false;

		Chat.sendMessage( getMessage( Text.RESET_STARTED, sender ).put( "game",
				game ) );

		game.stop();
		ArenaGenerator.switchBoard(game.getArena(), game.getArena(), game.getBox(),
				new BuildListener() {
					@Override
					public void onContructionComplete() {
						Chat.sendMessage( getMessage( Text.RESET_FINISHED,
								sender ).put( "game", game ) );
					}
				} );

		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

	@Override
	public Phrase extraShort() {
		return Text.RESET_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.RESET_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.RESET_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.RESET_USAGE;
	}
}
