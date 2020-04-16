package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class BmPlayerLeaveGameIntent extends BmIntent {

    public static BmPlayerLeaveGameIntent leave(Player player) {
        var leave = new BmPlayerLeaveGameIntent(player);
        Bukkit.getPluginManager().callEvent(leave);
        // Leave event may not be handled if player was not joined
        return leave;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Player player;

    private BmPlayerLeaveGameIntent(Player player) {
        this.player = player;
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

    @Nonnull
    public Player getPlayer() {
        return player;
    }

}
