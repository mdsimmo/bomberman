package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import org.bukkit.command.CommandSender;

public class Arena extends CommandGroup {

	public Arena(Cmd parent) {
		super(parent);
	}

	@Override
	public Message description(CommandSender sender) {
		return getMessage( Text.ARENA_DESCRIPTION, sender );
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
	public Message name( CommandSender sender ) {
		return getMessage( Text.ARENA_NAME, sender );
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

}
