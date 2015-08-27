package com.github.mdsimmo.bomberman.prizes;

import org.bukkit.entity.Player;

/**
 * An empty payment is a payment worth nothing. All players can pay the payment
 * and giving it to a player does nothing.
 */
public final class EmptyPayment implements Payment {

    /**
     * Gets an empty payment.
     * @return an empty payment
     */
    public static EmptyPayment getEmptyPayment() {
        return instance;
    }

    static final EmptyPayment instance = new EmptyPayment();

    private EmptyPayment() {
    }

    @Override
    public boolean ownedBy(Player player) {
        return true;
    }

    @Override
    public void giveTo(Player player) {

    }

    @Override
    public boolean takeFrom(Player player) {
        return true;
    }

}