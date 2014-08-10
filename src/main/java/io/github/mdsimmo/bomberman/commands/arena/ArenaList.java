package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class ArenaList extends Command {

	public ArenaList(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "list";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 0)
			return false;
		List<String> arenas = BoardGenerator.allBoards();
		if (arenas.size() == 0) {
			Bomberman.sendMessage(sender, "No arenas");
		} else {
			Bomberman.sendHeading(sender, "List: Arenas");
			List<String> list = new ArrayList<>();
			for (String name : arenas) {
				if (!name.endsWith(".old"))
					list.add(name);
			}
			Bomberman.sendMessage(sender, list);
		}
		return true;
	}

	@Override
	public String description() {
		return "List available arena types";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path();
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}
}
