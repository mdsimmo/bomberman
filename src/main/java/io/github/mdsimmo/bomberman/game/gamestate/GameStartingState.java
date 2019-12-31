package io.github.mdsimmo.bomberman.game.gamestate;

public class GameStartingState implements GameState {

    public GameStartingState(GameWaitingState waiting) {
        this.game = waiting.game;
        this.players = waiting.players;
    }

}
