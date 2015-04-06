package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Box;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

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
		if ( sender instanceof Player ) {
			Game trial = Game.findGame( args.get( 0 ) );
			if ( trial != null ) {
				Chat.sendMessage(
						getMessage( Text.CONVERT_GAME_EXISTS, sender ).put(
								"game", trial ) );
			} else {
				Box box = BoardGenerator.getBoundingStructure( Utils.getTarget(
						(Player)sender, 100 ) );
				Board board = BoardGenerator.createArena( args.get( 0 )
						+ ".old", box );
				BoardGenerator.saveBoard( board );
				Game game = new Game( args.get( 0 ), box );
				game.board = board;
				game.oldBoard = board;
				Game.register( game );
				PlayerRep.getPlayerRep( (Player)sender ).setActiveGame( game );
				Chat.sendMessage(
						getMessage( Text.CONVERT_SUCCESS, sender ).put( "game",
								game ) );
			}
		} else {
			Chat.sendMessage( getMessage( Text.MUST_BE_PLAYER, sender ) );
		}
		return true;
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
