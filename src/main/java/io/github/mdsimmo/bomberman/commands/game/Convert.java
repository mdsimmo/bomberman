package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.arenabuilder.ArenaDetector.BoundingListener;
import io.github.mdsimmo.bomberman.arenabuilder.ArenaGenerator;
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

public class Convert extends Cmd {

	public Convert( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.CONVERT_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		if ( args.size() == 1 )
			return Game.allGames();
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
		
		Game trial = Game.findGame( args.get( 0 ) );
		if ( trial != null ) {
			Chat.sendMessage( getMessage( Text.CONVERT_GAME_EXISTS, sender )
					.put( "game", trial ) );
			return true;
		}
		
		String name = args.get( 0 );
		Block target = Utils.getTarget( (Player)sender, 100 );
		if ( target == null ) {
			Chat.sendMessage( Text.ARENA_NO_TARGET.getMessage( sender ).put( "arena", name ) );
		} else {
			ArenaGenerator.getBoundingStructure( target, new BuildListener(
					sender, name ) );
			Message message = getMessage( Text.CONVERT_STARTED, sender );
			message.put( "game", name );
			Chat.sendMessage( message );
		}
		return true;
	}

	private class BuildListener implements BoundingListener {

		final CommandSender sender;
		final String name;

		public BuildListener( CommandSender sender, String name ) {
			this.sender = sender;
			this.name = name;
		}

		@Override
		public void onBoundingDetected( Box box ) {
			if ( box == null ) {
				Chat.sendMessage( getMessage( Text.ARENA_CREATE_TOO_BIG, sender )
						.put( "maxstructuresize", Config.MAX_STRUCTURE.getValue() )
						.put( "arena", name ) );
				return;
			}

			Board board = ArenaGenerator.createArena( name + ".old", box );
			if ( box.xSize < 2 && box.ySize < 2 && box.zSize < 2 ) {
				Chat.sendMessage( getMessage( Text.ARENA_CREATE_VERY_SMALL,
						sender ).put( "arena", board ) );
			}
			ArenaGenerator.saveBoard( board );
			Game game = new Game( name, box );
			game.board = board;
			game.oldBoard = board;
			Game.register( game );
			PlayerRep.getPlayerRep( (Player)sender ).setActiveGame( game );
			Message message = getMessage( Text.CONVERT_SUCCESS, sender );
			message.put( "game", game );
			Chat.sendMessage( message );
		}
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Message example( CommandSender sender ) {
		String game = Utils.random( Game.allGames() );
		game = game == null ? "mygame" : game;
		return getMessage( Text.CONVERT_EXAMPLE, sender ).put( "example", game );
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.CONVERT_EXTRA, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.CONVERT_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.CONVERT_USAGE, sender );
	}

}
