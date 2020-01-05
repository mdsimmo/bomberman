package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerHurtEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Game game;
    private final Player player, attacker;
    private boolean isCancelled = false;

    public PlayerHurtEvent(Game game, Player player, Player attacker) {
        this.game = game;
        this.player = player;
        this.attacker = attacker;
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

    public Player getAttacker() {
        return attacker;
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
