package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called when a game is completely deleted from the server
 */
public class BmGameRebuildIntent extends BmIntent {

    public static void rebuild(Game game) {
        var e = new BmGameRebuildIntent(game);
        Bukkit.getPluginManager().callEvent(e);
        e.verifyHandled();
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;

    private BmGameRebuildIntent(Game game) {
        this.game = game;
    }

    @Override
    @RefectAccess
    @Nonnull
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
