package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * Called when a bomb turns from a tnt block into a fire '+'
 */
public class BmExplosionEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled = false;
    private final Game game;
    private final Player cause;
    private final Set<Block> ignited;

    public BmExplosionEvent(Game game, Player cause, Set<Block> ignited) {
        this.game = game;
        this.cause = cause;
        this.ignited = new HashSet<>(ignited);
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

    public Player getCause() {
        return cause;
    }

    /**
     * Gets a mutable list of the blocks that will be ignited. Blocks can be added/removed from this list.
     * Any blocks in this list will be changed into fire if the event is not cancelled.
     * @return the list of blocks to be ignited.
     */
    public Set<Block> getIgnited() {
        return ignited;
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
