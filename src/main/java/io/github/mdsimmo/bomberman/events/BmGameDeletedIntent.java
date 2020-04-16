package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called when a game is completely deleted from the server
 */
public class BmGameDeletedIntent extends BmIntent {

    public static void delete(Game game, boolean deleteSave) {
        var e = new BmGameDeletedIntent(game);
        e.setDeletingSave(deleteSave);
        Bukkit.getPluginManager().callEvent(e);
        e.verifyHandled();
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;
    private boolean deleteSave;

    private BmGameDeletedIntent(Game game) {
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

    public boolean isDeletingSave() {
        return deleteSave;
    }

    public void setDeletingSave(boolean delete) {
        deleteSave = delete;
    }
}
