package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Join extends Command {

	public Join(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "join";
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
		if (args.size() != 1)
			return false;
		if (sender instanceof Player) {
			Game game = Game.findGame(args.get(0));
			if (game == null) {
				Bomberman.sendMessage(sender, "Game not found");
			} else {
				if (game.isPlaying == false) {
					PlayerRep rep = PlayerRep.getPlayerRep((Player) sender);
					rep.setGameActive(game);
					if (!rep.joinGame())
						Bomberman.sendMessage(sender, "couldn't join the game");;
				} else {
					Bomberman.sendMessage(sender, "Game has already started");
				}
			}
		} else {
			Bomberman.sendMessage(sender, "You must be a player to join");
		}
		return true;
	}

	@Override
	public String description() {
		return "join a game";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game>";
	}

	@Override
	public Permission permission() {
		return Permission.PLAYER;
	}

}
