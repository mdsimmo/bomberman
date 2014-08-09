package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Prize extends Command {

	public Prize(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "prize";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1) {
			return Game.allGames();
		} else if (args.size() == 1 ) {
			List<String> options = new ArrayList<>();
			options.add("none");
			options.add("pot");
			for (Material m : Material.values())
				options.add(m.toString());
			return options;
		} else
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() < 2 || args.size() > 3)
			return false;
		Game game = Game.findGame(args.get(0));
		if (game == null) {
			Bomberman.sendMessage(sender, "Game not found");
			return true;
		}
		if (args.size() == 2) {
			if (args.get(1).equalsIgnoreCase("none")) {
				game.setFare(null);
				Bomberman.sendMessage(sender, "Prize removed");
			} else if (args.get(1).equalsIgnoreCase("pot")) {
				game.setPot(true);
				Bomberman.sendMessage(sender, "Pot set");
			} else
				return false;
		} else if (args.size() == 3) {
			try {
				Material m = Material.getMaterial(args.get(1).toUpperCase());
				if (m == null) {
					Bomberman.sendMessage(sender, "Unknown material");
					return true;
				}
				int amount = Integer.parseInt(args.get(2));
				game.setFare(new ItemStack(m, amount));
				Bomberman.sendMessage(sender, "Prize set");
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public String description() {
		return "Change a game's prize";
	}

	@Override
	public String usage() {
		 String usage = "\n";
         usage += "   /" + path() + "<game> <material> <amount> \n";
         usage += "   /" + path() + "<game> none \n";
         usage += "   /" + path() + "<game> pot \n";
		return usage;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
