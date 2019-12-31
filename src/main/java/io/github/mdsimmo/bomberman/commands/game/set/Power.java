package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Power extends GameCommand {

	public Power(Cmd parent) {
		super(parent);
	}

	@Override
	public Phrase nameShort() {
		return Text.POWER_NAME;
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
			if (amount < 0) {
				throw new Exception();
			}
		} catch (Exception e) {
			Chat.sendMessage(getMessage(Text.INVALID_NUMBER, sender).put( "number", args.get(0)));
			return true;
		}
		game.setPower(amount);	
		Chat.sendMessage(getMessage(Text.POWER_SET, sender).put( "game", game) );
		return true;
	}
	
	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Phrase extraShort() {
		return Text.POWER_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.POWER_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.POWER_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.POWERS_USAGE;
	}

}
