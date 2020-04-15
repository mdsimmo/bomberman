package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class MinPlayers extends GameCommand {

	public MinPlayers(Cmd parent) {
		super(parent);
	}

	@Override
	public Message name() {
		return context(Text.MINPLAYERS_NAME).format();
	}

	@Override
	public List<String> gameOptions(List<String> args) {
		return null;
	}

	@Override
	public boolean gameRun(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;

		int amount;
		try {
			amount = Integer.parseInt(args.get(0));
		} catch (Exception e) {
			return false;
		}
		if ( amount <= 0 ) {
			context(Text.MINPLAYERS_LESS_THAN_ONE)
					.with("game", game )
					.with("amount", amount)
					.sendTo(sender);
			return true;
		}
		game.getSettings().minPlayers = amount;
		context(Text.MINPLAYERS_SET)
				.with("game", game )
				.sendTo(sender);
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Message extra() {
		return context(Text.MINPLAYERS_EXTRA).format();
	}

	@Override
	public Message example() {
		return context(Text.MINPLAYERS_EXAMPLE).format();
	}

	@Override
	public Message description() {
		return context(Text.MINPLAYERS_DESCRIPTION).format();
	}

	@Override
	public Message usage() {
		return context(Text.MINPLAYERS_USAGE).format();
	}
}
