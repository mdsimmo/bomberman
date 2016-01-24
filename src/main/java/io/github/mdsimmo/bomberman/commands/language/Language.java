package io.github.mdsimmo.bomberman.commands.language;

import org.bukkit.command.CommandSender;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

public class Language extends CommandGroup {

	public Language( Cmd parent ) {
		super( parent );
	}

	@Override
	public void setChildren() {
		addChildren( 
				new LangSet( this ),
				new FromLua( this ),
				new ToLua( this ),
				new Reload( this ) );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.LANGUAGE_GROUP_NAME, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.LANGUAGE_GROUP_DESCRIPTION, sender );
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

}
