package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class Timeout extends GameCommand {

	public Timeout(Cmd parent) {
		super(parent);
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 1) {
			List<String> options = new ArrayList<>();
			options.add(Text.TIMEOUT_OFF.getMessage(sender).toString());
			return options;
		} else
			return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;
		
		String value = args.get(0).toLowerCase();
		
		if (value.equals(Text.TIMEOUT_OFF.getMessage(sender).toString())) {
			game.setTimeout(-1);
			Chat.sendMessage(getMessage(Text.TIMEOUT_REMOVED, sender).put( "game", game));
		} else {
			try {
				int time = (int)Double.parseDouble(value);
				game.setTimeout(time);
				Chat.sendMessage(getMessage(Text.TIMEOUT_SET, sender).put( "game", game));
			} catch (Exception e) {
				Chat.sendMessage(getMessage(Text.INVALID_NUMBER, sender).put( "number", value));
			}
		}
		return true;
	}

	@Override
	public Phrase nameShort() {
		return Text.TIMEOUT_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Phrase extraShort() {
		return Text.TIMEOUT_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.TIMEOUT_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.TIMEOUT_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.TIMEOUT_USAGE;
	}

}
