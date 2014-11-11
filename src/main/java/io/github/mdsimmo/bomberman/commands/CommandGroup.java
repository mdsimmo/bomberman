package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
				options.add(c.name().getMessage(sender).toString());
		}
		return options;
	}
	
	@Override
	public void help(CommandSender sender, List<String> args) {
		Command c = getCommand(sender, args);
		if (c == this)
			super.help(sender, args);
		else
			c.help(sender, args);
	}
	
	@Override
	public Message extra(CommandSender sender, List<String> args) {
		return null;
	}
	
	@Override
	public Message example(CommandSender sender, List<String> args) {
		return null;
	}
	
	@Override
	public Map<Message, Message> info(CommandSender sender, List<String> args) {
		Map<Message, Message> info = new LinkedHashMap<Message, Message>();
		info.put(getMessage(Text.DESCTIPTION, sender), description(sender, args));
		info.put(getMessage(Text.COMMANDS, sender), usage(sender, args));
		return info;
	}
	
	@Override
	public Message usage(CommandSender sender, List<String> args) {
		String usage = "\n";
		for (Command c : children) {
			if (!c.isAllowedBy(sender))
				continue;
			
			if (c instanceof CommandGroup)
				usage += "    " + c.name().getMessage(sender) + " [...]\n";
			else
				usage += "    " + c.name().getMessage(sender) + "\n";
		}
		return new Message(sender, usage);
	}
	
	/**
	 * sets what children this group has
	 */
	public abstract void setChildren();

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			help(sender, args);
			return true;
		} else {
			for (Command c : children) {
				if (c.name().getMessage(sender).toString().equalsIgnoreCase(args.get(0))) {
					args.remove(0);
					return c.execute(sender, args);
				}				
			}
			Chat.sendMessage(sender, getMessage(Text.UNKNOWN_COMMAND, sender, Utils.listToString(args)));
			help(sender, args);
			return true;
		}
	}
	
	public Command getCommand(CommandSender sender, List<String> args) {
		if (args.size() == 0)
			return this;
		for (Command c : children) {
			if (c.name().getMessage(sender).toString().equalsIgnoreCase(args.get(0))) {
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
