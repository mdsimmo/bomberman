package io.github.mdsimmo.bomberman.commands.game.force;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Start extends GameCommand {

	public Start(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.START_NAME;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}
	
	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
			return false;
		
		if (game.isPlaying)
			Chat.sendMessage(sender, getMessage(Text.GAME_ALREADY_STARTED, sender, game));
		else {
			if (game.startGame())
				Chat.sendMessage(sender, getMessage(Text.GAME_START_SUCCESS, sender, game));
			else
				Chat.sendMessage(sender, getMessage(Text.GAME_MORE_PLAYERS, sender, game, game.getMinPlayers()));
		}
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

	@Override
	public Text extraShort() {
		return Text.START_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.START_EXAMPLE;		
	}

	@Override
	public Text descriptionShort() {
		return Text.START_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.START_USAGE;
	}
}
