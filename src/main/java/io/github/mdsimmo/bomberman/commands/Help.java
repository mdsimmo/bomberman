package io.github.mdsimmo.bomberman.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Help extends Command {

	public Help(Command parent) {
		super(parent);
	}

	@Override
	public String name() {
		return "help";
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		return ((CommandGroup)parent).getCommand(sender, args).options(sender, args);
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		parent.longHelp(sender, args);
		return true;
	}

	@Override
	public String description() {
		return "Help for the selected command";
	}

	@Override
	public String usage(CommandSender sender) {
		return "/" + path() + "<command path>"; 
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public String example(CommandSender sender, List<String> args) {
		return "/" + path() + "game create";
	}
	
	@Override
	public String extra(CommandSender sender, List<String> args) {
		return "Shortcut is to put '?' after a command";
	}

}
