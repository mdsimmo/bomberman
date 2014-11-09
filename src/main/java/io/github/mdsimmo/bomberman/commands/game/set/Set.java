package io.github.mdsimmo.bomberman.commands.game.set;

import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

public class Set extends CommandGroup {

	public Set(Command parent) {
		super(parent);
	}

	@Override
	public void setChildren() {
		addChildren(
				new Arena(this),
				new Autostart(this),
				new AutostartDelay(this),
				new Bombs(this),
				new Fare(this),
				new Handicap(this),
				new Lives(this),
				new MinPlayers(this),
				new Power(this),
				new Prize(this),
				new SuddenDeath(this),
				new Timeout(this)
			);
	}

	@Override
	public Text name() {
		return Text.SET_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return Text.SET_DESCRIPTION.getMessage(sender);
	}

}
