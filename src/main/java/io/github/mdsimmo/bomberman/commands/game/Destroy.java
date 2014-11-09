package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Destroy extends Command {

	public Destroy(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.DESTROY_NAME;
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
		Game game = Game.findGame(args.get(0));
		if (game != null) {
			game.destroy();
			Chat.sendMessage(sender, getMessage(Text.DESTROY_SUCCESS, sender, game));
		} else
			Chat.sendMessage(sender, getMessage(Text.INVALID_GAME, sender, args.get(0)));
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Message example(CommandSender sender, List<String> args) {
		String game = Utils.random(Game.allGames());
		if (game == null)
			game = "mygame";
		return getMessage(Text.DESTROY_EXAMPLE, sender, game);
	}

	@Override
	public Message extra(CommandSender sender, List<String> args) {
		return getMessage(Text.DESTROY_EXTRA, sender);
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return getMessage(Text.DESTROY_DESCRIPTION, sender);
	}

	@Override
	public Message usage(CommandSender sender, List<String> args) {
		return getMessage(Text.DESTROY_USAGE, sender);
	}

}
