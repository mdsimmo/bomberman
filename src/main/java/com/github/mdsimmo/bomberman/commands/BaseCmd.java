package com.github.mdsimmo.bomberman.commands;

import com.github.mdsimmo.bomberman.localisation.Phrase;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * The base level command which is handled specially by the {@link CmdHandler}. All other commands parents must
 * eventually link back to this command.
 */
public class BaseCmd implements CmdGroup {

    private static final Phrase
        NAME        = new Phrase( "command-basecmd-name", "bm" ),
        DESCRIPTION = new Phrase( "command-basecmd-description", "All bomberman related things. Type {format|command|{command|name} help} for help" ),
        USAGE       = new Phrase( "command-basecmd-usage", "{format|command|{command|path} <more-commands>" );

    @Override
    public Phrase name() {
        return NAME;
    }

    @Override
    public Phrase description() {
        return DESCRIPTION;
    }

    @Override
    public Phrase usage() {
        return USAGE;
    }

    @Override
    public Class<CmdGroup> parent() {
        // only this class is allowed to return null
        return null;
    }

    @Override
    public boolean execute( CommandSender sender, List<String> args, List<String> options ) {

        return false;
    }

    @Override
    public boolean hasPermission( CommandSender sender ) {
        return false;
    }
}
