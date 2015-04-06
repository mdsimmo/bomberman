package io.github.mdsimmo.bomberman.commands.game.force;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Stop extends GameCommand {

	public Stop(Cmd parent) {
		super(parent);
	}

	@Override
	public Text nameShort() {
		return Text.STOP_NAME;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
            return false;
		
        if (!game.isPlaying)
            Chat.sendMessage(getMessage(Text.STOP_NOT_STARTED, sender).put( "game", game));
	else {
        	game.stop();
            if (!game.players.contains(sender))
            	Chat.sendMessage(getMessage(Text.STOP_SUCCESS, sender).put( "game", game));
            for (PlayerRep rep : game.players) {
            	Chat.sendMessage(getMessage(Text.STOP_SUCCESS, rep.getPlayer()).put( "game", game));
            }
            
        }
        return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public Text extraShort() {
		return Text.STOP_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.STOP_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.STOP_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.STOP_USAGE;
	}

}
