package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;

public class GameList extends Command {

	public GameList(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "list";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 0)
			return false;
		List<String> games = Game.allGames();
		if (games.size() == 0) {
			Bomberman.sendMessage(sender, "No games");
		} else {
			Bomberman.sendHeading(sender, "List: Games");
			Map<String, String> list = new TreeMap<>();
			for (String name : games) {
				Game game = Game.findGame(name);
				String status = game.players.size() + "/"
						+ game.board.spawnPoints.size() + " : ";
				if (game.isPlaying)
					status += "playing";
				else
					status += "waiting  ";
				list.put(game.name, status);
			}
			Bomberman.sendMessage(sender, list);
		}
		return true;
	}

	@Override
	public String description() {
		return "Show all existing games";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path();
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

}
