package io.github.mdsimmo.bomberman.commands.language;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

public class Reload extends Cmd {

	public Reload( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return Text.LANG_RELOAD_NAME.getMessage( sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		io.github.mdsimmo.bomberman.messaging.Language.reload();		
		Chat.sendMessage( Text.LANG_RELOAD_SUCCESS.getMessage( sender ) );
		return true;
	}

	@Override
	public Message extra( CommandSender sender ) {
		return Text.LANG_RELOAD_EXTRA.getMessage( sender );
	}

	@Override
	public Message example( CommandSender sender ) {
		return Text.LANG_RELOAD_EXAMPLE.getMessage( sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return Text.LANG_RELOAD_DESCRIPTION.getMessage( sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return Text.LANG_RELOAD_USAGE.getMessage( sender );
	}


	@Override
	public boolean isAllowedBy( CommandSender sender ) {
		return sender instanceof ConsoleCommandSender;
	}
	
	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}
	
	

	
	

}
