package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a game run is stopped. May be due to game finishing, game forcefully stoped, server shutdown, etc.
 */
public class BmRunStoppedIntent extends BmIntent {

    public static void stopGame(Game game) {
        var e = new BmRunStoppedIntent(game);
        Bukkit.getPluginManager().callEvent(e);
        e.verifyHandled();
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;

    private BmRunStoppedIntent(Game game) {
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
