package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Scores extends GameCommand {

	public Scores(Command parent) {
		super(parent);
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		Chat.sendList(sender, game.scoreDisplay());
		return false;
	}

	@Override
	public Text name() {
		return Text.SCORES_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}
	
	@Override
	public Text extraShort() {
		return Text.SCORES_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.SCORES_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.SCORES_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.SCORES_USAGE;
	}
}
