package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class AutostartDelay extends GameCommand {

	public AutostartDelay(Cmd parent) {
		super(parent);
	}

	@Override
	public Phrase nameShort() {
		return Text.STARTDELAY_NAME;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;
		        
        try {
            game.setAutostartDelay(Integer.parseInt(args.get(0)));
            Chat.sendMessage(getMessage(Text.STARTDELAY_SET, sender).put( "game", game) );
        } catch (NumberFormatException e) {
            Chat.sendMessage(getMessage(Text.INVALID_NUMBER, sender).put( "number", args.get(0)));
        }
        return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Phrase extraShort() {
		return Text.STARTDELAY_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.STARTDELAY_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.STARTDELAY_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.STARTDELAY_USAGE;
	}

}
