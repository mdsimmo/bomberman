package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Info extends GameCommand {

	public Info( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name() {
		return Text.INFO_NAME.format();
	}

	@Override
	public Permission permission() {
		return Permission.PLAYER;
	}

	@Override
	public List<String> gameOptions(List<String> args ) {
		return null;
	}

	@Override
	public boolean gameRun(CommandSender sender, List<String> args, Game game ) {
		if ( args.size() != 0 )
			return false;

		context(Text.INFO_DETAILS).with("game", game).sendTo(sender);
		return true;
	}
	
	@Override
	public Message extra() {
		return context(Text.INFO_EXTRA).format();
	}

	@Override
	public Message example() {
		return context(Text.INFO_EXAMPLE).format();
	}

	@Override
	public Message description() {
		return context(Text.INFO_DESCRIPTION).format();
	}

	@Override
	public Message usage() {
		return context(Text.INFO_USAGE).format();
	}

}
