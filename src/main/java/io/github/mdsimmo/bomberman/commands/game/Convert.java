package io.github.mdsimmo.bomberman.commands.game;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;

public class Convert extends Command {

	public Convert(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "convert";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1)
			return Game.allGames();
		else
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 1)
			return false;
		if (sender instanceof Player) {
			if (Game.findGame(args.get(0)) != null) {
				Bomberman.sendMessage(sender, "Game already exists");
			} else {
				Location[] locations = BoardGenerator.getBoundingStructure(
						(Player) sender, args.get(0));
				Board board = BoardGenerator.createArena(args.get(0),
						locations[0], locations[1]);
				BoardGenerator.saveBoard(board);
				Game game = new Game(args.get(0), locations[0]);
				game.board = board;
				game.oldBoard = board;
				Game.register(game);
				PlayerRep.getPlayerRep((Player)sender).setGameActive(game);
				Bomberman.sendMessage(sender, "Game created");
			}
		} else {
			Bomberman.sendMessage(sender, "You must be a player");
		}
		return true;
	}

	@Override
	public String description() {
		return "Converts the structure under the cursor into a BomberMan game";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
