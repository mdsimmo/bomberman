package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a bm player moves. Cannot modify the event.
 */
public class BmPlayerMovedEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Game game;
    private final Player player;
    private final Location from, to;

    public BmPlayerMovedEvent(Game game, Player player, Location from, Location to) {
        this.game = game;
        this.player = player;
        this.from = from;
        this.to = to;
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

    public Player getPlayer() {
        return player;
    }

    public Location getFrom() {
        return from.clone();
    }

    public Location getTo() {
        return to.clone();
    }

}
