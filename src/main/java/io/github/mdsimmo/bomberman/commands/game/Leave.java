package io.github.mdsimmo.bomberman.commands.game;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;

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
		if (sender instanceof Player) {
            for (String name : Game.allGames()) {
                Game game = Game.findGame(name);
                PlayerRep rep = game.getPlayerRep((Player)sender);
                if (rep != null) {
                    rep.kill(true);
                    rep.game.observers.remove(rep);
                    return true;
                }
            }
        }
        Bomberman.sendMessage(sender, "You're not part of a game");
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
