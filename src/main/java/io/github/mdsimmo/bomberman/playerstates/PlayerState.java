package io.github.mdsimmo.bomberman.playerstates;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.PlayerRep;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class PlayerState {

	protected final PlayerRep rep;
	protected final Player player;
	protected boolean enabled = false;
	protected Plugin plugin = Bomberman.instance;

	public PlayerState( PlayerRep rep ) {
		this.rep = rep;
		this.player = rep.getPlayer();
	}

	/**
	 * Enables this state
	 * 
	 * @return true if the state was successfully enabled or is already enabled
	 */
	public final boolean enable() {
		if ( enabled == true )
			return true;
		enabled = true;
		return enabled = onEnable();
	}

	/**
	 * A call to say that the state is being enabled. Initialisation activities
	 * can be performed here. If the state is in an invalid state and should not
	 * be enabled, return false;
	 * 
	 * @return true if the state can be enabled.
	 */
	public abstract boolean onEnable();

	/**
	 * Disables the state
	 * 
	 * @return true if the state was successfully disabled or was already
	 *         disabled.
	 */
	public boolean disable() {
		if ( !enabled ) {
			System.out.println( "All ready disabled" );
			return true;
		}
		boolean success = onDisable();
		enabled = !success;
		return success;
	}

	/**
	 * A call to say that this state is being disabled. If the state cannot be
	 * disabled at the time, then return false to signify that the player's
	 * state should not be changed
	 * 
	 * @return true if the state can be disabled. False otherwise
	 */
	public abstract boolean onDisable();

	public boolean isEnabled() {
		return enabled;
	}
}
