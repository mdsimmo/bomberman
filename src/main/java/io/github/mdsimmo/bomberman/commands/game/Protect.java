package io.github.mdsimmo.bomberman.commands.game;

import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

public class Protect extends Command {

	public Protect(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "protect";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1)
			return Game.allGames();
		else
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 2)
			return false;
		Game game = Game.findGame(args.get(0));
		if (game == null) {
			Bomberman.sendMessage(sender, "Game not found");
			return true;
		}
		
		boolean enable;
		try {
			enable = Boolean.parseBoolean(args.get(1));
		} catch (Exception e) {
			return false;
		}
		game.setProteced(enable);
		if (enable)
			Bomberman.sendMessage(sender, "Game protected");
		else
			Bomberman.sendMessage(sender, "Game un-protected");
		return true;
	}

	@Override
	public String description() {
		return "protects the arena from griefing";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game> <true/false>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
