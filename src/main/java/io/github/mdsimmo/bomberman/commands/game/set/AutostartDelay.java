package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class AutostartDelay extends GameCommand {

	public AutostartDelay(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
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
            Chat.sendMessage(sender, getMessage(Text.STARTDELAY_SET, sender, game, game.getAutostartDelay()));
        } catch (NumberFormatException e) {
            Chat.sendMessage(sender, getMessage(Text.INVALID_NUMBER, sender, args.get(0)));
        }
        return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Text extraShort() {
		return Text.STARTDELAY_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.STARTDELAY_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.STARTDELAY_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.STARTDELAY_USAGE;
	}

}
