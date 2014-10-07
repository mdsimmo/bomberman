package io.github.mdsimmo.bomberman.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.mdsimmo.bomberman.commands.arena.Arena;
import io.github.mdsimmo.bomberman.commands.game.Game;

public class Bm extends CommandGroup {

	public Bm() {
		super(null);
	}

	@Override
	public String description() {
		return "Commands for Bomberman";
	}
	
	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.get(args.size()-1).equalsIgnoreCase("?")) {
			longHelp(sender, args);
			return true;
		}
		return super.run(sender, args);
	}

	@Override
	public void setChildren() {
		addChildren(
				new Game(this),
				new Arena(this),
				new Help(this)
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
