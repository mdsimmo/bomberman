package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Arena extends Command {

	public Arena(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "arena";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1)
			return Game.allGames();
		else if (args.size() == 2)
			return BoardGenerator.allBoards();
		else
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (!(args.size() == 1 || args.size() == 2))
            return false;
        Game game = Game.findGame(args.get(0));
        if (game == null) {
            Bomberman.sendMessage(sender, "Game not found");
            return true;
        }
        if (args.size() == 1) {
            Bomberman.sendMessage(sender, "Arena: " + game.board.name);
            return true;
        } else {
            if (game.isPlaying) {
                Bomberman.sendMessage(sender, "Game in progress");
                return true;
            }
                
            Board board = BoardGenerator.loadBoard(args.get(1));
            if (board == null) {
                Bomberman.sendMessage(sender, "Arena not found");
                return true;
            }
            BoardGenerator.switchBoard(game.board, game.oldBoard, game.loc);
            game.board = board;
            game.oldBoard = BoardGenerator.createArena(game.name+".old", game.loc, board.xSize, board.ySize, board.zSize);
            BoardGenerator.switchBoard(game.oldBoard, board, game.loc);
            Bomberman.sendMessage(sender, "Game arena changed");
            return true;
        }
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

}
