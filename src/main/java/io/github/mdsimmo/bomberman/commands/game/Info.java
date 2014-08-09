package io.github.mdsimmo.bomberman.commands.game;

import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

public class Info extends Command {

	public Info(Command parent) {
		super(parent);
	}

	@Override
	public String description() {
		return "Show information about a game";
	}

	@Override
	public String name() {
		return "info";
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
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
        
        Game game = Game.findGame(args.get(0));
        if (game == null) {
            Bomberman.sendMessage(sender, "Game not found");
            return true;
        }
        String message = "About " + game.name + ":\n";
        message += " * Status: ";
        if (game.isPlaying)
            message += "In progress\n";
        else
            message += "Waiting\n";
        message += " * Players: " + game.players.size() + "\n";
        message += " * Min players: " + game.getMinPlayers()+ "\n";
        message += " * Max players: " + game.board.spawnPoints.size() + "\n";
        message += " * Init bombs: " + game.getBombs() + "\n";
        message += " * Init lives: " + game.getLives() + "\n";
        message += " * Init power: " + game.getPower() + "\n";
        message += " * Autostart: " + game.getAutostart() + "\n";
        message += " * Entry fare: ";
        if (game.getFare() == null)
            message += "no fee \n";
        else
            message += game.getFare().getType() + " x" + game.getFare().getAmount() + "\n";
        message += " * Winner's prize: ";
        if (game.getPot() == true && game.getFare() != null)
            message += "Pot currently at " + game.getFare().getAmount()*game.players.size() + " " + game.getFare().getType() + "\n";
        else {
            if (game.getPrize() == null)
                message += "No prize \n";
            else
                message += game.getPrize().getAmount() + " " + game.getPrize().getType() + "\n";
        }
        message += " * Arena: " + game.board.name + "\n";
        Bomberman.sendMessage(sender, message);
        return true;
	}

	@Override
	public String usage() {
		return "/" + path() + "<game>";
	}


}
