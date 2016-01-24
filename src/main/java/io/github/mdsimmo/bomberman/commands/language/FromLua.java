package io.github.mdsimmo.bomberman.commands.language;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class FromLua extends Cmd {

	private static final Plugin plugin = Bomberman.instance;
	
	public FromLua( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.FROMLUA_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() != 2 )
			return false;
		File file = new File( plugin.getDataFolder(), args.get( 0 ) );
		if ( !file.exists() )
			Chat.sendMessage( getMessage( Text.FROMLUA_FILE_NOT_FOUND, sender )
					.put( "file", args.get( 0 ) ));
		String lua = null;
		try { 
			lua = new String( Utils.readFile( file ) );
		} catch ( IOException e ) {
			Chat.sendMessage( getMessage( Text.FROMLUA_FAILED, sender )
					.put( "file", file.getName() )
					.put( "lang", args.get( 1 ) ));
			return true;
		}
		YamlConfiguration c = new YamlConfiguration();
		for ( Phrase text : Text.values() ) {
			String path = text.getPath().replace( "\"", "\\\"" );
			Pattern p = Pattern.compile( "L\\[\\\"" + path + "\\\"\\] = \\\"(.*)\\\"" );
			Matcher matcher = p.matcher( lua );
			if ( !matcher.find() )
				continue;
			String value = matcher.group( 1 );
			// replace escaped lua quotes with normal quotes
			value.replace( "\\\"", "\"" );
			c.set( text.getPath(), value );
		}
		
		try {
			c.save( new File( plugin.getDataFolder(), args.get( 1 ) + ".lang" ) );
		} catch ( IOException e ) {
			Chat.sendMessage( getMessage( Text.FROMLUA_FAILED, sender )
					.put( "file", file.getName() )
					.put( "lang", args.get( 1 ) ));
			return true;
		}
		
		Chat.sendMessage( getMessage( Text.FROMLUA_SUCCESS, sender )
				.put( "file", file.getName() )
				.put( "lang", args.get( 1 ) ));
		return true;
	}
	
	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.FROMLUA_EXTRA, sender );
	}

	@Override
	public Message example( CommandSender sender ) {
		return getMessage( Text.FROMLUA_EXAMPLE, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.FROMLUA_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.FROMLUA_USAGE, sender );
	}

	@Override
	public Permission permission() {
		return Permission.OVERLORD;
	}

}
