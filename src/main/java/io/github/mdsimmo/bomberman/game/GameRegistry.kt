package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.BmGameDeletedIntent
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.util.*
import javax.annotation.CheckReturnValue

object GameRegistry {

    private val plugin = Bomberman.instance
    private val gameRegistry = HashMap<String, Game>()

    @CheckReturnValue
    fun allGames(): Set<Game> {
        return HashSet(gameRegistry.values)
    }

    /**
     * finds the game associated with the given name
     */
    @CheckReturnValue
    fun byName(name: String): Game? {
        return gameRegistry[name.toLowerCase()]
    }

    @CheckReturnValue
    private fun fileOf(g: Game): File {
        return File(plugin.settings.gameSaves(), g.name + ".yml")
    }

    @JvmStatic
    fun loadGames() {
        val data = plugin.settings.gameSaves()
        val files = data
                .listFiles { _, name ->
                    name.endsWith(".yml")
                }
                ?: return
        for (f in files) {
            loadGame(f)
        }
    }

    private fun loadGame(file: File): Game? {
        val configuration = YamlConfiguration.loadConfiguration(file)
        val game = configuration["data"] as Game?
        if (game != null) {
            Bomberman.instance.logger.info("Loaded " + game.name)
        } else {
            Bomberman.instance.logger.info("Cannot load " + file.name)
        }
        return game
    }

    fun saveGame(game: Game) {
        try {
            val configuration = YamlConfiguration()
            configuration["data"] = game
            configuration.save(fileOf(game))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Registers the game
     *
     * @param game
     * The game to register
     */
    fun register(game: Game) {
        gameRegistry[game.name.toLowerCase()] = game
    }

    fun remove(game: Game, deleteSave: Boolean) {
        gameRegistry.remove(game.name)
        if (deleteSave) fileOf(game).delete()
    }

    fun reload(game: Game): Game {
        BmGameDeletedIntent.delete(game, false)
        return loadGame(fileOf(game))!!
    }
}