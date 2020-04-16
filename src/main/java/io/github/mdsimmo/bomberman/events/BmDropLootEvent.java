package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Explosion;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Called when a bomb turns from a tnt block into a fire '+'
 */
public class BmDropLootEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled = false;
    private final Game game;
    private final Player cause;
    private final Set<Explosion.BlockPlan> ignited;
    private final Map<Location, Set<ItemStack>> drops;

    public BmDropLootEvent(Game game, Player cause, Set<Explosion.BlockPlan> ignited, Map<Location, Set<ItemStack>> drops) {
        this.game = game;
        this.cause = cause;
        this.ignited = ignited;
        this.drops = new HashMap<>(drops);
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
     * Gets a immutable list of the blocks that were ignited and thus in the drop check
     * @return the list of blocks ignited.
     */
    public Set<Explosion.BlockPlan> getIgnited() {
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

    /**
     * Gets mutable list of blocks dropping. Adding/removing will alter the block drops
     * @return blocks to drop
     */
    public Map<Location, Set<ItemStack>> getDrops() {
        return drops;
    }
}
