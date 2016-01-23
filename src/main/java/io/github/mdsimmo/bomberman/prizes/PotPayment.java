package io.github.mdsimmo.bomberman.prizes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.messaging.Message;

public class PotPayment implements Payment {

	private final String name;
	private Game game;
	
	public PotPayment( Game game ) {
		this( game.name );
		this.game = game;
	}
	
	private PotPayment( String name ) {
		this.name = name;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>( 1 );
		map.put( "game", name );
		return map;
	}
	
	public static PotPayment deserialize( Map<String, Object> map ) {
        String name = (String)map.get( "game" );
        return new PotPayment( name );
    }

	@Override
	public String format( Message message, List<String> args ) {
		return "pot";
	}

	@Override
	public void giveTo( Player player ) {
		Game game = this.game;
		if ( game == null )
			game = this.game = Game.findGame( name );
		Payment fare = game.getFare();
		int players = game.playersJoined();
		for ( int i = 0; i < players; i++ )
			fare.giveTo( player );
	}

	@Override
	public boolean takeFrom( Player player ) {
		// never called. Only given
		return false;
	}

}
