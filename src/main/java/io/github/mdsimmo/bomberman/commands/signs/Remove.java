package io.github.mdsimmo.bomberman.commands.signs;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.CommandSign;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.playerstates.ArenaEdittingState;
import io.github.mdsimmo.bomberman.utils.BlockLocation;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class Remove extends Cmd {

	private static final Plugin plugin = Bomberman.instance;
	
	public Remove( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.SIGN_REMOVE_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() != 0 )
			return false;
		if ( sender instanceof Player == false ) {
			Chat.sendMessage( getMessage( Text.MUST_BE_PLAYER, sender ) );
			return true;
		}
		Chat.sendMessage( getMessage( Text.SIGN_REMOVE_PROMT_CLICK, sender ) );
		new ClickListener( (Player) sender );
		return true;
	}
	
	private class ClickListener implements Listener {
		
		private final Player player;
		
		public ClickListener( Player player ) {
			this.player = player;
			plugin.getServer().getPluginManager().registerEvents( this, plugin );
		}
		
		@EventHandler( priority = EventPriority.LOW )
		public void onBlockClicked( PlayerInteractEvent e ) {
			if ( e.isCancelled() )
				return;
			if ( e.getAction() != Action.RIGHT_CLICK_BLOCK )
				return;
			if ( e.getPlayer() != player )
				return;
			Block b = e.getClickedBlock();
			if ( CommandSign.removeSign( BlockLocation.getLocation( b ) ) ) {
				Chat.sendMessage( getMessage( Text.SIGN_REMOVE_SUCCESS, player )
						.put( "block", b ));
			} else {
				Chat.sendMessage( getMessage( Text.SIGN_REMOVE_NO_COMMANDS, player ).put( "block", b ) );
			}
			PlayerRep rep = PlayerRep.getPlayerRep( e.getPlayer() );
			if ( rep.getState() instanceof ArenaEdittingState ) {
				ArenaEdittingState state = (ArenaEdittingState)rep.getState();
				state.update( e.getClickedBlock() );
			}
			HandlerList.unregisterAll( this );
			e.setCancelled( true );
		}
		
	}
	
	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.SIGN_REMOVE_EXTRA, sender );
	}

	@Override
	public Message example( CommandSender sender ) {
		return getMessage( Text.SIGN_REMOVE_EXAMPLE, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.SIGN_REMOVE_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.SIGN_REMOVE_USAGE, sender );
	}

	@Override
	public Permission permission() {
		return Permission.SIGN_MAKER;
	}

}
