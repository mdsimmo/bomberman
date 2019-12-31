package io.github.mdsimmo.bomberman.game.gamestate;

import io.github.mdsimmo.bomberman.arena.ArenaGenerator;
import io.github.mdsimmo.bomberman.game.Bomb;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GamePlayer;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GamePlayingState implements GameState {

    private final Game game;
    private Game.GameStarter countdownTimer = null;
    List<Bomb.DeathBlock> deathBlocks = new ArrayList<>();
    Map<Block, Bomb> explosions = new HashMap<>();

    public GamePlayingState(GameStartingState startingState) {
        this.game = startingState.game;
    }

    /**
     * updates the status of the game.
     *
     * @return true if the game has finished;
     */
    private boolean checkFinish() {
        if ( players.size() <= 1 && state == State.PLAYING ) {

            state = State.ENDING;

            // kill the survivors
            for ( GamePlayer rep : new ArrayList<>( players ) ) {
                ( (GamePlayingState)rep.getState() ).kill();
            }

            // to avoid the above kill loop executing this method twice
            if ( state != State.ENDING )
                return true;

            // get the total winnings
            final Player topPlayer = winners.get( 0 ).getPlayer();
            prize.giveTo( topPlayer );

            // display the scores
            sendMessages( Text.GAME_OVER_PLAYERS, Text.GAME_OVER_OBSERVERS,
                    Text.GAME_COUNT_ALL, null );
            winnersDisplay();

            // reset the game
            ArenaGenerator.switchBoard( this.arena, this.arena, box, null );
            stop();

            return true;
        }
        return state == State.ENDING || state == State.WAITING;
    }

    /**
     * call when a player dies
     */
    public void alertRemoval(GamePlayer rep) {
        players.remove(rep);
        HashMap<String, Object> map = new HashMap<>();
        map.put( "player", rep );
        if (state == State.PLAYING || state == State.ENDING) {
            if ( !checkFinish() )
                messagePlayers(Text.PLAYER_KILLED, Text.PLAYER_KILLED_OBSERVERS, Text.PLAYER_KILLED_ALL,
                        map );
        } else {
            messagePlayers( Text.PLAYER_LEFT_PLAYERS, Text.PLAYER_LEFT_OBSERVERS,
                    Text.PLAYER_LEFT_ALL, map );
        }
        if (players.size() < minPlayers && getCountdownTimer() != null) {
            map.put( "time", getCountdownTimer().count );
            getCountdownTimer().destroy();
            state = State.WAITING;
            messagePlayers( Text.COUNT_STOPPED_PLAYERS,
                    Text.COUNT_STOPPED_OBSERVERS, Text.COUNT_STOPPED_ALL, map );
        }
    }

}
