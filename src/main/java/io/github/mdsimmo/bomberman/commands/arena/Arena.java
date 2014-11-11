package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Arena extends CommandGroup {

	public Arena(Command parent) {
		super(parent);
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return Text.ARENA_DESCRIPTION.getMessage(sender);
	}

	@Override
	public void setChildren() {
		addChildren(
				new Create(this),
				new Delete(this),
				new EditArena(this),
				//new Shift(this),
				new ArenaList(this)
			);
	}

	@Override
	public Text name() {
		return Text.ARENA_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

}
