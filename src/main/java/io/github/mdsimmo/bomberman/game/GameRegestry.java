package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameRegestry {

    private static Plugin plugin = Bomberman.instance;
    private static final HashMap<String, Game> gameRegistry = new HashMap<>();

    public static List<String> allGames() {
        return new ArrayList<>(gameRegistry.keySet());
    }

    /**
     * finds the game associated with the given name
     */
    public static Game findGame( String name ) {
        return gameRegistry.get( name.toLowerCase() );
    }

    public static void loadGames() {
        File data = plugin.getDataFolder();
        if ( !data.exists() )
            data.mkdirs();
        File[] files = data.listFiles((dir, name) -> ( name.endsWith( ".game" ) ));
        for ( File f : files ) {
            GameSaver.loadGame( f );
        }
    }

    /**
     * Registers the game
     *
     * @param game
     *            The game to register
     */
    public static void register( Game game ) {
        gameRegistry.put( game.getName().toLowerCase(), game );
    }

    public static void remove(Game game) {
        gameRegistry.remove(game.getName());
    }
}
