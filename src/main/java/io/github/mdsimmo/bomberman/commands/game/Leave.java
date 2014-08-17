package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leave extends Command {

	public Leave(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "leave";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 0)
			return false;
		if (sender instanceof Player) {
			PlayerRep rep = PlayerRep.getPlayerRep((Player) sender);
			if (!rep.kill()) {
				if (rep.getGamePlaying() == null)
					Bomberman.sendMessage(sender, "You're not part of a game");
				else
					Bomberman.sendMessage(sender, "Couldn't remove you");
			}
		} else {
			Bomberman.sendMessage(sender, "You must be a player");
		}
		return true;
	}

	@Override
	public String description() {
		return "Leave the game";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path();
	}

	@Override
	public Permission permission() {
		return Permission.PLAYER;
	}

}
