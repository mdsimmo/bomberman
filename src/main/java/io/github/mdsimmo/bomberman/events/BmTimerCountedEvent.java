package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called whenever a game run is stopped. May be due to game finishing, game forcefully stoped, server shutdown, etc.
 */
public class BmTimerCountedEvent extends BmEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;
    private int count;

    public BmTimerCountedEvent(Game game, int count) {
        this.game = game;
        this.count = count;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
