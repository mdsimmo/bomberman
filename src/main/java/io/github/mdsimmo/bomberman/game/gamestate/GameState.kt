package io.github.mdsimmo.bomberman.game.gamestate

interface GameState {

    /**
     * Stops the current game state entirely so that the server may be shut down/restarted
     */
    fun terminate()

}
