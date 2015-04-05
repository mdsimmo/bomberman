package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Fare extends GameCommand {

	public Fare(Cmd parent) {
		super(parent);
	}

	@Override
	public Text nameShort() {
		return Text.FARE_NAME;
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
		
		String none = Text.FARE_NONE.getMessage(sender).toString();
		if (args.size() == 1 && args.get(0).equalsIgnoreCase(none)) {
			game.setFare(null);
			Chat.sendMessage(sender, getMessage(Text.FARE_REMOVED, sender).put( "game", game));
			return true;
		} else if (args.size() == 2) {
			Material m = Material.getMaterial(args.get(0).toUpperCase());
			if (m == null) {
				Chat.sendMessage(sender, getMessage(Text.INVALID_MATERIAL, sender).put( "material", args.get(0)));
				return true;
			}
			try {
				int amount = Integer.parseInt(args.get(1));
				game.setFare(new ItemStack(m, amount));
				Chat.sendMessage(sender, getMessage(Text.FARE_SET, sender).put( "game", game));
				return true;
			} catch (Exception e) {
				Chat.sendMessage(sender, getMessage(Text.INVALID_NUMBER, sender).put( "number", args.get(1)));
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Text extraShort() {
		return Text.FARE_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.FARE_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.FARE_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.FARE_USAGE;
	}

}
