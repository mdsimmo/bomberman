package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

import java.util.List;

import org.bukkit.command.CommandSender;

public class GameList extends Command {

	public GameList(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "list";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		 List<String> games = Game.allGames();
	        if (games.size() == 0) {
	            Bomberman.sendMessage(sender, "No games");
	        } else {
	            Bomberman.sendMessage(sender, "Current games:");
	            for (String name : games) {
	                Game game = Game.findGame(name);
	                String status = " * " + game.name;
	                status += " : " + game.players.size() + "/" + game.board.spawnPoints.size() + " : ";
	                if (game.isPlaying)
	                    status += "playing";
	                else
	                    status += "waiting  ";
	                        
	                Bomberman.sendMessage(sender, status);
	            }
	        }
	        return true;
	}

	@Override
	public String description() {
		return "Show all existing games";
	}

	@Override
	public String usage() {
		return "/" + path();
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

}
