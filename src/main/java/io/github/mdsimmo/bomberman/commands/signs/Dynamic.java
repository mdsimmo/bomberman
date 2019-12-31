package io.github.mdsimmo.bomberman.commands.signs;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.DynamicSigns;
import io.github.mdsimmo.bomberman.DynamicSigns.DynamicSign;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.BlockLocation;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Dynamic extends GameCommand {

	public Dynamic( Cmd parent ) {
		super( parent );
	}

	@Override
	public Phrase nameShort() {
		return Text.SIGN_DYNAMIC_NAME;
	}

	@Override
	public List<String> shortOptions( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean runShort( CommandSender sender, List<String> args, Game game ) {
		if ( args.size() < 2 )
			return false;
		
		if ( sender instanceof Player == false ) {
			Chat.sendMessage( getMessage( Text.MUST_BE_PLAYER, sender ) );
			return true;
		}
		
		String lineNo = args.get( 0 );
		int line;
		try {
			line = Integer.parseInt( lineNo );
		} catch ( NumberFormatException e ) {
			Chat.sendMessage( getMessage( Text.INVALID_NUMBER, sender ).put( "number", lineNo ) );
			return true;
		}
		if ( line < 0 || line >= 4 ) {
			Chat.sendMessage( getMessage( Text.INVALID_NUMBER, sender ).put( "number", lineNo ) );
			return true;
		}
		
		args.remove( 0 );
		String text = Utils.listToString( args );
		
		// validate the message
		try { 
			new Message( null, text ).validate();
		} catch ( Exception e ) {
			Chat.sendMessage( getMessage( Text.SIGN_DYNAMIC_ERROR, sender ).put( "error", e.getMessage() ) );
			return true;
		}
			
		new ClickListener( game, text, line, (Player)sender );
		
		Chat.sendMessage( getMessage( Text.SIGN_DYNAMIC_PROMT_CLICK, sender ) );
		return true;
	}
	
	private class ClickListener implements Listener {
		
		final String text;
		final int line;
		final Player player;
		final Game game;
		
		public ClickListener( Game game, String text, int line, Player player ) {
			this.text = text;
			this.line = line;
			this.player = player;
			this.game = game;
			Bomberman.instance.getServer().getPluginManager().registerEvents( this, Bomberman.instance );
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
			if ( b.getState() instanceof Sign == false ) {
				Chat.sendMessage( getMessage( Text.SIGN_DYNAMIC_NOT_SIGN, player ) );
			} else {
				DynamicSign sign = new DynamicSign( BlockLocation.getLocation( b ), line, text, game );	
				DynamicSigns.enable( sign );
				Chat.sendMessage( getMessage( Text.SIGN_DYNAMIC_SUCCESS, player ) );
			}
			HandlerList.unregisterAll( this );
			e.setCancelled( true );
		}
		
	}
	
	@Override
	public Phrase extraShort() {
		return Text.SIGN_DYNAMIC_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.SIGN_DYNAMIC_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.SIGN_DYNAMIC_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.SIGN_DYNAMIC_USAGE;
	}

	@Override
	public Permission permission() {
		return Permission.SIGN_MAKER;
	}
}
