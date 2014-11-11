package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Arena extends GameCommand {

	public Arena(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.SETARENA_NAME;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 0)
			return BoardGenerator.allBoards();
		else
			return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;
		
		if (game.isPlaying) {
			Chat.sendMessage(sender, getMessage(Text.SETARENA_GIP, sender, game));
			return true;
		}

		Board board = BoardGenerator.loadBoard(args.get(0));
		if (board == null) {
			Chat.sendMessage(sender, getMessage(Text.INVALID_ARENA, sender, args.get(0)));
			return true;
		}
		BoardGenerator.switchBoard(game.board, game.oldBoard, game.box);
		game.board = board;
		game.oldBoard = BoardGenerator.createArena(game.name + ".old", game.box);
		BoardGenerator.switchBoard(game.oldBoard, board, game.box);
		Chat.sendMessage(sender, getMessage(Text.SETARENA_SUCCESS, sender, game, board));
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Text extraShort() {
		return Text.SETARENA_EXTRA;
	}

	@Override
	public Text exampleShort() {
		throw new RuntimeException("This method is invalid");
	}
	
	@Override
	public Message example(CommandSender sender, List<String> args) {
		String game = Utils.random(Game.allGames());
		game = game == null ? "mygame" : game;
		String arena = Utils.random(BoardGenerator.allBoards());
		arena = arena == null ? "myarena" : game;
		return getMessage(Text.SETARENA_EXAMPLE, sender, game, arena);
	}

	@Override
	public Text descriptionShort() {
		return Text.SETARENA_DESCRIPTION;
	}

	@Override
	public Text usageShort() {
		return Text.SETARENA_USAGE;
	}

}
