package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ignore extends GameCommand {

	public Ignore(Command parent) {
		super(parent);
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
			return false;
		
		if (sender instanceof Player == false) {
			Bomberman.sendMessage(sender, "You must be  player");
			return true;
		}
		
		if (game.observers.remove(PlayerRep.getPlayerRep((Player)sender))) {
			Bomberman.sendMessage(sender, "Game %g ignored", game);
		} else {
			Bomberman.sendMessage(sender, "You where not observing %g", game);
		}
		
		return true;
	}

	@Override
	public String name() {
		return "ignore";
	}

	@Override
	public String description() {
		return "ignore all further messages from a game";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game>";
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

}
