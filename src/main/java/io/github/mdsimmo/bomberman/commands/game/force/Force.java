package io.github.mdsimmo.bomberman.commands.game.force;

import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Force extends CommandGroup {

	public Force(Command parent) {
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
	public Text name() {
		return Text.FORCE_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return getMessage(Text.FORCE_DESCRIPTION, sender);
	}
}
