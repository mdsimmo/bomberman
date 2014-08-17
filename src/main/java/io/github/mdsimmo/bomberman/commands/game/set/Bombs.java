package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.game.GameCommand;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Bombs extends GameCommand {

	public Bombs(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "bombs";
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;
				
		int amount;
		try {
			amount = Integer.parseInt(args.get(0));
		} catch (Exception e) {
			return false;
		}
		game.setBombs(amount);	
		Bomberman.sendMessage(sender, "Bombs set to " + amount);
		return true;
	}

	@Override
	public boolean firstIsGame(List<String> args) {
		return args.size() == 2;
	}
	
	@Override
	public String description() {
		return "Sets players' initial bombs";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game> <amount>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
