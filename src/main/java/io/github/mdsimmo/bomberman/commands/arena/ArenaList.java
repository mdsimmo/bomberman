package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class ArenaList extends Command {

	public ArenaList(Command parent) {
		super(parent);
	}

	@Override
	public Text name() {
		return Text.ARENA_LIST_NAME;
	}

	@Override
	public List<String> options(CommandSender sender, List<String> args) {
		return null;
	}

	@Override
	public boolean run(CommandSender sender, List<String> args) {
		if (args.size() != 0)
			return false;
		List<String> arenas = BoardGenerator.allBoards();
		if (arenas.size() == 0) {
			Chat.sendText(sender, Text.COUNT_STOPPED_ALL);
		} else {
			Chat.sendHeading(sender, getMessage(Text.LIST, sender, Text.ARENA.getMessage(sender)));
			List<Message> list = new ArrayList<>();
			for (String name : arenas) {
				if (!name.endsWith(".old"))
					list.add(new Message(sender, name));
			}
			Chat.sendList(sender, list);
		}
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public Message example(CommandSender sender, List<String> args) {		
		return getMessage(Text.ARENA_LIST_EXAMPLE, sender);
	}

	@Override
	public Message extra(CommandSender sender, List<String> args) {
		return getMessage(Text.ARENA_LIST_EXTRA, sender);
	}

	@Override
	public Message description(CommandSender sender, List<String> args) {
		return getMessage(Text.ARENA_LIST_DESCRIPTION, sender);
	}

	@Override
	public Message usage(CommandSender sender, List<String> args) {
		return getMessage(Text.ARENA_LIST_USAGE, sender);
	}
}
