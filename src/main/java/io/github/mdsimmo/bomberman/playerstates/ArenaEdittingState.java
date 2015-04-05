package io.github.mdsimmo.bomberman.playerstates;

import io.github.mdsimmo.bomberman.BlockRep;
import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

public class ArenaEdittingState extends PlayerState implements Listener {

	private final Game game;
	private LinkedHashMap<Block, BlockRep> changes;

	public ArenaEdittingState( PlayerRep rep ) {
		super( rep );
		this.game = rep.getActiveGame();
	}

	@Override
	public boolean onEnable() {
		if ( rep.getState() != null )
			return false;
		if ( game == null )
			return false;
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
		if ( changes == null )
			changes = new LinkedHashMap<>();
		else
			changes.clear();
		return true;
	}

	@Override
	public boolean onDisable() {
		HandlerList.unregisterAll( this );
		return true;
	}

	@EventHandler
	public void onBlockPlaced( BlockPlaceEvent e ) {
		if ( e.isCancelled() || !enabled || e.getPlayer() != player )
			return;
		Block b = e.getBlock();
		if ( game.box.contains( b.getLocation() ) )
			changes.put( b, BlockRep.createBlock( e.getBlockReplacedState() ) );
		else {
			e.setCancelled( true );
			Message message = Text.EDIT_BUILD_DENIED.getMessage( rep.getPlayer() );
			message.put( "game", game );
			message.put( "player", rep );
			message.put( "block", b );
			Chat.sendMessage( rep, message );
		}
	}

	@EventHandler
	public void onBlockBreak( BlockBreakEvent e ) {
		if ( e.isCancelled() || !enabled || e.getPlayer() != player )
			return;
		Block b = e.getBlock();
		if ( game.box.contains( b.getLocation() ) ) {
			changes.put( e.getBlock(), BlockRep.createBlock( e.getBlock() ) );
		} else {
			e.setCancelled( true );
			Message message = Text.EDIT_DESTROY_DENIED.getMessage( player );
			message.put( "game", game );
			message.put( "player", rep );
			message.put( "block", b );
			Chat.sendMessage( rep, message );
		}
	}

	@EventHandler
	public void onPlayerLeave( PlayerQuitEvent e ) {
		if ( !enabled || e.getPlayer() != player )
			return;
		saveChanges();
	}

	public void saveChanges() {
		for ( Block b : changes.keySet() ) {
			Vector v = b.getLocation()
					.subtract( game.box.x, game.box.y, game.box.z ).toVector();
			if ( game.box.contains( b.getLocation() ) )
				game.board.addBlock( BlockRep.createBlock( b ), v );
		}
		BoardGenerator.saveBoard( game.board );
		rep.switchStates( null );
	}

	public void discardChanges( boolean remove ) {
		if ( remove ) {
			for ( Map.Entry<Block, BlockRep> entry : changes.entrySet() ) {
				Block current = entry.getKey();
				BlockRep previous = entry.getValue();
				previous.setBlock( current );
			}
		}
		rep.switchStates( null );
	}

	public Game getGame() {
		return game;
	}
}
