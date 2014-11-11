package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.commands.arena.Arena;
import io.github.mdsimmo.bomberman.commands.game.Game;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Bm extends CommandGroup {

	private static JavaPlugin plugin = Bomberman.instance;
	private static List<String> aliases = Config.ALIASES.getValue();
	{
		for (int i = 0; i < aliases.size(); i++)
			aliases.set(i, aliases.get(i).toLowerCase());
		plugin.getCommand("bomberman").setAliases(aliases);
	}
	
	public Bm() {
		super(null);
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.get(args.size()-1).equalsIgnoreCase("?")) {
			help(sender, args);
			return true;
		}
		return super.run(sender, args);
	}

	@Override
	public void setChildren() {
		addChildren(
				new Game(this),
				new Arena(this),
				new LanguageCmd(this),
				new Help(this)
			);
	}

	public Text name() {
		return Text.BOMBERMAN_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}
	
	@Override
	public Message description(CommandSender sender, List<String> args) {
		return Text.BOMBERMAN_DESCRIPTION.getMessage(sender);
	}
}
