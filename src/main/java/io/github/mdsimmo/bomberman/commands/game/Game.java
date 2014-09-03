package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.commands.game.force.Force;
import io.github.mdsimmo.bomberman.commands.game.set.Set;

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
				new Protect(this)
			);
	}

	@Override
	public String name() {
		return "game";
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public String description() {
		return "All commands related to game configuation";
	}
	
}
