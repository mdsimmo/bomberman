package io.github.mdsimmo.bomberman.commands.game.force;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.Utils;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Stop extends GameCommand{

	public Stop(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "stop";
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
            return false;
		
        if (!game.isPlaying)
            Bomberman.sendMessage(sender, "Game %g hasn't started", game);
        else {
        	game.stop();
            if (!game.players.contains(sender))
            	Bomberman.sendMessage(sender, "Game %g stopped", game);
            Bomberman.sendMessage(game.players, "Game %g stopped", game);
        }
        return true;
	}

	@Override
	public String description() {
		return "Forcibly stop a game";
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
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public String example(CommandSender sender, List<String> args) {
		String game = Utils.random(Game.allGames());
		if (game == null)
			game = "mygame";
		return "/" + path() + game;
	}

}
