package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.events.BmGameDeletedIntent;
import io.github.mdsimmo.bomberman.events.BmGameRebuildIntent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.annotation.CheckReturnValue;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameRegistry {

    private static Bomberman plugin = Bomberman.instance;
    private static final HashMap<String, Game> gameRegistry = new HashMap<>();

    @CheckReturnValue
    public static Set<Game> allGames() {
        return new HashSet<>(gameRegistry.values());
    }

    /**
     * finds the game associated with the given name
     */
    @CheckReturnValue
    public static Optional<Game> byName(String name ) {
        return Optional.ofNullable(gameRegistry.get( name.toLowerCase()));
    }

    @CheckReturnValue
    private static File fileOf(Game g) {
        return new File(plugin.getSettings().gameSaves(), g.getName() + ".yml");
    }

    public static void loadGames() {
        File data = plugin.getSettings().gameSaves();
        File[] files = data.listFiles((dir, name) -> ( name.endsWith( ".yml" ) ));
        if (files == null)
            return;
        for ( File f : files ) {
            loadGame(f);
        }
    }

    private static void loadGame(File file) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        Game game = (Game) configuration.get("data");
    }

    public static void saveGame(Game game) {
        try {
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.set("data", game);
            configuration.save(fileOf(game));
        } catch (IOException e) {
            e.printStackTrace();
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

    public static void remove(Game game, boolean deleteSave) {
        gameRegistry.remove(game.getName());
        if (deleteSave)
            fileOf(game).delete();
    }

    public static void reload(Game game) {
        BmGameDeletedIntent.delete(game, false);
        loadGame(fileOf(game));
    }
}
