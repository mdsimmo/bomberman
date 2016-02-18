package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.Game.State;
import io.github.mdsimmo.bomberman.commands.Cmd.Permission;
import io.github.mdsimmo.bomberman.playerstates.GamePlayingState;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;

/**
 * Protects the game from any damage except that which is part of the game <br>
 */
public class GameProtection implements Listener {

	private final Game game;
	private Plugin plugin = Bomberman.instance;

	public GameProtection( Game game ) {
		this.game = game;
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onBlockBreak( BlockBreakEvent e ) {
		if ( game.getProtected( Config.PROTECT_DESTROYING )
				&& game.box.contains( e.getBlock().getLocation() )
				&& !Permission.PROTECTION_VOID.isAllowedBy( e.getPlayer() ) )
			e.setCancelled( true );
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlaceBlock( BlockPlaceEvent e ) {
		// protect from players placing blocks
		if ( game.getProtected( Config.PROTECT_PLACING )
				&& game.box.contains( e.getBlock().getLocation() )
				&& !Permission.PROTECTION_VOID.isAllowedBy( e.getPlayer() )
				&& !(PlayerRep.getPlayerRep( e.getPlayer() ).getState() instanceof GamePlayingState) )
			e.setCancelled( true );
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onBlockCobust( BlockBurnEvent e ) {
		if ( game.box.contains( e.getBlock().getLocation() ) ) {
			if ( game.state == State.PLAYING )
				e.setCancelled( true );
			if ( game.getProtected( Config.PROTECT_FIRE ) ) {
				e.setCancelled( true );
			}
		}
	}

	@EventHandler( priority = EventPriority.LOWEST)
	public void onBlockIgnite( BlockIgniteEvent e ) {
		if ( game.box.contains( e.getBlock().getLocation() ) ) {
			if ( game.state == State.PLAYING )
				e.setCancelled( true );
			if ( game.getProtected( Config.PROTECT_FIRE ) ) {
				if ( e.getPlayer() != null
						&& !Permission.PROTECTION_VOID.isAllowedBy( e
								.getPlayer() ) )
					e.setCancelled( true );
			}
		}
	}

	@EventHandler
	public void onFireSpread( BlockSpreadEvent e ) {
		if ( e.isCancelled() )
			return;
		if ( game.state == State.PLAYING && game.box.contains( e.getBlock().getLocation() ) )
			e.setCancelled( true );
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerDamage( EntityDamageEvent e ) {
		if ( e.isCancelled() )
			return;
		Entity entity = e.getEntity();
		if ( entity instanceof Player ) {
			if ( game.getProtected( Config.PROTECT_DAMAGE ) ) {
				if ( game.box.contains( entity.getLocation() ) ) {
					e.setCancelled( true );
				}
			}
		}
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPVP( EntityDamageByEntityEvent e ) {
		if ( e.isCancelled() )
			return;
		if ( e.getDamager() instanceof Player ) {
			Player player = (Player)e.getDamager();
			if ( game.getProtected( Config.PROTECT_PVP )
					&& game.box.contains( e.getDamager().getLocation() )
					&& !Permission.PROTECTION_VOID.isAllowedBy( player ) )
				e.setCancelled( true );
		}
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onExplosion( EntityExplodeEvent e ) {
		if ( e.isCancelled() )
			return;
		if ( game.getProtected( Config.PROTECT_EXPLOSIONS ) ) {
			if ( game.box.contains( e.getLocation() ) ) {
				e.setCancelled( true );
			}
		}
	}
}
