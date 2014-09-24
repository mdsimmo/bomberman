package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.Utils;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Arena extends GameCommand {

	public Arena(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "arena";
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 0)
			return BoardGenerator.allBoards();
		else
			return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;
		
		if (game.isPlaying) {
			Bomberman.sendMessage(sender, "Game %g in progress. Cannot change arena", game);
			return true;
		}

		Board board = BoardGenerator.loadBoard(args.get(0));
		if (board == null) {
			Bomberman.sendMessage(sender, "Arena %b not found", board);
			return true;
		}
		BoardGenerator.switchBoard(game.board, game.oldBoard, game.loc);
		game.board = board;
		game.oldBoard = BoardGenerator.createArena(game.name + ".old",
				game.loc, board.xSize, board.ySize, board.zSize);
		BoardGenerator.switchBoard(game.oldBoard, board, game.loc);
		Bomberman.sendMessage(sender, "Game %g arena's changed", game);
		return true;
	}

	@Override
	public String description() {
		return "Change a game's arena";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game> <arena>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public String example(CommandSender sender, List<String> args) {
		String game = Utils.random(Game.allGames());
		if (game == null)
			game = "mygame";
		String arena = Utils.random(BoardGenerator.allBoards());
		if (arena == null)
			arena = "myarena";
		return "/" + path() + game + ' ' + arena;
	}

}
