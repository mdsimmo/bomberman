package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.arena.ArenaTemplate;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.Game.State;
import io.github.mdsimmo.bomberman.arena.ArenaGenerator;
import io.github.mdsimmo.bomberman.arena.ArenaGenerator.BuildListener;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.utils.Box;
import io.github.mdsimmo.bomberman.utils.Utils;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Arena extends GameCommand {

	public Arena( Cmd parent ) {
		super( parent );
	}

	@Override
	public Phrase nameShort() {
		return Text.SETARENA_NAME;
	}

	@Override
	public List<String> shortOptions( CommandSender sender, List<String> args ) {
		if ( args
				.size() == 1 )
			return ArenaGenerator.allBoards();
		else
			return null;
	}

	@Override
	public boolean runShort( final CommandSender sender, List<String> args,
			final Game game ) {
		if ( args
				.size() != 1 )
			return false;

		if ( game.state != State.WAITING ) {
			Chat.sendMessage( getMessage(
					Text.SETARENA_GIP, sender )
					.put( "game", game ) );
			return true;
		}

		final ArenaTemplate newboard = ArenaGenerator
				.loadBoard( args
						.get( 0 ) );
		if ( newboard == null ) {
			Chat.sendMessage( getMessage(
					Text.INVALID_ARENA, sender )
					.put( "arena", args.get( 0 ) ) );
			return true;
		}

		game.stop();

		final ArenaTemplate oldboard = game.getArena();

		BuildListener l = new BuildListener() {
			@Override
			public void onContructionComplete() {
				game.setArena(newboard);
				Box box = game.getBox();
				box.xSize = newboard.xSize;
				box.ySize = newboard.ySize;
				box.zSize = newboard.zSize;
				game.oldArena = ArenaGenerator.createArena(	game.getName() + ".old", game.getBox());
				BuildListener l2 = new BuildListener() {
					@Override
					public void onContructionComplete() {
						Message message = getMessage(
								Text.SETARENA_SUCCESS, sender )
								.put( "game", game )
								.put( "arena1", oldboard )
								.put( "arena2", newboard );
						Chat.sendMessage( message );
					}
				};
				ArenaGenerator.switchBoard(	game.oldArena, newboard, game.getBox(), l2 );
			}
		};
		ArenaGenerator.switchBoard(game.getArena(), game.oldArena, game.getBox(), l );
		Message message = getMessage(
				Text.SETARENA_STARTED, sender )
				.put( "game", game )
				.put( "arena1", oldboard )
				.put( "arena2", newboard );
		Chat.sendMessage( message );
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Phrase extraShort() {
		return Text.SETARENA_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		throw new RuntimeException( "This method is invalid" );
	}

	@Override
	public Message example( CommandSender sender ) {
		String game = Utils.random( Game.allGames() );
		game = game == null ? "mygame" : game;
		String arena = Utils.random( ArenaGenerator.allBoards() );
		arena = arena == null ? "myarena" : game;
		return getMessage(
				Text.SETARENA_EXAMPLE, sender )
				.put( "game", game )
				.put( "arena", arena );
	}

	@Override
	public Phrase descriptionShort() {
		return Text.SETARENA_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.SETARENA_USAGE;
	}

}
