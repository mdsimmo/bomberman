package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Called whenever a game run is stopped. May be due to game finishing, game forcefully stoped, server shutdown, etc.
 */
public class BmRunStoppedIntent extends BmIntentCancellable {

    public static BmRunStoppedIntent stopGame(Game game) {
        var e = new BmRunStoppedIntent(game);
        Bukkit.getPluginManager().callEvent(e);
        e.verifyHandled();
        return e;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;
    private Message cancelReason;

    private BmRunStoppedIntent(Game game) {
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

    public void cancelFor(Message reason) {
        setCancelled(true);
        this.cancelReason = reason;
    }

    public Optional<Message> cancelledReason() {
        return Optional.ofNullable(cancelReason);
    }
}
