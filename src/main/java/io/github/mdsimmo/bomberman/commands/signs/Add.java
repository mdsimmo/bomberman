package io.github.mdsimmo.bomberman.commands.signs;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.CommandSign;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.playerstates.ArenaEditingState;
import io.github.mdsimmo.bomberman.utils.BlockLocation;
import io.github.mdsimmo.bomberman.utils.Utils;

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

public class Add extends Cmd {

	private static final Plugin plugin = Bomberman.instance;
	
	public Add( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.SIGN_ADD_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() == 0 )
			return false;
		if ( sender instanceof Player == false ) {
			Chat.sendMessage( getMessage( Text.MUST_BE_PLAYER, sender ) );
			return true;
		}
		String command = Utils.listToString( args );
		Chat.sendMessage( getMessage( Text.SIGN_ADD_PROMT_CLICK, sender ).put( "command", command ) );
		new ClickListener( command, (Player) sender );
		return true;
	}
	
	private class ClickListener implements Listener {
		
		private String command;
		private Player player;
		
		public ClickListener( String command, Player player ) {
			this.command = command;
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
			Chat.sendMessage( getMessage( Text.SIGN_ADD_ADDED, player )
					.put( "command", command )
					.put( "block", b ));
			if (command.charAt( 0 ) == '\\' || command.charAt( 0 ) == '/' )
				command = command.substring( 1 );
			PlayerRep rep = PlayerRep.getPlayerRep( e.getPlayer() );
			if ( rep.getState() instanceof ArenaEditingState ) {
				ArenaEditingState state = (ArenaEditingState)rep.getState();
				state.update( e.getClickedBlock() );
			}
			CommandSign.addCommand( BlockLocation.getLocation( b ), command );
			HandlerList.unregisterAll( this );
			e.setCancelled( true );
		}
		
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.SIGN_ADD_EXTRA, sender );
	}

	@Override
	public Message example( CommandSender sender ) {
		return getMessage( Text.SIGN_ADD_EXAMPLE, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.SIGN_ADD_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.SIGN_ADD_USAGE, sender );
	}

	@Override
	public Permission permission() {
		return Permission.SIGN_MAKER;
	}

}
