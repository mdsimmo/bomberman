package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.commands.arena.Arena;
import io.github.mdsimmo.bomberman.commands.game.Game;
import io.github.mdsimmo.bomberman.commands.signs.Sign;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

public class BaseCommand extends CommandGroup implements TabCompleter,
		CommandExecutor {

	private static final JavaPlugin plugin = Bomberman.instance;

	public BaseCommand() {
		super( null );
		plugin.getCommand( "bomberman" ).setExecutor( this );
		plugin.getCommand( "bomberman" ).setTabCompleter( this );
	}

	@Override
	public void setChildren() {
		addChildren( new Game( this ), new Arena( this ),
				new Sign( this ), new LanguageCmd( this ) );
	}

	@Override
	public Message name( CommandSender sender ) {
		return new Message(sender, "bomberman");
	}

	@Override
	public Permission permission() {
		return Permission.OBSERVER;
	}

	@Override
	public Message description( CommandSender sender ) {
		return Text.BOMBERMAN_DESCRIPTION.getMessage( sender );
	}

	@Override
	public boolean onCommand( CommandSender sender, Command command, String s,
			String[] args ) {
		List<String> arguments = new ArrayList<String>( Arrays.asList( args ) );
		Cmd c = getCommand( sender, arguments );
		if ( arguments.size() > 0
				&& arguments.get( arguments.size() - 1 ).equals( "?" ) ) {
			c.help( sender );
			return true;
		}
		if ( !c.execute( sender, arguments ) ) {
			c.incorrectUsage( sender, arguments );
			c.help( sender );
		}
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, Command command,
			String s, String[] args ) {
		List<String> arguments = new ArrayList<String>( Arrays.asList( args ) );
		Cmd c = getCommand( sender, arguments );
		List<String> options = new ArrayList<>();
		List<String> all = c.options( sender, arguments );
		if ( all == null )
			all = new ArrayList<>();
		all.add( "?" );
		for ( String option : all ) {
			if ( StringUtil
					.startsWithIgnoreCase( option, args[args.length - 1] ) )
				options.add( option );
		}
		return options;
	}
}
