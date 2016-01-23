package io.github.mdsimmo.bomberman.prizes;

import io.github.mdsimmo.bomberman.messaging.Formattable;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

/**
 * A payment is something that can be used as an entry fare or for a games prize.
 * It is generally just a wrapper around an ItemStack, but this is not required.
 * It could be anything such as a bunch of xp points or Vault's money.
 */
public interface Payment extends ConfigurationSerializable, Formattable {

    /**
     * Tests if the player can currently pay the payment
     * @param player the player to test
     * @return true if the player has the prize
     */
    boolean ownedBy( Player player );

    /**
     * Gives the payment to a player. If the player cannot hold the entire
     * payment (e.g. their inventory is full), then the remaining payment should
     * just be thrown at the players feet.
     * @param player the player to give the payment to
     */
    void giveTo( Player player );

    /**
     * Takes the payment from a player. If the player is not wealthy enough to
     * pay the payment, then nothing will be done and false will be returned.
     * @param player the player to deduct the payment from
     * @return true if the payment was deducted. false otherwise.
     */
    boolean takeFrom( Player player );

}