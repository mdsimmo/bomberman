package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class GameCommand extends Cmd {

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( nameShort(), sender );
	}
	
	public abstract Phrase nameShort();
	
	public GameCommand( Cmd parent ) {
		super( parent );
	}

	public final List<String> options( CommandSender sender, List<String> args ) {
		if ( args.size() <= 1 ) {
			List<String> list = new ArrayList<>( Game.allGames() );
			if ( sender instanceof Player
					&& PlayerRep.getPlayerRep( (Player)sender ).getActiveGame() != null ) {
				List<String> options = shortOptions( sender, args );
				if ( options != null )
					list.addAll( options );
			}
			return list;
		} else {
			args.remove( 0 );
			return shortOptions( sender, args );
		}
	}

	public abstract List<String> shortOptions( CommandSender sender,
			List<String> args );

	@Override
	public final boolean run( CommandSender sender, List<String> args ) {
		Game game;
		if ( args.size() >= 1 && Game.allGames().contains( args.get( 0 ) ) ) {
			game = Game.findGame( args.get( 0 ) );
			if ( sender instanceof Player )
				PlayerRep.getPlayerRep( (Player)sender ).setActiveGame( game );
			args.remove( 0 );
		} else {
			if ( sender instanceof Player )
				game = PlayerRep.getPlayerRep( (Player)sender ).getActiveGame();
			else
				return false;
		}
		return runShort( sender, args, game );
	}

	public abstract boolean runShort( CommandSender sender, List<String> args,
			Game game );

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( extraShort(), sender );
	}

	public abstract Phrase extraShort();

	@Override
	public Message example( CommandSender sender ) {
		String game = Utils.random( Game.allGames() );
		if ( game == null )
			game = "mygame";
		return getMessage( exampleShort(), sender ).put( "example", game );
	}

	public abstract Phrase exampleShort();

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( descriptionShort(), sender );
	}

	public abstract Phrase descriptionShort();

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( usageShort(), sender );
	}

	public abstract Phrase usageShort();

}