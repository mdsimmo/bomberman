package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;

public class Delete extends Command {

	public Delete(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "delete";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1)
			return BoardGenerator.allBoards();
		else 
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 1)
			return false;
		String arena = args.get(0);
		File f = BoardGenerator.toFile(arena);
		if (!f.exists()) {
			Bomberman.sendMessage(sender, "Arena %b does not exist %g", arena);
			return true;
		}
		for (String name : Game.allGames()) {
			Game game = Game.findGame(name);
			if (game.board.name.equalsIgnoreCase(args.get(0)) || game.oldBoard.name.equalsIgnoreCase(args.get(0))) {
				Bomberman.sendMessage(sender, "Cannot delete arena since it is being used by %g", game);
				return true;
			}
		}
		BoardGenerator.remove(arena);
		if (f.delete())
			Bomberman.sendMessage(sender, "Arena %b successfully deleted", arena);
		else
			Bomberman.sendMessage(sender, "Trouble deleting file: " + f);
		return true;
	}
	
	@Override
	public String description() {
		return "Deletes an arena permanently. No games must be using the arena for this to work";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<arena>";
	}

	@Override
	public Permission permission() {
		return Permission.ARENA_EDITING;
	}

	
	
}