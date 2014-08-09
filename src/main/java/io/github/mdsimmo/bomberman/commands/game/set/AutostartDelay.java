package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.List;

import org.bukkit.command.CommandSender;

public class AutostartDelay extends Command {

	public AutostartDelay(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "autostartdelay";
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
            Bomberman.sendMessage(sender, "Cannot find game");
            return true;
        }
        
        try {
            game.setAutostartDelay(Integer.parseInt(args.get(1)));
            Bomberman.sendMessage(sender, "Autostart delay set to " + game.getAutostartDelay());
        } catch (NumberFormatException e) {
            Bomberman.sendMessage(sender, "Delay entered is not a valid number");
        }
        return true;
	}

	@Override
	public String description() {
		return "Change the delay on a game's automated start";
	}

	@Override
	public String usage() {
		return "/" + path() + "<game> <amount>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
