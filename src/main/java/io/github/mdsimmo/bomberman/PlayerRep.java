package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.messaging.Formattable;
import io.github.mdsimmo.bomberman.messaging.Language;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.playerstates.GamePlayingState;
import io.github.mdsimmo.bomberman.playerstates.PlayerState;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public class PlayerRep implements Listener, Formattable {

	private static JavaPlugin plugin = Bomberman.instance;
	private static HashMap<Player, PlayerRep> lookup = new HashMap<>();

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

	private final Player player;
	private Game game;

	private Language lang = Language.getLanguage( (String)Config.LANGUAGE
			.getValue() );
	private PlayerState state = null;

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
		if ( game != null && game.destroyed )
			this.game = null;
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
		if ( this.state != null && !this.state.disable() ) {
			System.out.println( "State failed to disable" );
			return false;
		}
		this.state = null;

		if ( state == null ) {
			System.out.println( "State removed" );
			return true;
		}

		if ( state.enable() ) {
			System.out.println( "State enabled" );
			this.state = state;
			return true;
		} else {
			System.out.println( "State failed to enable" );
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
	public String format( Message message, List<String> args ) {
		if ( args.size() == 0 )
			return player.getName();
		if ( args.size() != 1 )
			throw new RuntimeException( "Players can have at most one argument" );
		switch ( args.get( 0 ) ) {
		case "name":
			return player.getName();
		case "lives":
			return Integer.toString( (int)player.getHealth() );
		case "power":
			if ( isPlaying() ) {
				int amount = ((GamePlayingState)getState()).bombStrength();
				return Integer.toString( amount );
			} else {
				return "not playing";
			}
		case "bombs":
			if ( isPlaying() ) {
				int amount = ((GamePlayingState)getState()).bombAmount();
				return Integer.toString( amount );
			} else {
				return "not playing";
			}
		case "handicap":
			if ( isPlaying() ) {
				Game game = ((GamePlayingState)getState()).getGame();
				game.getStats( this );
				int amount = game.getStats( this ).hadicapLevel;
				return Integer.toString( amount );
			} else {
				return "not playing";
			}
		default:
			return null;
		}
	}

	public boolean isPlaying() {
		return state instanceof GamePlayingState;
	}

}
