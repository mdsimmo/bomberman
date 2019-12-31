package io.github.mdsimmo.bomberman.commands.language;

import io.github.mdsimmo.bomberman.game.GamePlayer;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Language;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LangSet extends Cmd {

	public LangSet(Cmd parent) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return getMessage( Text.LANGUAGE_NAME, sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		return Language.allLanguages();
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() != 1 )
			return false;

		if ( sender instanceof Player == false ) {
			Chat.sendMessage( getMessage( Text.MUST_BE_PLAYER, sender ) );
			return true;
		}

		Language lang = Language.getLanguage( args.get( 0 ) );
		if ( lang == null ) {
			if ( args.get( 0 ).equalsIgnoreCase( "english" ) ) {
				GamePlayer.getPlayerRep( (Player)sender ).setLanguage( null );
				Chat.sendMessage(
						getMessage( Text.LANGUAGE_SUCCESS, sender ).put(
								"lang", args.get( 0 ) ) );
			} else
				Chat.sendMessage(
						getMessage( Text.LANGUAGE_UNKNOWN, sender ).put( "lang" , args.get( 0 ) ) );
		} else {
			GamePlayer.getPlayerRep( (Player)sender ).setLanguage( lang );
			Chat.sendMessage( getMessage( Text.LANGUAGE_SUCCESS, sender ).put( "lang", lang ) );
		}
		return true;
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.LANGUAGE_EXTRA, sender );
	}

	@Override
	public Message example( CommandSender sender ) {
		String lang = Utils.random( Language.allLanguages() );
		lang = lang == null ? "mylang" : lang;
		return getMessage( Text.LANGUAGE_EXAMPLE, sender )
				.put( "example", lang );
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.LANGUAGE_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.LANGUAGE_USAGE, sender );
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

}
