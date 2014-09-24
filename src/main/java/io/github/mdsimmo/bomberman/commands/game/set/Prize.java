package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Prize extends GameCommand {

	public Prize(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "prize";
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 1 ) {
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
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() < 1 || args.size() > 2)
			return false;

		if (args.size() == 1) {
			if (args.get(0).equalsIgnoreCase("none")) {
				game.setFare(null);
				Bomberman.sendMessage(sender, "Prize removed");
			} else if (args.get(0).equalsIgnoreCase("pot")) {
				game.setPot(true);
				Bomberman.sendMessage(sender, "Pot set");
			} else
				return false;
		} else if (args.size() == 2) {
			try {
				Material m = Material.getMaterial(args.get(0).toUpperCase());
				if (m == null) {
					Bomberman.sendMessage(sender, "Unknown material");
					return true;
				}
				int amount = Integer.parseInt(args.get(1));
				game.setPrize(new ItemStack(m, amount));
				Bomberman.sendMessage(sender, "Prize set to %i", game.getPrize());
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
	public String usage(CommandSender sender) {
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

	@Override
	public String example(CommandSender sender, List<String> args) {
		String game = Utils.random(Game.allGames());
		if (game == null)
			game = "mygame";
		return "/" + path() + game + "pot";
	}

}
