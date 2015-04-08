package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.commands.game.force.Force;
import io.github.mdsimmo.bomberman.commands.game.set.Set;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import org.bukkit.command.CommandSender;

public class Game extends CommandGroup {

	public Game(Cmd parent) {
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
				new Protect(this)
				//new Scores(this)
			);
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.GAME_NAME, sender );
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public Message description(CommandSender sender ) {
		return getMessage(Text.GAME_DESCRIPTION, sender);
	}
	
}
