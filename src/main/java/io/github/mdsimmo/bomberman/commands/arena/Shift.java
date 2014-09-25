package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class Shift extends Command {

	public Shift(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "shift";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1)
			return BoardGenerator.allBoards();
		else if (args.size() == 2) {
			List<String> list = new ArrayList<>();
			list.add("set");
			list.add("add");
			return list;
		} else
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 5)
			return false;
		Board board = BoardGenerator.loadBoard(args.get(0));
		if (board == null) {
			Bomberman.sendMessage(sender, "Cannot find arena");
			return true;
		}
		int x, y, z;
		try {
			x = Integer.parseInt(args.get(2));
			y = Integer.parseInt(args.get(3));
			z = Integer.parseInt(args.get(4));
		} catch (NumberFormatException e) {
			Bomberman.sendMessage(sender, "Last three args need to be numbers");
			return false;
		}
		switch (args.get(1)) {
		case "set":
			board.setShift(x, y, z);
			break;
		case "add":
			board.addShift(x, y, z);
			break;
		default:
			Bomberman.sendMessage(sender, "Second arg must be 'set' or 'add'");
			return false;
		}
		BoardGenerator.saveBoard(board);
		return true;
	}

	@Override
	public String description() {
		return "Shifts where the arena will generate when a game is created";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<arena> <set/add> <x> <y> <z>";
	}

	@Override
	public Permission permission() {
		return Permission.ARENA_EDITING;
	}

	@Override
	public String example(CommandSender sender, List<String> args) {
		String arena = Utils.random(BoardGenerator.allBoards());
		if (arena == null)
			arena = "myarena";
		return "/" + path() + arena + " 0 10 0 (will shift upwards by 10 blocks)";
	}

}
