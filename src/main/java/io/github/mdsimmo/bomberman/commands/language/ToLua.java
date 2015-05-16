package io.github.mdsimmo.bomberman.commands.language;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Language;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

public class ToLua extends Cmd {

	private static final Plugin plugin = Bomberman.instance;
	
	public ToLua( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.TOLUA_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		if ( args.size() == 1 )
			return Language.allLanguages();
		else
			return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() != 2 )
			return false;
		
		String languageName = args.get( 0 );
		Language language = Language.getLanguage( languageName );
		if ( language == null && !languageName.equalsIgnoreCase( "english" )) {
			Chat.sendMessage( getMessage( Text.LANGUAGE_UNKNOWN, sender )
					.put( "lang", languageName ));
			return true;
		}
		
		File file = new File( plugin.getDataFolder(), args.get( 1 ) );
		
		if ( convert( language, file ) ) {
			Chat.sendMessage( getMessage( Text.TOLUA_SUCCESS, sender )
					.put( "file", file.getName() )
					.put( "lang", language ) );
		} else {
			Chat.sendMessage( getMessage( Text.TOLUA_FAILED, sender )
					.put( "file", file.getName() )
					.put( "lang", language ) );
		}
		return true;
	}
	
	private boolean convert( Language language, File file ) {
		StringBuffer output = new StringBuffer( 1000 ); // at a very rough guess
		for ( Phrase text : Text.values() ) {
			// if the language has not got a translation, skip it
			if ( !language.contains( text ) )
				continue;
			output.append( "L[\"" );
			// escape quotation marks
			output.append( text.getPath().replace( "\"", "\\\"" ) );
			output.append( "\"] = \"" );
			output.append( language.translate( text ).replace( "\"", "\\\"" ) );
			output.append( "\"\n" );
		}
		FileWriter out = null;
		try {
			out = new FileWriter( file );
			out.write( output.toString() );
		} catch ( IOException e ) {
			return false;
		} finally {
			if ( out != null )
				try {
					out.close();
				} catch ( IOException e ) {
					plugin.getLogger().warning( "output stream filed to close" );
				}
		}
		return true;
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.TOLUA_EXTRA, sender );
	}

	@Override
	public Message example( CommandSender sender ) {
		return getMessage( Text.TOLUA_EXAMPLE, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.TOLUA_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.TOLUA_USAGE, sender );
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
