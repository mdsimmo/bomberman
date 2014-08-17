package io.github.mdsimmo.bomberman.commands.game.force;

import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Reset extends Command {

	public Reset(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "reset";
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
        Bomberman.sendMessage(game.players, "Game reseting");
        game.stop();
        BoardGenerator.switchBoard(game.board, game.board, game.loc);
        Bomberman.sendMessage(sender, "Game reset");
        return true;
	}

	@Override
	public String description() {
		return "Forcibly reset a game to its starting point";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

}
