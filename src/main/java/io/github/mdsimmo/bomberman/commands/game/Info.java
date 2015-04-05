package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public class Info extends GameCommand {

	public Info( Cmd parent ) {
		super( parent );
	}

	@Override
	public Text nameShort() {
		return Text.INFO_NAME;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public List<String> shortOptions( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
			return false;

		Chat.sendHeading(sender, Text.INFO.getMessage(sender).put( "game", game));
		Map<Message, Message> list = new LinkedHashMap<>();
		if (game.isPlaying)
			list.put(getMessage(Text.INFO_STATUS, sender), getMessage(Text.INFO_IN_PROGRESS, sender));
		else
			list.put(getMessage(Text.INFO_STATUS, sender), getMessage(Text.INFO_WAITING, sender));
		list.put(
				getMessage(Text.INFO_PLAYERS, sender),
				new Message(sender, ""+game.players.size()));
		list.put(
				getMessage(Text.INFO_MIN_PLAYERS, sender),
				new Message(sender, ""+game.getMinPlayers()));
		list.put(
				getMessage(Text.INFO_MAX_PLAYERS, sender),
				new Message(sender, ""+game.board.spawnPoints.size()));
		list.put(
				getMessage(Text.INFO_INIT_BOMBS, sender),
				new Message(sender, ""+game.getBombs()));
		list.put(
				getMessage(Text.INFO_INIT_LIVES, sender),
				new Message(sender, ""+game.getLives()));
		list.put(
				getMessage(Text.INFO_INIT_POWER, sender),
				new Message(sender, ""+game.getPower()));
		Message fare = getMessage(Text.INFO_FARE, sender);
		if (game.getFare() == null)
			list.put(fare, getMessage(Text.INFO_NO_FARE, sender));
		else
			list.put(fare, new Message(sender, game.getFare().toString() ));
		
		Message prize = getMessage(Text.INFO_PRIZE, sender);
		if (game.getPot() == true && game.getFare() != null)
			list.put(prize, getMessage(Text.INFO_POT_AT, sender).put( "game", game )  );
		else {
			if (game.getPrize() == null)
				list.put(prize, getMessage(Text.INFO_NO_PRIZE, sender));
			else
				list.put(prize, new Message(sender, game.getPrize().toString() ));
		}
		Message sd = game.getSuddenDeath() == -1 ? getMessage( Text.INFO_OFF,
				sender ) : getMessage(Text.INFO_TIME, sender).put( "game", game);
		list.put(getMessage(Text.INFO_SUDDENDEATH, sender), sd);
		Message to = game.getTimeout() == -1 ? Text.INFO_OFF.getMessage(sender)	: getMessage( Text.INFO_TIME, sender).put( "game", game );
		list.put(getMessage(Text.INFO_TIMEOUT, sender), to);
		list.put(Text.ARENA.getMessage(sender), new Message(sender, game.board.name));
		Chat.sendMap(sender, list);
		return true;
	}

	@Override
	public Text extraShort() {
		return Text.INFO_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.INFO_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.INFO_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.INFO_USAGE;
	}

}
