package io.github.mdsimmo.bomberman.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerOnBombEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled = false;
    private Player player;
    private Player cause;

    public PlayerOnBombEvent(Player player, Player bombCause) {
        this.player = player;
        this.cause = bombCause;
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

    public Player getCause() {
        return cause;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
