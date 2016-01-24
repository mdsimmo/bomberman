package io.github.mdsimmo.bomberman.commands.language;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class NewLang extends Cmd {

	public NewLang( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.NEW_LANG_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() != 1 )
			return false;
		
		String langName = args.get( 0 );
		
		JavaPlugin plugin = Bomberman.instance;		
		File outFile = new File( plugin.getDataFolder(), langName + ".lang" );
		
		try {
			outFile.createNewFile();
			InputStream in = plugin.getResource( "english.lang" );
			FileOutputStream fos = new FileOutputStream( outFile );
			int read = 0;
			byte[] bytes = new byte[1024];

			while ( ( read = in.read( bytes ) ) != -1 )
				fos.write( bytes, 0, read );

			fos.flush();
			fos.close();
			in.close();
			Chat.sendMessage( getMessage( Text.NEW_LANG_SUCCESS, sender ).put( "lang", langName ) );
		} catch ( IOException e ) {
			plugin.getLogger().log( Level.WARNING, "Couldn't create language file: '" + langName + ".lang'", e );
			Chat.sendMessage( getMessage( Text.NEW_LANG_FAIL, sender ).put( "lang", langName ) );			
		}		
		return true;
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.NEW_LANG_EXTRA, sender );
	}

	@Override
	public Message example( CommandSender sender ) {
		return getMessage( Text.NEW_LANG_EXAMPLE, sender );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.NEW_LANG_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.NEW_LANG_USAGE, sender );
	}

	@Override
	public Permission permission() {
		return Permission.OVERLORD;
	}

}
