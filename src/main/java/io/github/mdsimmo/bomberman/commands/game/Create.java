package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Box;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Create extends Command {

	public Create(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.CREATE_NAME;
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		if (args.size() == 1)
			return Game.allGames();
		else if (args.size() == 2)
			return BoardGenerator.allBoards();
		else
			return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (!(args.size() == 1 || args.size() == 2))
			return false;
		if (sender instanceof Player) {
			if (Game.findGame(args.get(0)) != null) {
				Chat.sendMessage(sender, getMessage(Text.CREATE_GAME_EXISTS, sender, args.get(0)));
			} else {
				Board arena;
				if (args.size() == 2) {
					arena = BoardGenerator.loadBoard(args.get(1));
				} else {
					arena = BoardGenerator.loadBoard((String)Config.DEFAULT_ARENA.getValue());
				}
				if (arena == null) {
					if (args.size() == 1)
						Chat.sendMessage(sender, getMessage(Text.CREATE_DEFAULTMISSING, sender, (String)Config.DEFAULT_ARENA.getValue()));
					else
						Chat.sendMessage(sender, getMessage(Text.INVALID_ARENA, sender, args.get(1)));
					return true;
				}
				// long location getting line to round to integers...
				Location l = ((Player) sender).getLocation().getBlock().getLocation();
				Game game = createGame(args.get(0), l, arena);
				PlayerRep.getPlayerRep((Player)sender).setGameActive(game);
				Chat.sendMessage(sender, getMessage(Text.CREATE_SUCCESS, sender, game));
			}
		} else {
			Chat.sendMessage(sender, getMessage(Text.MUST_BE_PLAYER, sender));
		}
		return true;
	}

	private Game createGame(String name, Location l, Board arena) {
		Game game = new Game(name, new Box(l, arena.xSize, arena.ySize, arena.zSize));
		game.board = arena;
		game.oldBoard = BoardGenerator.createArena(name + ".old", game.box);
		BoardGenerator.switchBoard(game.oldBoard, game.board, game.box);
		Game.register(game);
		return game;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Message example(CommandSender sender, List<String> args) {
		String arena = Utils.random(BoardGenerator.allBoards());
		arena = arena == null ? "myarena" : arena;
		return getMessage(Text.CONVERT_EXAMPLE, sender, arena);
	}

	@Override
	public Message extra(CommandSender sender, List<String> args) {
		return getMessage(Text.CREATE_EXTRA, sender);
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return getMessage(Text.CREATE_DESCRIPTION, sender);
	}

	@Override
	public Message usage(CommandSender sender, List<String> args) {
		return getMessage(Text.CREATE_USAGE, sender);
	}

}
