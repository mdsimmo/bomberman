package com.github.mdsimmo.bomberman.prizes;

import org.bukkit.entity.Player;

/**
 * A payment that is an amount of experience points
 */
public final class XpPayment implements Payment {

    /**
     * Makes a payment from an amount of xp points
     * @param amount the amount of xp point the payment represents
     * @return an XpPayment of {@code amount} cp points
     */
    public static XpPayment of( int amount ) {
        return new XpPayment( amount );
    }

    private final int amount;

    private XpPayment( int amount ) {
        if ( amount < 0 )
            throw new IllegalArgumentException( "amount must be greater or eaqual to 0" );
        this.amount = amount;
    }

    /**
     * Gets the amount of xp points that this XpPayment represents
     * @return the amount of xp points
     */
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean ownedBy( Player player ) {
        return player.getTotalExperience() >= amount;
    }

    @Override
    public void giveTo( Player player ) {
        player.setTotalExperience( player.getTotalExperience() + amount );
    }

    @Override
    public boolean takeFrom( Player player ) {
        if ( !ownedBy( player ))
            return false;
        player.setTotalExperience( player.getTotalExperience() - amount );
        return true;
    }
}
