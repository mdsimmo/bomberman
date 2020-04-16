package io.github.mdsimmo.bomberman.events;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GamePlayer;
import io.github.mdsimmo.bomberman.utils.RefectAccess;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called when a player places down a block of TNT (or whatever the game configured as the tnt block). Cancelling the
 * event will remove the tnt from the ground as if the player never clicked
 */
public class BmPlayerPlacedBombEvent extends BmEvent implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled = false;
    private final Game game;
    private final Player player;
    private final Block block;
    private int strength;

    public BmPlayerPlacedBombEvent(Game game, Player player, Block block) {
        this.game = game;
        this.player = player;
        this.block = block;
        this.strength = GamePlayer.bombStrength(game, player);
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

    public Block getBlock() {
        return block;
    }

    public int getStrength() {
        return strength;
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
