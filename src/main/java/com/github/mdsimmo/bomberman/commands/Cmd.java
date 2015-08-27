package com.github.mdsimmo.bomberman.commands;

import com.github.mdsimmo.bomberman.localisation.Phrase;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * A Cmd is a simple representation of something that a user can execute from the chat. Commands can be nested within
 * other commands by giving a valid parent with {@link #parent()}. All commands must have exactly one parent which
 * link back to {@link BaseCmd}.
 * For bomberman to know about a command, it must be registered with {@link CmdHandler#register(Cmd)}
 */
public interface Cmd {

    /**
     * What word to type into the chat box to call this command
     * @return the commands name
     */
    Phrase name();

    /**
     * Gets the description that should be shown to the player when asking for help about this command
     * @return this commands description
     */
    Phrase description();

    /**
     * A very quick phrase showing how to use the command. E.g: {@code /bm game create &lt;game&gt;}
     * @return the commands usage
     */
    Phrase usage();

    /**
     * This commands parent. All commands should return a parent command.
     * @return the parent of this command
     */
    Class<CmdGroup> parent();

    /**
     * Executes this command. This method will never be called if {@link #hasPermission(CommandSender)} does not return
     * true for the given sender.
     * @param sender the player/console that ran the command
     * @param args the args given to the command. This will only contain the commands that are relevant to this command
     *             with all leading command path arguments striped away
     * @param options the options given to the command
     * @return true if the command was run correctly. False is wrong and to display usage.
     */
    boolean execute( CommandSender sender, List<String> args, List<String> options );

    /**
     * Tests if a user can use this command. This will always be called before a call to {@link #execute(CommandSender, List, List)}
     * @param sender the player/console who wrote the command
     * @return true if the user can use this command
     */
    boolean hasPermission( CommandSender sender );

}
