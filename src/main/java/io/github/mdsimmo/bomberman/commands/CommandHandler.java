package io.github.mdsimmo.bomberman.commands;


import io.github.mdsimmo.bomberman.Bomberman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

public class CommandHandler implements CommandExecutor, TabCompleter {
	
	private JavaPlugin plugin = Bomberman.instance;
	private Bm handler = new Bm();
	
	public CommandHandler() {
		plugin.getCommand("bm").setExecutor(this);
		plugin.getCommand("bm").setTabCompleter(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String s, String[] args) {
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));
		io.github.mdsimmo.bomberman.commands.Command c = handler.getCommand(sender, arguments);
		if (!c.excecute(sender, arguments)) {
			c.incorrectUsage(sender);
			c.displayHelp(sender, arguments);
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender,
			Command command, String s, String[] args) {
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));
		io.github.mdsimmo.bomberman.commands.Command c = handler.getCommand(sender, arguments);
		List<String> options = new ArrayList<>();
		List<String> all = c.options(sender, arguments);
		if (all == null)
			all = new ArrayList<>();
		for (String option : all) {
			if (StringUtil.startsWithIgnoreCase(option, args[args.length-1]))
					options.add(option);
		}
		return options;
	}
}
