package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leave extends Command {

	public Leave(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.LEAVE_NAME;
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
			PlayerRep rep = PlayerRep.getPlayerRep((Player) sender);
			if (!rep.kill()) {
				if (rep.getGamePlaying() == null)
					Chat.sendMessage(sender, getMessage(Text.LEAVE_NOT_JOINED, sender));
				else
					Chat.sendMessage(sender, getMessage(Text.LEAVE_FAILED, sender));
			}
		} else {
			Chat.sendMessage(sender, getMessage(Text.MUST_BE_PLAYER, sender));
		}
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.PLAYER;
	}

	@Override
	public Message extra(CommandSender sender, List<String> args) {
		return getMessage(Text.JOIN_EXTRA, sender);
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return getMessage(Text.JOIN_DESCRIPTION, sender);
	}

	@Override
	public Message usage(CommandSender sender, List<String> args) {
		return getMessage(Text.JOIN_USAGE, sender);
	}

	@Override
	public Message example(CommandSender sender, List<String> args) {
		return getMessage(Text.JOIN_EXAMPLE, sender);
	}

}
