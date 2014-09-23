package io.github.mdsimmo.bomberman.commands.game.force;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.Utils;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Start extends GameCommand {

	public Start(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "start";
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}
	
	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
			return false;
		
		if (game.isPlaying)
			Bomberman.sendMessage(sender, "Game %g already started", game);
		else {
			if (game.startGame())
				Bomberman.sendMessage(sender, "Game %g starting...", game);
			else
				Bomberman.sendMessage(sender, "There needs to be %d players", game.getMinPlayers());
		}
		return true;
	}

	@Override
	public String description() {
		return "Forcibly start a game";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

	@Override
	public String example(CommandSender sender, List<String> args) {
		String game = Utils.random(Game.allGames());
		if (game == null)
			game = "mygame";
		return "/" + path() + game;
	}
}
