package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.messaging.Formattable;
import io.github.mdsimmo.bomberman.messaging.Language;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.playerstates.GamePlayingState;
import io.github.mdsimmo.bomberman.playerstates.PlayerState;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public class PlayerRep implements Listener, Formattable {

	private static JavaPlugin					plugin	= Bomberman.instance;
	private static HashMap<Player, PlayerRep>	lookup	= new HashMap<>();

	public static PlayerRep getPlayerRep( Player player ) {
		PlayerRep rep = lookup.get( player );
		if ( rep == null )
			return new PlayerRep( player );
		else
			return rep;
	}

	public static Collection<PlayerRep> allPlayers() {
		return lookup.values();
	}

	public static Language getLanguage( CommandSender sender ) {
		if ( sender instanceof Player == false )
			return null;
		PlayerRep rep = lookup.get( sender );
		return rep == null ? null : rep.getLanguage();
	}

	private final Player	player;
	private Game			game;

	private Language		lang	= Language
											.getLanguage( (String)Config.LANGUAGE
													.getValue() );
	private PlayerState		state	= null;

	public PlayerRep( Player player ) {
		this.player = player;
		lookup.put( player, this );
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
	}

	/**
	 * Gets the game that the player changed last
	 * 
	 * @return the active game
	 */
	public Game getActiveGame() {
		return game;
	}

	/**
	 * Sets the game that the player last did something to
	 * 
	 * @param game
	 *            the active game
	 */
	public void setActiveGame( Game game ) {
		this.game = game;
		if ( game != null && !game.observers.contains( this ) )
			game.observers.add( this );
	}

	/**
	 * @return the Player this PlayerRep represents
	 */
	public Player getPlayer() {
		return player;
	}

	public PlayerState getState() {
		return state;
	}

	/**
	 * Sets the current state of the player. This method will try to enable the
	 * state. if the state fails to be enabled, nothing happens.
	 * 
	 * @param state
	 *            the state to switch to. null to remove current state
	 * @return true if the state was successfully added
	 */
	public boolean switchStates( PlayerState state ) {
		if ( this.state != null && !this.state.disable() )
			return false;

		this.state = null;

		if ( state == null )
			return true;

		if ( state.enable() ) {
			this.state = state;
			return true;
		} else {
			return false;
		}
	}

	public void removeEffects() {
		if ( plugin.isEnabled() )
			plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask( plugin, new Runnable() {
						@Override
						public void run() {
							player.setFireTicks( 0 );
							for ( PotionEffect effect : player
									.getActivePotionEffects() ) {
								plugin.getLogger().info(
										effect.getType().toString() );
								player.removePotionEffect( effect.getType() );
							}
						}
					} );
	}

	public Language getLanguage() {
		return lang;
	}

	public void setLanguage( Language lang ) {
		this.lang = lang;
	}

	@Override
	public Object format( Message message, String value ) {
		if ( value == null )
			return player.getName();
		switch ( value ) {
		case "name":
			return player.getName();
		case "lives":
			return Integer.toString( (int)player.getHealth() );
		case "bombs":
			return "FIXME"; // FIXME
		case "power":
			return "FIXME";
		default:
			return null;
		}
	}

	public boolean isPlaying() {
		return state instanceof GamePlayingState;
	}

}
