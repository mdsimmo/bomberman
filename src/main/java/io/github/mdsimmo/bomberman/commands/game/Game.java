package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.commands.game.force.Force;
import io.github.mdsimmo.bomberman.commands.game.set.Set;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Game extends CommandGroup {

	public Game(Command parent) {
		super(parent);
	}

	@Override
	public void setChildren() {
		addChildren(
				new Set(this),
				new Force(this),
				new Create(this),
				new Convert(this),
				new Destroy(this),
				new GameList(this),
				new Ignore(this),
				new Info(this),
				new Join(this),
				new Leave(this),
				new Protect(this),
				new Scores(this)
			);
	}

	@Override
	public Text name() {
		return Text.GAME_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return getMessage(Text.GAME_DESCRIPTION, sender);
	}
	
}
