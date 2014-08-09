package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.CommandGroup;

public class Set extends CommandGroup {

	public Set(Command parent) {
		super(parent);
	}

	@Override
	public String description() {
		return "Change a game's settings";
	}

	@Override
	public void setChildren() {
		addChildren(
				new Arena(this),
				new Autostart(this),
				new AutostartDelay(this),
				new Bombs(this),
				new Fare(this),
				new Handicap(this),
				new Lives(this),
				new MinPlayers(this),
				new Power(this),
				new Prize(this)
			);
	}

	@Override
	public String name() {
		return "set";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
