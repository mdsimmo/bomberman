package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Create extends Command {

	public Create(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "create";
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
        if (sender instanceof Player) {
            if (Game.findGame(args.get(0)) != null) {
                Bomberman.sendMessage(sender, "Game already exists");
            } else {
                Board arena;
                if (args.size() == 2) {
                    arena = BoardGenerator.loadBoard(args.get(1));
                } else {
                    arena = BoardGenerator.loadDefault();
                }
                if (arena == null) {
                    Bomberman.sendMessage(sender, "Arena not found");
                    return true;
                }
                // long location getting line to round to integers...
                Location l = ((Player)sender).getLocation().getBlock().getLocation();
                createGame(args.get(0), l, arena);
                Bomberman.sendMessage(sender, "Game created");
            }
        } else {
            Bomberman.sendMessage(sender, "You must be a player");
        }
        return true;
	}
	
	private void createGame(String name, Location l, Board arena) {
		Game game = new Game(name, l);
		game.board = arena;
		game.oldBoard = BoardGenerator.createArena(name+".old", game.loc, game.board.xSize, game.board.ySize, game.board.zSize);
		BoardGenerator.switchBoard(game.oldBoard, game.board, game.loc);
		Game.register(game);
	}

	@Override
	public String description() {
		return "Generate a BomberMan game.";
	}

	@Override
	public String usage() {
		return "/" + path() + "<game> [arena]";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}
	
}
