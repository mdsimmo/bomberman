package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Box;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Create extends Cmd {

	public Create( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.CREATE_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		if ( args.size() == 1 )
			return Game.allGames();
		else if ( args.size() == 2 )
			return BoardGenerator.allBoards();
		else
			return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( !( args.size() == 1 || args.size() == 2 ) )
			return false;
		if ( sender instanceof Player ) {
			Game trial = Game.findGame( args.get( 0 ) );
			if ( trial != null ) {
				Chat.sendMessage(
						sender,
						getMessage( Text.CREATE_GAME_EXISTS, sender ).put(
								"game", args.get( 0 ) ) );
			} else {
				Board arena;
				if ( args.size() == 2 ) {
					arena = BoardGenerator.loadBoard( args.get( 1 ) );
				} else {
					arena = BoardGenerator
							.loadBoard( (String)Config.DEFAULT_ARENA.getValue() );
				}
				if ( arena == null ) {
					if ( args.size() == 1 )
						Chat.sendMessage(
								sender,
								getMessage( Text.CREATE_DEFAULTMISSING, sender )
										.put( "arena",
												(String)Config.DEFAULT_ARENA
														.getValue() ) );
					else
						Chat.sendMessage(
								sender,
								getMessage( Text.INVALID_ARENA, sender ).put(
										"arena", args.get( 1 ) ) );
					return true;
				}
				// long location getting line to round to integers...
				Location l = ( (Player)sender ).getLocation().getBlock()
						.getLocation();
				Game game = createGame( args.get( 0 ), l, arena );
				PlayerRep.getPlayerRep( (Player)sender ).setActiveGame( game );
				Chat.sendMessage(
						sender,
						getMessage( Text.CREATE_SUCCESS, sender ).put( "game",
								game ) );
			}
		} else {
			Chat.sendMessage( sender, getMessage( Text.MUST_BE_PLAYER, sender ) );
		}
		return true;
	}

	private Game createGame( String name, Location l, Board arena ) {
		Game game = new Game( name, new Box( l, arena.xSize, arena.ySize,
				arena.zSize ) );
		game.board = arena;
		game.oldBoard = BoardGenerator.createArena( name + ".old", game.box );
		BoardGenerator.switchBoard( game.oldBoard, game.board, game.box );
		Game.register( game );
		return game;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Message example( CommandSender sender ) {
		String arena = Utils.random( BoardGenerator.allBoards() );
		arena = arena == null ? "myarena" : arena;
		return getMessage( Text.CONVERT_EXAMPLE, sender )
				.put( "example", arena );
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.CREATE_EXTRA, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.CREATE_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.CREATE_USAGE, sender );
	}

}
