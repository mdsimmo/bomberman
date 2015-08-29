package com.github.mdsimmo.bomberman.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * A CmdHandler handles the interface between Bukkit's command system and Bomberman's
 * command system. This class will handle correct execution of commands and
 * tab completion. The command must be registered with
 * {@link org.bukkit.command.PluginCommand#setExecutor(CommandExecutor)}
 */
public final class CmdHandler implements CommandExecutor {

    private final Cmd startCmd;

    /**
     * Creates a CmdHandler to handle all execution for the specific command.
     * @param command the command to pass off execution to
     */
    public CmdHandler( Cmd command ) {
        if ( command == null )
            throw new NullPointerException( "command cannot be null" );
        this.startCmd = command;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        // sort out passed arguments
        List<String> arguments = new ArrayList<String>();
        List<String> options = new ArrayList<String>();
        for ( String arg : args ) {
            if ( arg.startsWith( "-" ) )
                options.add( arg.substring( 1 ) );
            else
                arguments.add( arg );
        }
        return startCmd.execute( sender, arguments, options );
    }
}
