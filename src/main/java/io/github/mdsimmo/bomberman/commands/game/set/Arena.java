package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.arenabuilder.ArenaGenerator;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Arena extends GameCommand {

	public Arena(Cmd parent) {
		super(parent);
	}

	@Override
	public Phrase nameShort() {
		return Text.SETARENA_NAME;
	}

	@Override
	public List<String> shortOptions(CommandSender sender, List<String> args) {
		if (args.size() == 0)
			return ArenaGenerator.allBoards();
		else
			return null;
	}

	@Override
	public boolean runShort(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 1)
			return false;
		
		if (game.isPlaying) {
			Chat.sendMessage(getMessage(Text.SETARENA_GIP, sender).put( "game", game));
			return true;
		}

		Board board = ArenaGenerator.loadBoard(args.get(0));
		if (board == null) {
			Chat.sendMessage(getMessage(Text.INVALID_ARENA, sender).put( "arena", args.get(0)));
			return true;
		}
		ArenaGenerator.switchBoard(game.board, game.oldBoard, game.box);
		game.board = board;
		game.oldBoard = ArenaGenerator.createArena(game.name + ".old", game.box);
		ArenaGenerator.switchBoard(game.oldBoard, board, game.box);
		Chat.sendMessage(getMessage(Text.SETARENA_SUCCESS, sender).put( "game", game).put( "arena", board));
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Phrase extraShort() {
		return Text.SETARENA_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		throw new RuntimeException("This method is invalid");
	}
	
	@Override
	public Message example(CommandSender sender ) {
		String game = Utils.random(Game.allGames());
		game = game == null ? "mygame" : game;
		String arena = Utils.random(ArenaGenerator.allBoards());
		arena = arena == null ? "myarena" : game;
		return getMessage(Text.SETARENA_EXAMPLE, sender).put( "game", game).put( "arena", arena);
	}

	@Override
	public Phrase descriptionShort() {
		return Text.SETARENA_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.SETARENA_USAGE;
	}

}
