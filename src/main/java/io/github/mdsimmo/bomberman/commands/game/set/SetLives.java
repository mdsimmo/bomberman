package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.game.GameRegistry;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class SetLives extends GameCommand {

	public SetLives(Cmd parent) {
		super(parent);
	}

	@Override
	public Message name() {
		return context(Text.LIVES_NAME).format();
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
			if (amount <= 0)
				throw new Exception();
		} catch (Exception e) {
			context(Text.INVALID_NUMBER)
					.with("number", args.get(0))
					.sendTo(sender);
			return true;
		}
		game.getSettings().lives = amount;
		GameRegistry.saveGame(game);
		context(Text.LIVES_SET)
				.with("game", game)
				.sendTo(sender);
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Message extra() {
		return context(Text.LIVES_EXTRA).format();
	}

	@Override
	public Message example() {
		return context(Text.LIVES_EXAMPLE).format();
	}

	@Override
	public Message description() {
		return context(Text.LIVES_DESCRIPTION).format();
	}

	@Override
	public Message usage() {
		return context(Text.LIVES_USAGE).format();
	}

}
