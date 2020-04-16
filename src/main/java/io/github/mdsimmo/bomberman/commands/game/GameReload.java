package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.events.BmGameDeletedIntent;
import io.github.mdsimmo.bomberman.events.BmGameRebuildIntent;
import io.github.mdsimmo.bomberman.events.BmRunStoppedIntent;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GameRegistry;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.command.CommandSender;

import java.util.List;

public class GameReload extends GameCommand {

	public GameReload(Cmd parent) {
		super(parent);
	}

	@Override
	public Message name() {
		return context(Text.RELOAD_NAME).format();
	}

	@Override
	public boolean gameRun(CommandSender sender, List<String> args, Game game) {
		if (args.size() != 0)
            return false;
		GameRegistry.reload(game);
		BmGameRebuildIntent.rebuild(game);
		Text.RELOAD_SUCCESS.with("game", game).sendTo(sender);
        return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public List<String> gameOptions(List<String> args) {
		return null;
	}

	@Override
	public Message extra() {
		return context(Text.RELOAD_EXTRA).format();
	}

	@Override
	public Message example() {
		return context(Text.RELOAD_EXAMPLE).format();
	}

	@Override
	public Message description() {
		return context(Text.RELOAD_DESCRIPTION).format();
	}

	@Override
	public Message usage() {
		return context(Text.RELOAD_USAGE).format();
	}

}
