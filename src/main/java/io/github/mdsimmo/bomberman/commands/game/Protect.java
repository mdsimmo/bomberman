package io.github.mdsimmo.bomberman.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;

public class Protect extends Command {

	public Protect(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "protect";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		List<String> list = new ArrayList<>();
		if (args.size() == 1)
			return Game.allGames();
		else if (args.size() == 2) {
			list.add("true");
			list.add("false");
			list.add("enabled");
			list.add("pvp");
			list.add("destroy");
			list.add("damage");
			list.add("fire");
			list.add("explosion");
			return list;
		} else if (args.size() == 3) {
			list.add("true");
			list.add("false");
			return list;
		} else 
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() < 2 || args.size() > 3)
			return false;
		Game game = Game.findGame(args.get(0));
		if (game == null) {
			Bomberman.sendMessage(sender, "Game not found");
			return true;
		}
		
		boolean enable;
		try {
			if (args.size() == 3)
				enable = Boolean.parseBoolean(args.get(2));
			else
				enable = Boolean.parseBoolean(args.get(1));
		} catch (NumberFormatException e) {
			return false;
		}
		
		if (args.size() == 2)
			game.setProteced(Config.PROTECT, enable);
		else {		
			switch (args.get(1).toLowerCase()) {
			case "enabled":
				game.setProteced(Config.PROTECT, enable); break;
			case "pvp":
				game.setProteced(Config.PROTECT_PVP, enable); break;
			case "placing":
				game.setProteced(Config.PROTECT_PLACING, enable); break;
			case "destoy":
				game.setProteced(Config.PROTECT_DESTROYING, enable); break;
			case "damage":
				game.setProteced(Config.PROTECT_DAMAGE, enable); break;
			case "fire":
				game.setProteced(Config.PROTECT_FIRE, enable); break;
			case "explosion":
				game.setProteced(Config.PROTECT_EXPLOSIONS, enable); break;
			default:
				return false;
			}
		}
		
		if (enable)
			Bomberman.sendMessage(sender, "Game protected");
		else
			Bomberman.sendMessage(sender, "Game un-protected");
		return true;
	}

	@Override
	public String description() {
		return "protects the arena from griefing";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/n" + path() + "<game> <true/false>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
