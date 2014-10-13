package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.commands.arena.Arena;
import io.github.mdsimmo.bomberman.commands.game.Game;

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
	public String description() {
		return "Commands for Bomberman";
	}
	
	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.get(args.size()-1).equalsIgnoreCase("?")) {
			longHelp(sender, args);
			return true;
		}
		return super.run(sender, args);
	}

	@Override
	public void setChildren() {
		addChildren(
				new Game(this),
				new Arena(this),
				new Help(this)
			);
	}

	@Override
	public String name() {
		if (aliases.isEmpty())
			return "bomberman";
		else
			return aliases.get(0);
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}
	
	@Override
	public Command getCommand(CommandSender sender, List<String> args) {
		// handle if an alias is used
		if (args.size() > 0)
			if (aliases.contains(args.get(0).toLowerCase()))
					args.set(0, name());
		return super.getCommand(sender, args);
	}
}
