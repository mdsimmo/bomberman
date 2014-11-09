package io.github.mdsimmo.bomberman.commands.game.force;

import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Reset extends GameCommand {

	public Reset(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.RESET_NAME;
	}
	
	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
			return false;
		
		for (PlayerRep rep : game.players)
			Chat.sendMessage(rep.getPlayer(), getMessage(Text.RESET_SUCCESS_P, rep.getPlayer(), game));
		game.stop();
		BoardGenerator.switchBoard(game.board, game.board, game.box);
		Chat.sendMessage(sender, getMessage(Text.RESET_SUCCESS, sender, game));
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

	@Override
	public Text extraShort() {
		return Text.RESET_EXTRA;
	}

	@Override
	public Text exampleShort() {
		return Text.RESET_EXAMPLE;
	}

	@Override
	public Text descriptionShort() {
		return Text.RESET_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.RESET_USAGE;
	}
}
