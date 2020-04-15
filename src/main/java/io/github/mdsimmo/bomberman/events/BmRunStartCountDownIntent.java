package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.messaging.Message;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.Optional;

/**
 * Called whenever a run is attempted to be started
 */
public class BmRunStartCountDownIntent extends BmIntentCancellable {

    public static BmRunStartCountDownIntent startGame(Game game, int delay) {
        var e = new BmRunStartCountDownIntent(game, delay);
        Bukkit.getPluginManager().callEvent(e);
        e.verifyHandled();
        return e;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private Game game;
    private int delay;
    private Message cancelReason = null;

    private BmRunStartCountDownIntent(Game game, int delay) {
        this.game = game;
        this.delay = delay;
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

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Optional<Message> getCancelledReason() {
        return Optional.ofNullable(cancelReason);
    }

    public void cancelBecause(Message cancelReason) {
        this.cancelReason = cancelReason;
        setCancelled(true);
    }
}
