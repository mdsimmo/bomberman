package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class Autostart extends GameCommand {

	public Autostart(Cmd parent) {
		super(parent);
	}

	@Override
	public Phrase nameShort() {
		return Text.AUTOSTART_NAME;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 1) {
			List<String> options = new ArrayList<String>();
			options.add(Text.TRUE.getMessage(sender).toString());
			options.add(Text.FALSE.getMessage(sender).toString());
			return options;
		} else {
			return null;
		}
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;
		
		if (args.get(0).equalsIgnoreCase(Text.FALSE.getMessage(sender).toString())) {
			game.setAutostart(false);
			Chat.sendMessage(getMessage(Text.AUTOSTART_DISABLED, sender).put( "game", game));
		} else if (args.get(0).equalsIgnoreCase(Text.TRUE.getMessage(sender).toString())) {
			game.setAutostart(true);
			Chat.sendMessage(getMessage(Text.AUTOSTART_ENABLED, sender).put( "game", game));
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
	public Phrase extraShort() {
		return Text.AUTOSTART_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.AUTOSTART_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.AUTOSTART_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.AUTOSTART_USAGE;
	}

}
