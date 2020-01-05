package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStoppedEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Game game;

    public GameStoppedEvent(Game game) {
        this.game = game;
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
}
