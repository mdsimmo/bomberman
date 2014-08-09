package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.CommandGroup;

public class Arena extends CommandGroup {

	public Arena(Command parent) {
		super(parent);
	}

	@Override
	public String description() {
		return "Arena management commands";
	}

	@Override
	public void setChildren() {
		addChildren(
				new ArenaList(this),
				new Create(this)
			);
	}

	@Override
	public String name() {
		return "arena";
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

}
