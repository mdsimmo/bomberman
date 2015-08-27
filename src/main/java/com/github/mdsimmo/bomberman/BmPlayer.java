package com.github.mdsimmo.bomberman;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * A BmPlayer (Bomberman Player) is a wrapper around a {@link org.bukkit.entity.Player}. It's purpose is to add a few
 * extra functions that Bomberman needs
 */
public class BmPlayer {

    private static HashMap<UUID, BmPlayer> playerRegistry = new HashMap<UUID, BmPlayer>();

    public static BmPlayer getBmPlayer( Player player ) {
        if ( player == null )
            throw new NullPointerException( "Player cannot be null" );
        BmPlayer bmPlayer = playerRegistry.get( player.getUniqueId() );
        if ( bmPlayer != null )
            return bmPlayer;
        bmPlayer = new BmPlayer( player );
        return bmPlayer;
    }

    private final Player player;

    private BmPlayer( Player player) {
        this.player = player;
    }

    /**
     * Gets the Player that this BmPlayer is targeting
     * @return the Player
     */
    Player getPlayer() {
        return player;
    }



}
