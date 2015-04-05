package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import org.bukkit.command.CommandSender;

public class Set extends CommandGroup {

	public Set(Cmd parent) {
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
	public Message name( CommandSender sender ) {
		return getMessage( Text.SET_NAME, sender );
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Message description(CommandSender sender ) {
		return Text.SET_DESCRIPTION.getMessage(sender);
	}

}
