package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a game is being removed - may be because game deleted or because
 * server is shutting down. Is possible game starts back when server starts back.
 * All event listeners for the game should destroy themselves on this event.
 */
public class BmGameTerminatedIntent extends BmIntent {

    public static void terminateGame(Game game) {
        var e = new BmGameTerminatedIntent(game);
        Bukkit.getPluginManager().callEvent(e);
        e.verifyHandled();
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;

    private BmGameTerminatedIntent(Game game) {
        this.game = game;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Game getGame() {
        return game;
    }
}
