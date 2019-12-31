package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Forget extends Cmd {

	public Forget( Cmd parent ) {
		super( parent );
	}

	@Override
	public Message name( CommandSender sender ) {
		return Text.FORGET_NAME.getMessage( sender );
	}

	@Override
	public List<String> options( CommandSender sender, List<String> args ) {
		if ( args.size() == 1 )
			return Game.allGames();
		return null;
	}

	@Override
	public boolean run( CommandSender sender, List<String> args ) {
		if ( args.size() != 1 )
			return false;
		String name = args.get( 0 );
		Game game = Game.findGame( name );
		if ( game == null)
			Chat.sendMessage( getMessage( Text.INVALID_GAME, sender ).put( "game", name ) );
		else {
			game.destroy();
			Chat.sendMessage( getMessage( Text.FORGET_SUCCESS, sender ).put( "game", name ) );
		}
		return true;
	}

	@Override
	public Message extra( CommandSender sender ) {
		return getMessage( Text.FORGET_EXTRA, sender );
	}

	@Override
	public Message example( CommandSender sender ) {
		String game = Utils.random(Game.allGames());
		if (game == null)
			game = "mygame";
		return getMessage(Text.FORGET_EXAMPLE, sender).put( "example", game);
	}

	@Override
	public Message description( CommandSender sender ) {
		return getMessage( Text.FORGET_DESCRIPTION, sender );
	}

	@Override
	public Message usage( CommandSender sender ) {
		return getMessage( Text.FORGET_USAGE, sender );
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}


}
