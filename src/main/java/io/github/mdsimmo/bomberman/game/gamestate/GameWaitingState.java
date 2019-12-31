package io.github.mdsimmo.bomberman.game.gamestate;

import io.github.mdsimmo.bomberman.game.GamePlayer;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameWaitingState implements GameState {


    /**
     * Adds a new player to the game
     *
     * @param p the player to add
     */
    public boolean addPlayer(Player p) {
        // Only can join if waiting
        if (!(state instanceof GameWaitingState)) {
            Chat.sendMessage(getMessage(Text.JOIN_GAME_STARTED, p));
            return false;
        }

        // Check if there is spare room
        Location gameSpawn = findSpareSpawn();
        if ( gameSpawn == null ) {
            Chat.sendMessage(getMessage(Text.GAME_FULL, p));
            return false;
        }

        // Check if the player can afford it
        if (!settings.fare.takeFrom(p)) {
            Chat.sendMessage(getMessage(Text.TOO_POOR, p));
            return false;
        }

        GamePlayer player = new GamePlayer(p, this, gameSpawn);
        players.add(player);

        // Trigger auto-start if needed
        if (settings.autostart) {
            if (findSpareSpawn() == null) {
                startGame();
            } else if (players.size() >= settings.minPlayers) {
                startGame(settings.autostartDelay, false);
            }
        }

        return true;
    }

}
