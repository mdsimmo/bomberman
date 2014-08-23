package io.github.mdsimmo.bomberman.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;

public abstract class GameCommand extends Command {

	public GameCommand(Command parent) {
		super(parent);
	}

	public final List<String> options(CommandSender sender, List<String> args) {
		if (args.size() <= 1) {
			List<String> list = new ArrayList<>(Game.allGames());
			if (sender instanceof Player
					&& PlayerRep.getPlayerRep((Player) sender).getGameActive() != null) {
				List<String> options = shortOptions(sender, args);
				if (options != null)
					list.addAll(options);
			}
			return list;
		} else {
			args.remove(0);
			return shortOptions(sender, args);
		}
	}
	
	public abstract List<String> shortOptions (CommandSender sender, List<String> args);

	@Override
	public final boolean run(CommandSender sender, List<String> args) {
		Game game = null;
		if (firstIsGame(args)) {
			game = Game.findGame(args.get(0));
			if (game == null) {
				Bomberman.sendMessage(sender, "Game %g not found", args.get(0));
				return true;
			} else {
				if (sender instanceof Player)
					PlayerRep.getPlayerRep((Player) sender).setGameActive(game);
			}
			args.remove(0);
		} else {
			if (sender instanceof Player)
				game = PlayerRep.getPlayerRep((Player) sender).getGameActive();
			if (game == null)
				return false;
		}
		return runShort(sender, args, game);
	}
	
	public abstract boolean runShort(CommandSender sender, List<String> args, Game game);
		
	public abstract boolean firstIsGame(List<String> args);
}