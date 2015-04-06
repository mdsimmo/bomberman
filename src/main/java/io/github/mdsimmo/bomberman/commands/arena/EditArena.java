package io.github.mdsimmo.bomberman.commands.arena;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.playerstates.ArenaEdittingState;
import io.github.mdsimmo.bomberman.playerstates.PlayerState;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditArena extends Cmd {

	public EditArena( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.EDIT_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		if ( sender instanceof Player == false )
			return null;
		PlayerRep rep = PlayerRep.getPlayerRep( (Player)sender );
		if ( args.size() == 1 ) {
			if ( rep.getState() instanceof ArenaEdittingState ) {
				List<String> list = new ArrayList<>();
				list.add( getMessage( Text.EDIT_SAVE, sender ).toString() );
				list.add( getMessage( Text.EDIT_DISCARD, sender ).toString() );
				list.add( getMessage( Text.EDIT_IGNORE, sender ).toString() );
				return list;
			} else {
				return Game.allGames();
			}
		} else
			return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() > 1 )
			return false;

		if ( sender instanceof Player == false ) {
			Chat.sendMessage( getMessage( Text.MUST_BE_PLAYER, sender ) );
			return true;
		}
		Player player = (Player)sender;
		PlayerRep rep = PlayerRep.getPlayerRep( player );
		PlayerState state = rep.getState();
		Game game;
		if ( state instanceof ArenaEdittingState )
			game = ( (ArenaEdittingState)state ).getGame();
		else
			game = rep.getActiveGame();
		Board board = null;
		if ( game != null )
			board = game.board;

		if ( args.size() == 0 ) {
			if ( state instanceof ArenaEdittingState )
				Chat.sendMessage(
						getMessage( Text.EDIT_ALREADY_STARTED, sender ).put(
								"game", game ).put( "arena", board ) );
			else if ( rep.getState() != null )
				Chat.sendMessage(
						getMessage( Text.EDIT_CANT_START, sender ).put( "game",
								game ).put( "arena", board ) );
			if ( game != null ) {
				Chat.sendMessage(
						getMessage( Text.EDIT_STARTED, sender ).put( "game",
								game ).put( "arena", board ) );
			} else {

			}
		} else {

			String arg = args.get( 0 );
			String save = getMessage( Text.EDIT_SAVE, sender ).toString();
			String discard = getMessage( Text.EDIT_DISCARD, sender ).toString();
			String ignore = getMessage( Text.EDIT_IGNORE, sender ).toString();
			ArenaEdittingState editState = null;
			if ( state instanceof ArenaEdittingState )
				editState = (ArenaEdittingState)state;
			else
				Chat.sendMessage(
						getMessage( Text.EDIT_PROMPT_START, sender ).put(
								"game", game ).put( "arena", board ) );

			if ( save.equalsIgnoreCase( arg ) ) {
				editState.saveChanges();
				Chat.sendMessage(
						getMessage( Text.EDIT_CHANGES_SAVED, sender ).put(
								"game", game ).put( "arena", board ) );
			} else if ( discard.equalsIgnoreCase( arg ) ) {
				editState.discardChanges( true );
				Chat.sendMessage(
						getMessage( Text.EDIT_CANGES_REMOVED, sender ).put(
								"game", game ).put( "arena", board ) );
			} else if ( ignore.equalsIgnoreCase( arg ) ) {
				editState.discardChanges( false );
				Chat.sendMessage( getMessage( Text.EDIT_MODE_QUIT, sender ).put(
						"game", game ).put( "arena", board ) );
			} else {
				Game game2 = Game.findGame( args.get( 0 ) );
				if ( game2 == null )
					return false;
				else {
					rep.setActiveGame( game2 );
					args.remove( 0 );
					return run( sender, args );
				}
			}
		}
		return true;
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.EDIT_EXTRA, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.EDIT_USAGE, sender );
	}

	@Override
	public Permission permission() {
		return Permission.ARENA_EDITING;
	}

	@Override
	public Message example( CommandSender sender ) {
		String game = Utils.random( Game.allGames() );
		if ( game == null )
			game = "mygame";
		return getMessage( Text.EDIT_EXAMPLE, sender).put( "example", game );
	}

	@Override
	public Message description( CommandSender sender  ) {
		return getMessage( Text.EDIT_DESCRIPTION, sender );
	}

}
