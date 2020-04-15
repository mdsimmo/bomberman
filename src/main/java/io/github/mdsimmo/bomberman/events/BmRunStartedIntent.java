package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a run is attempted to be started
 */
public class BmRunStartedIntent extends BmIntent {

    public static void startRun(Game game) {
        var e = new BmRunStartedIntent(game);
        Bukkit.getPluginManager().callEvent(e);
        e.verifyHandled();
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;

    private BmRunStartedIntent(Game game) {
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
