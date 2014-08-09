package io.github.mdsimmo.bomberman.commands.game.force;

import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.CommandGroup;

public class Force extends CommandGroup {

	public Force(Command parent) {
		super(parent);
	}

	@Override
	public String description() {
		return "Force actions on a game";
	}

	@Override
	public void setChildren() {
		addChildren(
				new Reset(this),
				new Start(this),
				new Stop(this)
			);
	}

	@Override
	public String name() {
		return "force";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

}
