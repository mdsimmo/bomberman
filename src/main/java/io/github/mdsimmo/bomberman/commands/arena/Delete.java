package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.arena.ArenaGenerator;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;

public class Delete extends Cmd {

	public Delete( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.DELETE_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		if ( args.size() == 1 )
			return ArenaGenerator.allBoards();
		else
			return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() != 1 )
			return false;
		String arena = args.get( 0 );
		File f = ArenaGenerator.toFile( arena );
		if ( !f.exists() ) {
			Chat.sendMessage( getMessage( Text.INVALID_ARENA, sender )
					.put( "arena", arena ) );
			return true;
		}
		for ( String name : Game.allGames() ) {
			Game game = Game.findGame( name );
			if ( game.getArena().name.equalsIgnoreCase( args.get( 0 ) )
					|| game.oldArena.name.equalsIgnoreCase( args.get( 0 ) ) ) {
				Chat.sendMessage(
						getMessage( Text.DELETE_ARENA_USED, sender ).put(
								"arena", arena ).put( "game", game ) );
				return true;
			}
		}
		ArenaGenerator.remove( arena );
		if ( f.delete() )
			Chat.sendMessage(
					getMessage( Text.DELETE_SUCCESSFUL, sender ).put( "arena",
							arena ) );
		else
			Chat.sendMessage( getMessage( Text.DELETE_TROUBLE, sender )
					.put( "file", f ) );
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.ARENA_EDITING;
	}

	@Override
	public Message example( CommandSender sender ) {
		String arena = Utils.random( ArenaGenerator.allBoards() );
		if ( arena == null )
			arena = "myarena";
		return getMessage( Text.DELETE_EXAMPLE, sender).put( "example", arena );
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.DELETE_EXTRA, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.DELETE_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.DELETE_USAGE, sender );
	}

}