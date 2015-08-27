package com.github.mdsimmo.bomberman.commands;

import com.github.mdsimmo.bomberman.localisation.Phrase;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * A CmdGroup is a group of commands. Command groups do nothing themselves except pass tasks onto sub level commands.
 */
public abstract class CmdGroup implements Cmd {

    public List<Cmd> getChildren() {
        return CmdHandler.childrenOf( getClass() );
    }

    @Override
    public boolean hasPermission( CommandSender sender ) {
        // only has permission if all sublcasses have permission
        for ( Cmd cmd : getChildren() ) {
            if ( cmd.hasPermission( sender ) )
                return true;
        }
        return false;
    }

    @Override
    public boolean execute( CommandSender sender, List<String> args, List<String> options ) {
        for ( Cmd)
        return false;
    }
}
