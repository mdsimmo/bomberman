package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Join extends GameCommand {

	public Join(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "join";
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
			Bomberman.sendMessage(sender, "You must be a player to join");
			return true;
		}	
		if (game.isPlaying) {
			Bomberman.sendMessage(sender, "Game has already started");
			return true;
		}
		PlayerRep rep = PlayerRep.getPlayerRep((Player) sender);
		rep.setGameActive(game);
		rep.joinGame();
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

	@Override
	public String example(CommandSender sender, List<String> args) {
		String game = Utils.random(Game.allGames());
		if (game == null)
			game = "mygame";
		return "/" + path() + game;
	}

}
