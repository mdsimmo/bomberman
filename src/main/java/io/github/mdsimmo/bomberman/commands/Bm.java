package io.github.mdsimmo.bomberman.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.mdsimmo.bomberman.commands.arena.Arena;
import io.github.mdsimmo.bomberman.commands.game.Game;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

public class Bm extends CommandGroup {
	
	public Bm() {
		super(null);
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
	public Text name() {
		return Text.BOMBERMAN_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return Text.BOMBERMAN_DESCRIPTION.getMessage(sender);
	}
}
