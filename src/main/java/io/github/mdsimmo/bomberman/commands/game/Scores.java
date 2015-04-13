package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Scores extends GameCommand {

	public Scores(Cmd parent) {
		super(parent);
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		Chat.sendList(game.scoreDisplay( sender ));
		// TODO scores command
		return true;
	}

	@Override
	public Phrase nameShort() {
		return Text.SCORES_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}
	
	@Override
	public Phrase extraShort() {
		return Text.SCORES_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.SCORES_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.SCORES_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.SCORES_USAGE;
	}
}
