package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

/**
 * Called when a game is completely deleted from the server
 */
public class BmGameDeletedIntent extends BmIntent {

    public static void delete(Game game) {
        var e = new BmGameDeletedIntent(game);
        Bukkit.getPluginManager().callEvent(e);
        e.verifyHandled();
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;

    private BmGameDeletedIntent(Game game) {
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
