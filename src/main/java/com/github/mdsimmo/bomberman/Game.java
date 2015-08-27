package com.github.mdsimmo.bomberman;

import com.github.mdsimmo.bomberman.GameConfig;

public interface Game {

    /**
     * Represents all possible states that a game can be in
     */
    enum State {
        /**
         * Game is in the initial building state. Nothing should be done to it
         */
        BUILDING,
        /**
         * The game is idle. Waiting for players can join or someone to start
         * the game
         */
        WAITING,
        /**
         * Game is starting.
         */
        STARTING,
        /**
         * Game is in the middle of a round.
         */
        PLAYING,
        /**
         * The game has just finished. This state should only last for a single tick.
         */
        ENDING,
        /**
         * The game has been destroyed and should be garbage collected
         */
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
     * Gets the state that this game currently is in
     * @return the game's state
     */
    State getState();

    /**
     * Very similar to {@link #start()} but will count down from 3 seconds. If the
     * game is in the {@link com.github.mdsimmo.bomberman.Game.State#STARTING}
     * state, then the count down will be set to a maximum of 3 seconds.
     */
    void quickStart();

    /**
     * If the game is in the {@link com.github.mdsimmo.bomberman.Game.State#WAITING}
     * state, then the game will start counting down with with the configured
     * auto start delay. If the game is in any other state, this method will
     * do nothing.
     */
    void start();

    /**
     * Stops the current game from playing (if it was playing). No prizes will
     * be awarded to players.
     */
    void stop();

    /**
     * Fully destroys the game. All players will be kicked out and the arena
     * will be reverted.
     */
    void destroy();

    /**
     * Gets the config used for this game
     * @return the game's config
     */
    GameConfig getConfig();

}