package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class MinPlayers extends GameCommand {

	public MinPlayers(Cmd parent) {
		super(parent);
	}

	@Override
	public Phrase nameShort() {
		return Text.MINPLAYERS_NAME;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;

		int amount;
		try {
			amount = Integer.parseInt(args.get(0));
		} catch (Exception e) {
			return false;
		}
		if ( amount <= 0 ) {
			Chat.sendMessage( getMessage(Text.MINPLAYERS_LESS_THAN_ONE, sender)
					.put( "game", game )
					.put("amount", amount) );
			return true;
		}
		game.setMinPlayers(amount);	
		Chat.sendMessage(getMessage(Text.MINPLAYERS_SET, sender).put( "game", game ));
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Phrase extraShort() {
		return Text.MINPLAYERS_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.MINPLAYERS_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.MINPLAYERS_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.MINPLAYERS_USAGE;
	}
}
