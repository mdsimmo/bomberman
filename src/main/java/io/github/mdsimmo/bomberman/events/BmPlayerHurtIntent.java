package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called whenever a bm player takes damage.
 */
public class BmPlayerHurtIntent extends BmIntentCancellable {

    public static void run(Game game, Player player, Player cause) {
        var hurtEvent = new BmPlayerHurtIntent(game, player, cause);
        Bukkit.getPluginManager().callEvent(hurtEvent);
        hurtEvent.verifyHandled();
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final Game game;
    private final Player player, attacker;

    public BmPlayerHurtIntent(Game game, Player player, Player attacker) {
        this.game = game;
        this.player = player;
        this.attacker = attacker;
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

    public Player getPlayer() {
        return player;
    }

    public Player getAttacker() {
        return attacker;
    }

}
