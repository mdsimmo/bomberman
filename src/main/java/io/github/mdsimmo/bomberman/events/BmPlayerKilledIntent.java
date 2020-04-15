package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a bm player is killed
 */
public class BmPlayerKilledIntent extends BmIntent {

    public static void kill(Game game, Player player, Player cause) {
        var e = new BmPlayerKilledIntent(game, player, cause);
        Bukkit.getPluginManager().callEvent(e);
        e.verifyHandled();
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Game game;
    private final Player player, attacker;

    private BmPlayerKilledIntent(Game game, Player player, Player attacker) {
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

}