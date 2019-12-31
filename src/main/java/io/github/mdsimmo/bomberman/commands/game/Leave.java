package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.game.GamePlayer;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.game.playerstates.GamePlayingState;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leave extends Cmd {

	public Leave(Cmd parent) {
		super(parent);
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.LEAVE_NAME, sender );
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 0)
			return false;
		if (sender instanceof Player) {
			GamePlayer rep = GamePlayer.getPlayerRep((Player) sender);
			if ( rep.getState() instanceof GamePlayingState ) {
				GamePlayingState state = (GamePlayingState)rep.getState();
				state.kill();
			} else {
				Chat.sendMessage(getMessage(Text.LEAVE_NOT_JOINED, sender));
			}
			
		} else {
			Chat.sendMessage(getMessage(Text.MUST_BE_PLAYER, sender));
		}
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.PLAYER;
	}

	@Override
	public Message extra(CommandSender sender ) {
		return getMessage(Text.LEAVE_EXTRA, sender);
	}

	@Override
	public Message description(CommandSender sender ) {
		return getMessage(Text.LEAVE_DESCRIPTION, sender);
	}

	@Override
	public Message usage(CommandSender sender ) {
		return getMessage(Text.LEAVE_USAGE, sender);
	}

	@Override
	public Message example(CommandSender sender ) {
		return getMessage(Text.JOIN_EXAMPLE, sender);
	}

}
