package com.github.mdsimmo.bomberman;

import com.github.mdsimmo.bomberman.prizes.Payment;

public interface Game {

    enum State {
        BUILDING,
        WAITING,
        STARTING,
        PLAYING,
        ENDING,
        DESTROYED
    };


    /**
     * Adds a player to the game. This can only be done if the game is in the
     * waiting state.
     * @param rep the player that will be added
     * @return true if the player was added. false otherwise.
     */
    boolean addPlayer( BmPlayer rep );

    /**
     * Starts the game's countdown
     */
    void start();

    /**
     * Stops the games countdown
     */
    void stop();

    void destroy();

    void setFare( Payment payment );

    Payment getFare();

    void setPrize( Payment payment );

    /**
     * Gets the amount that will
     * @return
     */
    Payment getPrize();



}
