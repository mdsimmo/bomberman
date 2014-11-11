package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

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
	public Text name() {
		return Text.PRIZE_NAME;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 1 ) {
			List<String> options = new ArrayList<>();
			options.add(Text.PRIZE_NONE.getMessage(sender).toString());
			options.add(Text.PRIZE_POT.getMessage(sender).toString());
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
			if (args.get(0).equalsIgnoreCase(Text.PRIZE_NONE.getMessage(sender).toString())) {
				game.setFare(null);
				Chat.sendMessage(sender, getMessage(Text.PRIZE_REMOVED, sender, game));
			} else if (args.get(0).equalsIgnoreCase(Text.PRIZE_POT.getMessage(sender).toString())) {
				game.setPot(true);
				Chat.sendMessage(sender, getMessage(Text.PRIZE_POT_SET, sender, game, game.getFare()));
			} else
				return false;
		} else if (args.size() == 2) {
			Material m = Material.getMaterial(args.get(0).toUpperCase());
			if (m == null) {
				Chat.sendMessage(sender, getMessage(Text.INVALID_MATERIAL, sender, args.get(0)));
				return true;
			}
			try {
				int amount = Integer.parseInt(args.get(1));
				game.setPrize(new ItemStack(m, amount));
				Chat.sendMessage(sender, getMessage(Text.PRIZE_SET, sender, game, game.getPrize()));
			} catch (Exception e) {
				Chat.sendMessage(sender, getMessage(Text.INVALID_NUMBER, sender, args.get(1)));
			}
		} else {
			return false;
		}
		return true;
	}
	
	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Text extraShort() {		
		return Text.PRIZE_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.PRIZE_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.PRIZE_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.PRIZE_USAGE;
	}
}
