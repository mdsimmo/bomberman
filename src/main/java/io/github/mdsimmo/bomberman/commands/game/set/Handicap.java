package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.game.GameCommand;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Handicap extends GameCommand {

	public Handicap(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "handicap";
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 1) {
			List<String> options = new ArrayList<>();
			for (Player p : Bukkit.getServer().getOnlinePlayers())
				options.add(p.getName());
			return options;
		} else {
			return null;
		}
		
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 2)
			return false;
		
		@SuppressWarnings("deprecation")
		PlayerRep rep = PlayerRep.getPlayerRep(Bukkit.getPlayer(args.get(0)));
		if (rep == null) {
			Bomberman.sendMessage(sender, "Cannot find the player");
			return true;
		}
		int handicap = 0;
		try {
			handicap = Integer.parseInt(args.get(1));
		} catch (NumberFormatException e) {
			Bomberman.sendMessage(sender, "Invalid number");
		}
		game.setHandicap(rep, handicap);
		if (handicap > 0)
			Bomberman.sendMessage(sender, "Handicap set");
		else if (handicap == 0)
			Bomberman.sendMessage(sender, "Handicap removed");
		else
			Bomberman.sendMessage(sender, "Advantage added");
		
		if (rep.isPlaying() && !rep.getGamePlaying().isPlaying)
			game.initialise(rep);
		return true;
	}
	
	@Override
	public boolean firstIsGame(List<String> args) {
		return args.size() == 3;
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
