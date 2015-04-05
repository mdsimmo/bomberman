package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ignore extends GameCommand {

	public Ignore(Cmd parent) {
		super(parent);
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
			return false;
		
		if (sender instanceof Player == false) {
			Chat.sendMessage(sender, getMessage(Text.MUST_BE_PLAYER, sender));
			return true;
		}
		
		if (game.observers.remove(PlayerRep.getPlayerRep((Player)sender))) {
			Chat.sendMessage(sender, getMessage(Text.IGNORE_SUCCESS, sender).put( "game", game));
		} else {
			Chat.sendMessage(sender, getMessage(Text.IGNORE_NOT_WATCHED, sender).put( "game", game));
		}
		
		return true;
	}

	@Override
	public Text nameShort() {
		return Text.IGNORE_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public Text extraShort() {
		return Text.IGNORE_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.IGNORE_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.IGNORE_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.IGNORE_USAGE;
	}

}
