package io.github.mdsimmo.bomberman.commands.game.force;

import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.game.GameCommand;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Reset extends GameCommand {

	public Reset(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "reset";
	}

	@Override
	public String description() {
		return "Forcibly reset a game to its starting point";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
			return false;
		Bomberman.sendMessage(game.players, "Game resetting");
		game.stop();
		BoardGenerator.switchBoard(game.board, game.board, game.loc);
		Bomberman.sendMessage(sender, "Game %g reset", game);
		return true;
	}

	@Override
	public boolean firstIsGame(List<String> args) {
		return args.size() == 1;
	}

}
