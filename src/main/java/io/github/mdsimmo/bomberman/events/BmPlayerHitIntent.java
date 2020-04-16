package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Event that occurs whenever a player is standing on a bomb. Will be called every tick that the player remains on the
 * bomb
 */
public class BmPlayerHitIntent extends BmIntentCancellable {

    public static void hit(Player player, Player cause) {
        BmIntent onBombEvent = new BmPlayerHitIntent(player, cause);
        Bukkit.getPluginManager().callEvent(onBombEvent);
        onBombEvent.verifyHandled();
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled = false;
    private Player player;
    private Player cause;

    private BmPlayerHitIntent(Player player, Player bombCause) {
        this.player = player;
        this.cause = bombCause;
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
