package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Destroy extends Command {

	public Destroy(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "destroy";
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
		Game game = Game.findGame(args.get(0));
		if (game != null) {
			game.destroy();
			Bomberman.sendMessage(game.observers, "Game %g destroyed", game);
		} else
			Bomberman.sendMessage(sender, "Game %g not found", args.get(0));
		return true;
	}

	@Override
	public String description() {
		return "Destroy a game and revert the land to its previous state.";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game>";
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
		return "/" + path() + game;
	}

}
