package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.arenabuilder.ArenaGenerator;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class ArenaList extends Cmd {

	public ArenaList( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.ARENA_LIST_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() != 0 )
			return false;
		List<String> arenas = ArenaGenerator.allBoards();
		if ( arenas.size() == 0 ) {
			Chat.sendMessage( getMessage( Text.ARENA_LIST_NO_ARENA, sender ) );
		} else {
			Chat.sendHeading( Text.LIST.getMessage( sender ), Text.ARENA.getMessage( sender ) );
			List<Message> list = new ArrayList<>();
			for ( String name : arenas ) {
				if ( !name.endsWith( ".old" ) )
					list.add( new Message( sender, name ) );
			}
			Chat.sendList( list );
		}
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public Message example( CommandSender sender ) {
		return getMessage( Text.ARENA_LIST_EXAMPLE, sender );
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.ARENA_LIST_EXTRA, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.ARENA_LIST_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.ARENA_LIST_USAGE, sender );
	}
}
