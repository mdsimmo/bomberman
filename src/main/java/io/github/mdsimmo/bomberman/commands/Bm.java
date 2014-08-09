package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.commands.arena.Arena;
import io.github.mdsimmo.bomberman.commands.game.Game;

public class Bm extends CommandGroup {

	public Bm() {
		super(null);
	}

	@Override
	public String description() {
		return "Commands for BomberMan";
	}

	@Override
	public void setChildren() {
		addChildren(
				new Game(this),
				new Arena(this)
			);
	}

	@Override
	public String name() {
		return "bm";
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}
}
