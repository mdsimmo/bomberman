package com.github.mdsimmo.bomberman.localisation;

import org.bukkit.command.CommandSender;

/**
 * Defines some helpful utility methods for sending a Phrase to a player. It is
 * advised that all phrases be sent through this method as it wil ensure that
 * all messages are formatted the same way.
 */
public class Chat {

    /**
     * Translates and sends the phrase to the player. If the translation of the
     * messages is empty, then no message will be sent
     * @param sender the player/console to send to
     * @param phrase the phrase to send
     */
    public static void send( CommandSender sender, Phrase phrase ) {
        Language language = Language.of( sender );
        String message = language.translate( phrase );
        sender.sendMessage( message );
    }

}
