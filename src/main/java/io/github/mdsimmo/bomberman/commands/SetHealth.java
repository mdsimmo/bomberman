package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SetHealth extends Cmd {

	private static Plugin plugin = Bomberman.instance;
	
	public SetHealth( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.SET_HEALTH_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		if ( args.size() == 1 ) {
			List<String> options = new ArrayList<>();
			for (Player p : Bukkit.getServer().getOnlinePlayers())
				options.add(p.getName());
			return options;
		}
		return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() < 1 || args.size() > 2 )
			return false;
		
		@SuppressWarnings( "deprecation" )
		Player player = plugin.getServer().getPlayer( args.get( 0 ) );
		if ( player == null ) {
			Chat.sendMessage( getMessage( Text.INVALID_PLAYER, sender ).put( "player", args.get( 0 ) ) );
			return true;
		}
		
		if ( args.size() == 1 ) {
			player.setMaxHealth( 20 );
			player.setHealth( 20 );
			player.setHealthScale( 20 );
			player.setHealthScaled( false );
			return true;
		}
		
		try {
			int health = Integer.parseInt( args.get( 1 ) );
			player.setMaxHealth( health );
			player.setHealth( health );
			player.setHealthScale( health );
			player.setHealthScaled( false );
			return true;
		} catch ( NumberFormatException e ) {
			Chat.sendMessage( getMessage( Text.INVALID_NUMBER, sender ).put( "number", args.get( 1 ) ) );
			return true;
		}
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.SET_HEALTH_EXTRA, sender );
	}

	@Override
	public Message example( CommandSender sender ) {
		return getMessage( Text.SET_HEALTH_EXAMPLE, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.SET_HEALTH_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.SET_HEALTH_USAGE, sender );
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}
	
	@Override
	public boolean isAllowedBy( CommandSender sender ) {
		return sender instanceof ConsoleCommandSender;
	}

}
