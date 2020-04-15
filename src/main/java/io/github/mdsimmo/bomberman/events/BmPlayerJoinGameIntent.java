package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.messaging.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Optional;

/**
 * Called whenever a player attempts to join a game. If there are not enough spawns in the game or the player cannot
 * afford entry, or ..., the event will be cancelled
 */
public class BmPlayerJoinGameIntent extends BmIntentCancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;
    private Player player;
    private Message cancelReason = null;

    public BmPlayerJoinGameIntent(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void cancelFor(Message reason) {
        setCancelled(true);
        this.cancelReason = reason;
    }

    public Optional<Message> cancelledReason() {
        return Optional.ofNullable(cancelReason);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

}
