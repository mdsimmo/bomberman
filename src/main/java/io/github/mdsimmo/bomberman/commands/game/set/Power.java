package io.github.mdsimmo.bomberman.commands.game.set;

import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

public class Power extends Command {

	public Power(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "power";
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
		if (args.size() != 2)
			return false;
		Game game = Game.findGame(args.get(0));
		if (game == null) {
			Bomberman.sendMessage(sender, "Game not found");
			return true;
		}
		int amount;
		try {
			amount = Integer.parseInt(args.get(1));
		} catch (Exception e) {
			return false;
		}
		game.setPower(amount);	
		Bomberman.sendMessage(sender, "Power set");
		return true;
	}

	@Override
	public String description() {
		return "Sets players' initial power";
	}

	@Override
	public String usage() {
		return "/" + path() + "<game> <amount>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
