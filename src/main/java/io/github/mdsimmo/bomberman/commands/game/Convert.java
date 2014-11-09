package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Box;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Convert extends Command {

	public Convert(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.CONVERT_NAME;
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1)
			return Game.allGames();
		else
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 1)
			return false;
		if (sender instanceof Player) {
			if (Game.findGame(args.get(0)) != null) {
				Chat.sendMessage(sender, getMessage(Text.CONVERT_GAME_EXISTS, sender, args.get(0)));
			} else {
				Box box = BoardGenerator.getBoundingStructure(
						((Player)sender).getTargetBlock(null, 100));
				Board board = BoardGenerator.createArena(args.get(0) + ".old", box);
				BoardGenerator.saveBoard(board);
				Game game = new Game(args.get(0), box);
				game.board = board;
				game.oldBoard = board;
				Game.register(game);
				PlayerRep.getPlayerRep((Player)sender).setGameActive(game);
				Chat.sendMessage(sender, getMessage(Text.CONVERT_SUCCESS, sender, game));
			}
		} else {
			Chat.sendMessage(sender, getMessage(Text.MUST_BE_PLAYER, sender));
		}
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Message example(CommandSender sender, List<String> args) {
		String game = Utils.random(Game.allGames());
		game = game == null ? "mygame" : game;
		return getMessage(Text.CONVERT_EXAMPLE, sender, game);
	}
	
	@Override
	public Message extra(CommandSender sender, List<String> args) {
		return getMessage(Text.CONVERT_EXTRA, sender);
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return getMessage(Text.CONVERT_DESCRIPTION, sender);
	}

	@Override
	public Message usage(CommandSender sender, List<String> args) {
		return getMessage(Text.CONVERT_USAGE, sender);
	}

}
