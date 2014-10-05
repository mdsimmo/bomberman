package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.utils.Box;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Create extends Command {

	public Create(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "create";
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
        if (sender instanceof Player) {
            Box box = BoardGenerator.getBoundingStructure((Player)sender);
            if (box == null) {
            	Bomberman.sendMessage(sender, "Max build size exceeded! %d blocks maximum", Config.MAX_STRUCTURE.getValue());
            	return true;
            }
            if (box.xSize < 2 && box.ySize < 2 && box.zSize < 2) {
            	Bomberman.sendMessage(sender, "Structure is only a single block! Were you looking at a natural block?");
            }
            Board board2 = BoardGenerator.createArena(args.get(0), box);
            BoardGenerator.saveBoard(board2);
            Bomberman.sendMessage(sender, "Arena created");
        }
        return true;
	}

	@Override
	public String description() {
		return "Create a new arena type for games to use";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<arena> (look at the arena when using)" ;
	}

	@Override
	public Permission permission() {
		return Permission.ARENA_EDITING;
	}

	@Override
	public String example(CommandSender sender, List<String> args) {
		return "/" + path() + "redPuddingArena";
	}
	
	@Override
	public String extra(CommandSender sender, List<String> args) {
		return "Natural blocks are ignored when detecting structures";
	}

}
