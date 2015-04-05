package io.github.mdsimmo.bomberman.commands.game.force;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import org.bukkit.command.CommandSender;

public class Force extends CommandGroup {

	public Force(Cmd parent) {
		super(parent);
	}

	@Override
	public void setChildren() {
		addChildren(
				new Reset(this),
				new Start(this),
				new Stop(this)
			);
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.FORCE_NAME, sender );
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

	@Override
	public Message description(CommandSender sender ) {
		return getMessage(Text.FORCE_DESCRIPTION, sender);
	}
}
