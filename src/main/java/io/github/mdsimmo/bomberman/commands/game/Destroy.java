package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Destroy extends Cmd {

	public Destroy(Cmd parent) {
		super(parent);
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.DESTROY_NAME, sender );
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
			game.resetArena();
			Chat.sendMessage(getMessage(Text.DESTROY_SUCCESS, sender).put( "game", game));
		} else
			Chat.sendMessage(getMessage(Text.INVALID_GAME, sender).put( "game", args.get(0)));
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Message example(CommandSender sender ) {
		String game = Utils.random(Game.allGames());
		if (game == null)
			game = "mygame";
		return getMessage(Text.DESTROY_EXAMPLE, sender).put( "example", game);
	}

	@Override
	public Message extra(CommandSender sender ) {
		return getMessage(Text.DESTROY_EXTRA, sender);
	}

	@Override
	public Message description(CommandSender sender ) {
		return getMessage(Text.DESTROY_DESCRIPTION, sender);
	}

	@Override
	public Message usage(CommandSender sender ) {
		return getMessage(Text.DESTROY_USAGE, sender);
	}

}
