package com.github.mdsimmo.bomberman;

import com.github.mdsimmo.bomberman.localisation.Language;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A BmPlayer (Bomberman Player) is a wrapper around a {@link org.bukkit.entity.Player}.
 * It's purpose is to add a few extra functions that Bomberman needs
 */
public class BmPlayer implements ConfigurationSerializable {

    private static HashMap<UUID, BmPlayer> playerRegistry = new HashMap<UUID, BmPlayer>();

    /**
     * Gets the Bomberman player that represents the given player. There is a
     * one-to-one mapping between Players an BmPlayers; thus, this method will
     * always return the same BmPlayer for the same Player and will never
     * return null.
     * @param player the player to represent
     * @return the BmPlayer
     */
    public static BmPlayer of( OfflinePlayer player ) {
        if ( player == null )
            throw new NullPointerException( "Player cannot be null" );
        UUID playerUUID = player.getUniqueId();
        BmPlayer bmPlayer = playerRegistry.get( playerUUID );
        if ( bmPlayer != null )
            return bmPlayer;
        bmPlayer = new BmPlayer( player );
        playerRegistry.put( playerUUID, bmPlayer );
        return bmPlayer;
    }

    private final OfflinePlayer player;
    private Language language = Language.getDefaultLanguage();

    private BmPlayer( OfflinePlayer player) {
        this.player = player;
    }

    /**
     * Gets the Player that this BmPlayer is targeting
     * @return the Player
     */
    public OfflinePlayer getPlayer() {
        return player;
    }

    public Language getLanguage() {
        // todo make BmPlayer Language configurable
        return Language.getDefaultLanguage();
    }

    /**
     * For serialization purposes only. DO NOT USE.
     * @return this objects data
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "player", player );
        map.put( "language", language );
        return map;
    }

    /**
     * For serialization purposes only. DO NOT USE.
     * @param map the objects data
     * @return a new object
     */
    public static BmPlayer deserialize( Map<String, Object> map ) {
        UUID playerUUID = (UUID)map.get( "player" );
        OfflinePlayer player = Bukkit.getOfflinePlayer( playerUUID );
        BmPlayer bmPlayer = BmPlayer.of( player );
        bmPlayer.language = (Language)map.get( "language" );
        return bmPlayer;
    }
}