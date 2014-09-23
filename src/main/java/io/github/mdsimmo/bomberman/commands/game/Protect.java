package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Protect extends GameCommand {

	private static BiMap<Config, String> options = HashBiMap.create();
	
	public Protect(Command parent) {
		super(parent);
		options.put(Config.PROTECT, "enabled");
		options.put(Config.PROTECT_PLACING, "placing");
		options.put(Config.PROTECT_PVP, "pvp");
		options.put(Config.PROTECT_DESTROYING, "destroy");
		options.put(Config.PROTECT_DAMAGE, "damage");
		options.put(Config.PROTECT_FIRE, "fire");
		options.put(Config.PROTECT_EXPLOSIONS, "explosion");
	}

	@Override
	public String name() {
		return "protect";
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		List<String> list = new ArrayList<>();
		if (args.size() == 1) {
			list.add("true");
			list.add("false");
			list.addAll(options.values());
			return list;
		} else if (args.size() == 2) {
			list.add("true");
			list.add("false");
			return list;
		} else 
			return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() < 1 || args.size() > 2)
			return false;
		
		String first = args.get(0).toLowerCase();
		String second = args.size() == 2 ? args.get(1).toLowerCase() : null;
		
		String protectionString = null;
		String enableString = null;
		
		if (options.values().contains(first)) {
			protectionString = first;
			enableString = second;
		} else {
			if (args.size() == 2)
				return false;
			enableString = first;
		}
		
		Config protection;
		boolean enable;
		
		if (protectionString == null)
			protection = Config.PROTECT;
		else {		
			protection = options.inverse().get(protectionString);
			if (protection == null)
				return false;
		}
		
		if (enableString == null)
			enable = !game.getProtected(protection);
		else if (enableString.equals("true"))
			enable = true;
		else if (enableString.equals("false"))
			enable = false;
		else
			return false;
		
		game.setProteced(protection, enable);
		if (enable)
			game.setProteced(Config.PROTECT, true);
		
		String returnString = protection == Config.PROTECT ? "" : options.get(protection) + " ";
				
		if (enable)
			Bomberman.sendMessage(sender, "Game %g enabled " + returnString + "protection", game);
		else
			Bomberman.sendMessage(sender, "Game %g removed " + returnString + "protection", game);
		return true;
	}
	
	@Override
	public String description() {
		return "protects the arena from griefing";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<game> [protection-option] <true/false>";
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

}
