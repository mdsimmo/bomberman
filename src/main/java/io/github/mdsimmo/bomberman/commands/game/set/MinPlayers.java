package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class MinPlayers extends GameCommand {

	public MinPlayers(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.MINPLAYERS_NAME;
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
		game.setMinPlayers(amount);	
		Chat.sendMessage(sender, getMessage(Text.MINPLAYERS_SET, sender, game, amount));
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Text extraShort() {
		return Text.MINPLAYERS_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.MINPLAYERS_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.MINPLAYERS_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.MINPLAYERS_USAGE;
	}
}
