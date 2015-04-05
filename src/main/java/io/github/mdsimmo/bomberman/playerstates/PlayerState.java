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
	 * @return true if the state was successfully enabled or is already enabled
	 */
	public final boolean enable() {
		if ( enabled == true )
			return true;
		enabled = true;
		return enabled = onEnable();
	}

	public abstract boolean onEnable();

	/**
	 * Unenables the state
	 * @return true if the state was successfully unenabled or already unenabled.
	 */
	public boolean disable() {
		if ( !enabled )
			return true;
		return enabled = !onDisable();
	}

	public abstract boolean onDisable();

	public boolean isEnabled() {
		return enabled;
	}
}
