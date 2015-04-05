package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Protect extends GameCommand {

	private static BiMap<Config, Text> options = HashBiMap.create();
	
	public Protect(Cmd parent) {
		super(parent);
		options.put(Config.PROTECT, Text.PROTECT_ENABLED);
		options.put(Config.PROTECT_PLACING, Text.PROTECT_PLACEING);
		options.put(Config.PROTECT_PVP, Text.PROTECT_PVP);
		options.put(Config.PROTECT_DESTROYING, Text.PROTECT_DESTROYING);
		options.put(Config.PROTECT_DAMAGE, Text.PROTECT_DAMAGE);
		options.put(Config.PROTECT_FIRE, Text.PROTECT_FIRE);
		options.put(Config.PROTECT_EXPLOSIONS, Text.PROTECT_EXPLOSIONS);
	}

	@Override
	public Text nameShort() {
		return Text.PROTECT_NAME;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		List<String> list = new ArrayList<>();
		if (args.size() == 1) {
			list.add(getMessage(Text.TRUE, sender).toString());
			list.add(getMessage(Text.FALSE, sender).toString());
			for (Text text : options.values()) {
				list.add(getMessage(text, sender).toString());
			}
			return list;
		} else if (args.size() == 2) {
			list.add(getMessage(Text.TRUE, sender).toString());
			list.add(getMessage(Text.FALSE, sender).toString());
			return list;
		} else 
			return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() > 2)
			return false;
		
		if (args.size() == 0) {
			args.add(getMessage(Text.TRUE, sender).toString());
		}
		
		String first = args.get(0).toLowerCase();
		String second = args.size() == 2 ? args.get(1).toLowerCase() : null;
		
		Config protection = null;
		for (Text option : options.values()) {
			if (first.equalsIgnoreCase(getMessage(option, sender).toString())) {
				protection = options.inverse().get(option);
				break;
			}
		}
		
		boolean enable;
		String enable_s;
		
		if (protection == null) {
			if (args.size() == 2)
				return false;
			protection = Config.PROTECT;
			enable_s = first;
		} else {
			enable_s = second;
		}
		
		if (enable_s == null)
			enable = true;
		else if (enable_s.equalsIgnoreCase(getMessage(Text.TRUE, sender).toString()))
			enable = true;
		else if (enable_s.equalsIgnoreCase(getMessage(Text.FALSE, sender).toString()))
			enable = false;
		else
			return false;
		
		game.setProteced(protection, enable);
		if (enable)
			game.setProteced(Config.PROTECT, true);
		
		String returnString = protection == Config.PROTECT ? "" : options.get(protection).getMessage(sender).toString();
				
		if (enable)
			Chat.sendMessage(sender, getMessage(Text.PROTECT_ON, sender).put( "game", game).put( "protection", returnString));
		else
			Chat.sendMessage(sender, getMessage(Text.PROTECT_OFF, sender).put( "game", game).put( "protection", returnString));
		return true;
	}
	
	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Text extraShort() {
		return Text.PROTECT_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.PROTECT_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.PROTECT_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.PROTECT_USAGE;
	}

}
