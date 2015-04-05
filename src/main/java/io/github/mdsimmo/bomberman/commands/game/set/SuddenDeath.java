package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class SuddenDeath extends GameCommand {

	public SuddenDeath(Cmd parent) {
		super(parent);
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 1) {
			List<String> options = new ArrayList<>();
			options.add(getMessage(Text.SUDDENDEATH_OFF, sender).toString());
			return options;
		} else
			return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;
		
		String value = args.get(0).toLowerCase();
		
		if (value.equals(getMessage(Text.SUDDENDEATH_OFF, sender).toString())) {
			game.setSuddenDeath(-1);
			Chat.sendMessage(sender, getMessage(Text.SUDDENDEATH_REMOVED, sender).put( "game", game));
		} else {
			try {
				int time = (int)Double.parseDouble(value);
				game.setSuddenDeath(time);
				Chat.sendMessage(sender, getMessage(Text.SUDDENDEATH_SET, sender).put( "game", game ));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Text nameShort() {
		return Text.SUDDENDEATH_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Text extraShort() {
		return Text.SUDDENDEATH_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.SUDDENDEATH_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.SUDDENDEATH_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.SUDDENDEATH_USAGE;
	}

}
