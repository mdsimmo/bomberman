package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Fare extends GameCommand {

	public Fare(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "fare";
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 1 ) {
			List<String> options = new ArrayList<>();
			options.add("none");
			for (Material m : Material.values())
				options.add(m.toString());
			return options;
		} else
			return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() < 1 || args.size() > 2)
			return false;
		
		if (args.size() == 1 && args.get(0).equalsIgnoreCase("none")) {
			game.setFare(null);
			Bomberman.sendMessage(sender, "Fare removed");
			return false;
		} else if (args.size() == 2) {
			try {
				Material m = Material.getMaterial(args.get(0).toUpperCase());
				if (m == null) {
					Bomberman.sendMessage(sender, "Unknown material %i", args.get(0));
					displayHelp(sender, args);
					return true;
				}
				int amount = Integer.parseInt(args.get(1));
				game.setFare(new ItemStack(m, amount));
				Bomberman.sendMessage(sender, "Fare set to %i", game.getFare());
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public String description() {
		return "Change a game's fare";
	}

	@Override
	public String usage(CommandSender sender) {
		 String usage = "\n";
         usage += "   /" + path() + "<game> <material> <amount> \n";
         usage += "   /" + path() + "<game> none \n";
		return usage;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
