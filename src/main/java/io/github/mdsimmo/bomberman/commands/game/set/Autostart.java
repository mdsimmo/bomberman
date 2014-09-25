package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class Autostart extends GameCommand {

	public Autostart(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "autostart";
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 1) {
			List<String> options = new ArrayList<String>();
			options.add("false");
			options.add("true");
			return options;
		} else {
			return null;
		}
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;
		
		if (args.get(0).equalsIgnoreCase("false")) {
			game.setAutostart(false);
			Bomberman.sendMessage(sender, "Autostart disabled in game %g", game);
		} else if (args.get(0).equalsIgnoreCase("true")) {
			game.setAutostart(true);
			Bomberman.sendMessage(sender, "Autostart enabled in game %g", game);
		} else {
			return false;
		}
		return true;
	}
	
	@Override
	public String description() {
		return "Set if the game should autostart";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game> <true/false>";
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
		return "/" + path() + game + " true";
	}

}
