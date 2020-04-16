package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.events.BmPlayerJoinGameIntent;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GameJoin extends GameCommand {

	public GameJoin(Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name() {
		return context(Text.JOIN_NAME).format();
	}

	@Override
	public List<String> gameOptions(List<String> args ) {
		return null;
	}

	@Override
	public boolean gameRun(CommandSender sender, List<String> args, Game game ) {
		if ( args.size() != 0 )
			return false;

		if (!(sender instanceof Player)) {
			context(Text.MUST_BE_PLAYER).sendTo(sender);
			return true;
		}

		var e = new BmPlayerJoinGameIntent(game, (Player) sender);
		Bukkit.getPluginManager().callEvent(e);
		e.verifyHandled();

		if (e.isCancelled()) {
			e.cancelledReason().ifPresentOrElse(
					reason -> reason.sendTo(sender),
					() -> context(Text.CANT_JOIN)
							.with("game", game)
							.with("player", sender)
							.sendTo(sender));
		} else {
			context(Text.PLAYER_JOINED)
					.with("game", game)
					.with("player", sender)
					.sendTo(sender);
		}

		return true;
	}

	@Override
	public Permission permission() {
		return Permission.PLAYER;
	}

	@Override
	public Message extra() {
		return context(Text.JOIN_EXTRA).format();
	}

	@Override
	public Message example() {
		return context(Text.JOIN_EXAMPLE).format();
	}

	@Override
	public Message description() {
		return context(Text.JOIN_DESCRIPTION).format();
	}

	@Override
	public Message usage() {
		return context(Text.JOIN_USAGE).format();
	}

}
