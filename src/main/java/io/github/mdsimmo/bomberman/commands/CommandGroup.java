package io.github.mdsimmo.bomberman.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class CommandGroup extends Command {

	private List<Command> children = new ArrayList<>();
	
	/**
	 * Adds some child commands
	 * @param children the child commands
	 */
	public void addChildren(Command ... children) {
		this.children.addAll(Arrays.asList(children));
	}
	
	public CommandGroup(Command parent) {
		super(parent);
		setChildren();
	}
	
	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		List<String> options = new ArrayList<>();
		for (Command c : children) {
			if (c.isAllowedBy(sender))
				options.add(c.name());
		}
		return options;
	}
	
	public abstract String description();
	
	@Override
	public void displayHelp(CommandSender sender, List<String> args) {
		if (args.size() != 0) {
			for (Command c : children) {
				if (c.name().equalsIgnoreCase(args.get(0))) {
					args.remove(0);
					c.displayHelp(sender, args);
					return;
				}
			}
		}
		sender.sendMessage(heading(name()));
		sender.sendMessage(info(sender));
	}
	
	@Override
	public String info(CommandSender sender) {
		String info = ChatColor.GOLD + "Description: " + ChatColor.WHITE + description() + " \n";
		info += ChatColor.GOLD + "Commands: \n" + ChatColor.WHITE;
		info += usage(sender);
		return info;
	}
	
	@Override
	public String usage(CommandSender sender) {
		String usage = "";
		for (Command c : children) {
			if (!c.isAllowedBy(sender))
				continue;
			
			if (c instanceof CommandGroup)
				usage += "    " + c.name() + " [...]\n";
			else
				usage += "    " + c.name() + "\n";
		}
		return usage;
	}
	
	/**
	 * sets what children this group has
	 */
	public abstract void setChildren();

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			displayHelp(sender, args);
			return true;
		} else {
			for (Command c : children) {
				if (c.name().equalsIgnoreCase(args.get(0))) {
					args.remove(0);
					return c.excecute(sender, args);
				}				
			}
			sender.sendMessage(ChatColor.RED + "You entered an unknown command!");
			return false;
		}
	}
	
	public Command getCommand(CommandSender sender, List<String> args) {
		if (args.size() == 0)
			return this;
		for (Command c : children) {
			if (c.name().equalsIgnoreCase(args.get(0))) {
				args.remove(0);
				if (c instanceof CommandGroup)
					return ((CommandGroup)c).getCommand(sender, args);
				else
					return c;
			}
		}
		return this;
	}
}
