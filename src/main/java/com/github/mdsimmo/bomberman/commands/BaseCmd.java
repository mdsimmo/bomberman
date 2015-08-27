package com.github.mdsimmo.bomberman.commands;

import com.github.mdsimmo.bomberman.localisation.Phrase;

/**
 * The base level command which is handled specially by the {@link CmdHandler}. All other commands parents must
 * eventually link back to this command.
 */
public class BaseCmd extends CmdGroup {

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
        // only the base command is allowed to return null
        return null;
    }
}
