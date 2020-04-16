package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.events.BmRunStartCountDownIntent;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.NumberUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RunStart extends GameCommand {

	public RunStart(Cmd parent) {
		super(parent);
	}

	@Override
	public Message name() {
		return context(Text.START_NAME).format();
	}

	@Override
	public List<String> gameOptions(List<String> args) {
		return null;
	}
	
	@Override
	public boolean gameRun(CommandSender sender, List<String> args, Game game) {
		if (args.size() > 1)
			return false;

		// get the delay
		var delay = 3;
		if (args.size() == 1) {
			var parsed = NumberUtils.tryParseInt(args.get(0));
			if (parsed.isPresent()) {
				delay = parsed.getAsInt();
				if (delay < 0) {
					Text.INVALID_NUMBER.with("number", delay).sendTo(sender);
					return true;
				}
			} else {
				return false;
			}
		}

		var e = BmRunStartCountDownIntent.startGame(game, delay);
		if (e.isCancelled()) {
			e.getCancelledReason().ifPresentOrElse(
					reason -> reason.sendTo(sender),
					() -> Text.GAME_START_CANCELLED.with("game", game).sendTo(sender)
			);
		} else {
			Text.GAME_START_SUCCESS.with("game", game).sendTo(sender);
		}
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_OPERATE;
	}

	@Override
	public Message extra() {
		return context(Text.START_EXTRA).format();
	}

	@Override
	public Message example() {
		return context(Text.START_EXAMPLE).format();
	}

	@Override
	public Message description() {
		return context(Text.START_DESCRIPTION).format();
	}

	@Override
	public Message usage() {
		return context(Text.START_USAGE).format();
	}
}
