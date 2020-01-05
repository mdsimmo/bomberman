package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveGameEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;
    private Player player;

    public PlayerLeaveGameEvent(Game game, Player player) {
        this.game = game;
        this.player = player;
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
