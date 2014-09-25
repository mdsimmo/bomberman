package io.github.mdsimmo.bomberman.commands.game.set;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.utils.Utils;

public class SuddenDeath extends GameCommand {

	public SuddenDeath(Command parent) {
		super(parent);
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 1) {
			List<String> options = new ArrayList<>();
			options.add("off");
			return options;
		} else
			return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;
		
		String value = args.get(0).toLowerCase();
		
		if (value.equals("off")) {
			game.setSuddenDeath(-1);
			Bomberman.sendMessage(sender, "Sudden death removed for game %g", game);
		} else {
			try {
				int time = (int)Double.parseDouble(value);
				game.setSuddenDeath(time);
				Bomberman.sendMessage(sender, "Sudden death set to %d seconds", time);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String name() {
		return "suddendeath";
	}

	@Override
	public String description() {
		return "Sets when sudden death should happen";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game> <value>";
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
		return "/" + path() + game + "60";
	}

}
