package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public class Info extends GameCommand {

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
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
			return false;

		Bomberman.sendHeading(sender, "Info: " + game.name);
		Map<String, String> list = new LinkedHashMap<>();
		if (game.isPlaying)
			list.put("Status", "In progress");
		else
			list.put("Status", "Waiting");
		list.put("Players ", "" + game.players.size());
		list.put("Min players", "" + game.getMinPlayers());
		list.put("Max players", "" + game.board.spawnPoints.size());
		list.put("Init bombs", "" + game.getBombs());
		list.put("Init lives", "" + game.getLives());
		list.put("Init power", "" + game.getPower());
		list.put("Autostart", "" + game.getAutostart());
		if (game.getFare() == null)
			list.put("Entry fare", "no fee");
		else
			list.put("Entry fare", game.getFare().getType() + " x"
					+ game.getFare().getAmount());
		if (game.getPot() == true && game.getFare() != null)
			list.put("Prize", "Pot currently at " + game.getFare().getAmount()
					* game.players.size() + " " + game.getFare().getType());
		else {
			if (game.getPrize() == null)
				list.put("Prize", "No prize");
			else
				list.put("Prize", game.getPrize().getAmount() + " "
						+ game.getPrize().getType());
		}
		list.put("Sudden death", game.getSuddenDeath() == -1 ? "off" : game.getSuddenDeath() + " seconds");
		list.put("Game over", game.getTimeout() == -1 ? "off" : game.getTimeout() + " seconds");
		list.put("Arena", game.board.name);
		Bomberman.sendMessage(sender, list);
		return true;
	}
	
	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game>";
	}

}
