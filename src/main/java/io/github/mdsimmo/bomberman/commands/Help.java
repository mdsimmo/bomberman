package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Help extends Command {

	public Help(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.HELP_NAME;
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		return ((CommandGroup)parent).getCommand(sender, args).options(sender, args);
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		parent.help(sender, args);
		return true;
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return getMessage(Text.HELP_DESCRIPTION, sender);
	}

	@Override
	public Message usage(CommandSender sender, List<String> args) {
		return getMessage(Text.HELP_USAGE, sender, args); 
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public Message example(CommandSender sender, List<String> args) {
		return getMessage(Text.HELP_EXAMPLE, sender);
	}

	@Override
	public Message extra(CommandSender sender, List<String> args) {
		return getMessage(Text.HELP_EXTRA, sender);
	}
	
	@Override
	public String extra(CommandSender sender, List<String> args) {
		return "Shortcut is to put '?' after a command";
	}

}
