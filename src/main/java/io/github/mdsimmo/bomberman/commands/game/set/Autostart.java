package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class Autostart extends Command {

	public Autostart(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "autostart";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1)
			return Game.allGames();
		else if (args.size() == 2) {
			List<String> options = new ArrayList<String>();
			options.add("false");
			options.add("true");
			return options;
		} else {
			return null;
		}
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 2)
			return false;
		Game game = Game.findGame(args.get(0));
		if (game == null) {
			Bomberman.sendMessage(sender, "Game not found");
			return true;
		}
		if (args.get(1).equalsIgnoreCase("false")) {
			game.setAutostart(false);
		} else if (args.get(1).equalsIgnoreCase("true")) {
			game.setAutostart(true);
		} else {
			return false;
		}
		Bomberman.sendMessage(sender, "Autostart set");
		return true;
	}

	@Override
	public String description() {
		return "Set if the game should autostart";
	}

	@Override
	public String usage() {
		return "/" + path() + "<game> <true/false>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
