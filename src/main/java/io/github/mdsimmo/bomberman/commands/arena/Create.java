package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.arena.Arena;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.arena.ArenaDetector.BoundingListener;
import io.github.mdsimmo.bomberman.arena.ArenaGenerator;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Box;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Create extends Cmd {

	public Create( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.ARENA_CREATE_NAME, sender );
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
		if ( sender instanceof Player == false ) {
			Chat.sendMessage( getMessage( Text.MUST_BE_PLAYER, sender ) );
			return true;
		}
		String name = args.get( 0 );
		for ( String gameName : Game.allGames() ) { 
			Game game = Game.findGame( gameName );
			if ( game.getArena().name.equalsIgnoreCase( name ) ) {
				Message message = getMessage( Text.ARENA_CREATE_IN_USE, sender );
				message.put( "game", game ).put( "arena", game.getArena());
				Chat.sendMessage( message );
				return true;
			}
		}
		Block target = Utils.getTarget( (Player)sender, 100 );
		if ( target != null ) {
			ArenaGenerator.getBoundingStructure( target, new BuildListener(
					sender, name ) );
			Message message = getMessage( Text.ARENA_CREATING, sender );
			message.put( "arena", name );
			Chat.sendMessage( message );
		} else {
			Chat.sendMessage( Text.ARENA_NO_TARGET.getMessage( sender ).put( "arena", name ) );
		}
		return true;
	}

	private class BuildListener implements BoundingListener {

		private final CommandSender sender;
		private final String name;

		public BuildListener( CommandSender sender, String name ) {
			this.sender = sender;
			this.name = name;
		}

		@Override
		public void onBoundingDetected( Box box ) {
			if ( box == null ) {
				Chat.sendMessage( getMessage( Text.ARENA_CREATE_TOO_BIG, sender )
						.put( "maxstructuresize",Config.MAX_STRUCTURE.getValue() )
						.put( "arena", name ) );
				return;
			}

			Arena arena = ArenaGenerator.createArena( name, box );
			if ( box.xSize < 2 && box.ySize < 2 && box.zSize < 2 ) {
				Chat.sendMessage( getMessage( Text.ARENA_CREATE_VERY_SMALL,
						sender ).put( "arena", arena) );
			}
			ArenaGenerator.saveBoard(arena);
			Chat.sendMessage( getMessage( Text.ARENA_CREATED, sender ).put(
					"arena", arena) );
		}
	}

	@Override
	public Permission permission() {
		return Permission.ARENA_EDITING;
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.ARENA_CREATE_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.ARENA_CREATE_USAGE, sender );
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.ARENA_CREATE_EXTRA, sender );
	}

	@Override
	public Message example( CommandSender sender ) {
		return getMessage( Text.ARENA_CREATE_EXAMPLE, sender );
	}

}
