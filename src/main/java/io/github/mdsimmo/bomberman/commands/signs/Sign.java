package io.github.mdsimmo.bomberman.commands.signs;

import org.bukkit.command.CommandSender;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

public class Sign extends CommandGroup {

	public Sign( Cmd parent ) {
		super( parent );
	}

	@Override
	public void setChildren() {
		addChildren( 
				new Add( this ), new Remove( this ) );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.SIGN_NAME, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.SIGN_DESCRIPTION, sender );
	}

	@Override
	public Permission permission() {
		return Permission.SIGN_MAKER;
	}

	
	
}
