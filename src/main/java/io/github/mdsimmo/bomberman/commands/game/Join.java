package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.Game.State;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.playerstates.GamePlayingState;
import io.github.mdsimmo.bomberman.playerstates.PlayerState;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Join extends GameCommand {

	public Join( Cmd parent ) {
		super( parent );
	}

	@Override
	public Phrase nameShort() {
		return Text.JOIN_NAME;
	}

	@Override
	public List<String> shortOptions( CommandSender sender, List<String> args ) {
		if (args.size() == 1) {
			List<String> values = new ArrayList<String>();
			for ( DyeColor color : DyeColor.values() )
				values.add( color.toString() );
			//return values;
			return null;
		}
		return null;
	}

	@Override
	public boolean runShort( CommandSender sender, List<String> args, Game game ) {
		if ( args.size() != 0 )
			return false;
		if ( sender instanceof Player == false ) {
			Chat.sendMessage( getMessage( Text.MUST_BE_PLAYER, sender ) );
			return true;
		}
		if ( game.state == State.PLAYING || game.state == State.ENDING ) {
			Chat.sendMessage( getMessage( Text.JOIN_GAME_STARTED, sender ).put(
					"game", game ) );
			return true;
		}
		PlayerRep rep = PlayerRep.getPlayerRep( (Player)sender );
		DyeColor team = null;
		if ( args.size() == 1 )
			try {
				team = DyeColor.valueOf( args.get( 0 ).toUpperCase() );
			} catch (Exception e) {
				return false;
			}
		if ( !rep.switchStates( new GamePlayingState( rep, team ) ) ) {
			PlayerState state = rep.getState();
			Game activeGame = state.getGame();
			Message message = Text.PLAYER_BUSY.getMessage( rep );
			message.put( "game", activeGame );
			Chat.sendMessage( message );
			return true;
		}
		rep.setActiveGame( game );
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.PLAYER;
	}

	@Override
	public Phrase extraShort() {
		return Text.JOIN_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.JOIN_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.JOIN_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.JOIN_USAGE;
	}

}
