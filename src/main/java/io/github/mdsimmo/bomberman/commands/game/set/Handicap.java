package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Handicap extends Command {

	public Handicap(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "handicap";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1)
			return Game.allGames();
		else if (args.size() == 2) {
			List<String> options = new ArrayList<>();
			for (Player p : Bukkit.getServer().getOnlinePlayers())
				options.add(p.getName());
			return options;
		} else {
			return null;
		}
		
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 3)
			return false;
		Game game = Game.findGame(args.get(0));
		if (game == null) {
			sender.sendMessage("cannot find game");
			return true;
		}
		@SuppressWarnings("deprecation")
		PlayerRep rep = game.getPlayerRep(Bukkit.getPlayer(args.get(1)));
		if (rep == null) {
			Bomberman.sendMessage(sender, "Cannot find the player (they must be joined into the game)");
			return true;
		}
		int handicap = 0;
		try {
			handicap = Integer.parseInt(args.get(2));
		} catch (NumberFormatException e) {
			return false;
		}
		rep.handicap = handicap;
		if (handicap > 0)
			Bomberman.sendMessage(sender, "Handicap set");
		else if (handicap == 0)
			Bomberman.sendMessage(sender, "Handicap removed");
		else
			Bomberman.sendMessage(sender, "Advantage added");
		game.initialise(rep);
		return true;
	}

	@Override
	public String description() {
		return "Gives a hanicap/advantage to a player";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game> <player> <level> (neg values for advantage)";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
