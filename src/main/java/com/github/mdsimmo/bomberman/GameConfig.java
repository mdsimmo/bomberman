package com.github.mdsimmo.bomberman;

import com.github.mdsimmo.bomberman.prizes.EmptyPayment;
import com.github.mdsimmo.bomberman.prizes.ItemPayment;
import com.github.mdsimmo.bomberman.prizes.Payment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A class designed for holding the huge amount of different options a game
 * can be configured with
 */
public class GameConfig {

    private boolean autoStartEnabled = false;
    private int autoStartDelay = 30;
    private Payment
            fare  = EmptyPayment.getEmptyPayment(),
            prize = ItemPayment.of( new ItemStack( Material.DIAMOND, 3 ) );
    private int minPlayers = 2, maxPlayers = 10;

    /**
     * Tests if auto starting is enabled
     * @return true if auto start is enabled
     */
    public boolean isAutoStartEnabled() {
        return autoStartEnabled;
    }

    /**
     * Sets if auto starting should be enabled. If auto start is enabled, then
     * the game will automatically when the minimum players have joined.
     * @param enabled true to enable auto start
     * @see #getMinPlayers()
     */
    public void setAutoStartEnabled( boolean enabled ) {
        autoStartEnabled = enabled;
    }

    /**
     * Gets the time (in seconds) that the game will delay for before the game
     * will auto start
     * @return the count down on auto start or -1 if auto start is not enabled
     */
    public int getAutoStartDelay() {
        return autoStartDelay;
    }

    /**
     * Sets the delay on auto start.
     * @param delay the amount of seconds to wait before string the game
     */
    public void setAutoStartDelay( int delay ) {
        autoStartDelay = delay;
    }

    /**
     * Sets the entry fare to the game
     * @param payment the payment to use
     * @throws NullPointerException if payment is null (use
     * {@link com.github.mdsimmo.bomberman.prizes.EmptyPayment} for no fare)
     */
    public void setFare( Payment payment ) {
        if ( payment == null )
            throw new NullPointerException( "cannot have a null fare" );
        this.fare = payment;
    }

    /**
     * Gets the fare that players must pay to enter this game
     * @return the games entry fare
     */
    public Payment getFare() {
        return fare;
    }

    /**
     * Sets what prize players will get when they win
     * @param payment the amount players must pay
     * @throws NullPointerException if payment is null (use
     * {@link com.github.mdsimmo.bomberman.prizes.EmptyPayment} for no prize)
     */
    public void setPrize( Payment payment ) {
        if ( payment == null )
            throw new NullPointerException( "cannot have a null prize" );
        this.prize = payment;
    }

    /**
     * Gets the amount that will be payed to the winner of the game
     * @return the games prize money
     */
    public Payment getPrize() {
        return prize;
    }

    /**
     * Sets the minimum amount of players that can be in the game before the
     * game can be started. If auto start is enabled, then the game will start
     * when the minimum amount of players have joined.
     * @return the minimum amount of players
     * @see #isAutoStartEnabled()
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    /**
     * Sets the minimum amount of players that must join before a game can start
     * @param minPlayers the minimum amount of players
     * @throws IllegalArgumentException if min players is smaller than 1
     * @see #getMinPlayers()
     */
    public void setMinPlayers( int minPlayers ) {
        if ( minPlayers <= 0 )
            throw new IllegalArgumentException( "min players cannot be less that 1" );
        this.maxPlayers = minPlayers;
    }

    /**
     * Sets the maximum amount of players that can be in the game. Once the
     * max players have joined, no other player will be allowed to join.
     * Note: if max players is set to be more than the amount of spawn points
     * in an arena, then max players is effectively ignored.
     * @return the maximum amount of players
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Sets the minimum amount of players that must join before a game can start
     * @param maxPlayers the maximum amount of players
     * @see #getMaxPlayers()
     * @throws IllegalArgumentException if maxPlayers is less than 1
     */
    public void setMaxPlayers( int maxPlayers ) {
        if ( maxPlayers <= 0 )
            throw new IllegalArgumentException( "max players cannot be less than 1" );
        this.maxPlayers = maxPlayers;
    }

}
