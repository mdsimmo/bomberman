package com.github.mdsimmo.bomberman.commands;

import com.github.mdsimmo.bomberman.localisation.Language;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * A CmdGroup is a group of commands. Command groups do nothing themselves except pass tasks onto sub level commands.
 */
public abstract class CmdGroup implements Cmd {

    private Set<Cmd> children = new LinkedHashSet<Cmd>();
    private Set<Cmd> immutableChildren = Collections.unmodifiableSet( children );

    /**
     * Creates a command group and registers all the passed children. More
     * children can still be registered/un-registered later.
     * @param children this commands children
     * @throws NullPointerException if any of the commands are null
     */
    public CmdGroup( Cmd ... children ) {
        for ( Cmd cmd : children )
            addChild( cmd );
    }

    /**
     * Gets an unmodifiable list of this classes children. As more children
     * are added, the passed list will be updated.
     * @return this commands children
     */
    public final Set<Cmd> getChildren() {
        return immutableChildren;
    }

    /**
     * Registers another child command with this command. If the command was
     * already registered, nothing will happen.
     * @param command the command to register
     * @return true if the command was added. false if the command was already
     * registered
     * @throws NullPointerException if command is null
     */
    public boolean addChild( Cmd command ) {
        if ( command == null )
            throw new NullPointerException( "command cannot be null" );
        return children.add( command );
    }

    /**
     * Un-registers a command from the command group. If the command was not
     * registered, nothing will be done.
     * @param command the command to de-register
     * @return true if the command was removed; false if the command was not
     * registered in the first place.
     * @throws NullPointerException if command is null
     */
    public boolean removeChild( Cmd command ) {
        if ( command == null )
            throw new NullPointerException( "command cannot be null" );
        return children.remove( command );
    }

    @Override
    public boolean hasPermission( CommandSender sender ) {
        // only has permission if any child command has permission
        for ( Cmd cmd : getChildren() ) {
            if ( cmd.hasPermission( sender ) )
                return true;
        }
        return false;
    }

    @Override
    public boolean execute( CommandSender sender, List<String> args, List<String> options ) {
        Language l = Language.of( sender );
        String name = args.get( 0 );
        args.remove( 0 );
        for ( Cmd cmd : getChildren() ) {
            if ( !cmd.hasPermission( sender ) )
                continue;
            if ( l.translate( cmd.name() ).equalsIgnoreCase( name ) )
                return cmd.execute( sender, args, options );
        }
        return false;
    }
}
