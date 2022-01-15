package io.github.mdsimmo.bomberman.game

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import io.github.mdsimmo.bomberman.Bomberman
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.logging.Level
import kotlin.io.path.*

/**
 * Handles reading/writing a game's data to disk
 */
class GameSave private constructor(val name: String, val origin: Location, private val zipPath: Path) {

    /*
     * Games are saved into a zip file with the following structure
     *
     * arena.schem
     *      The World Edit schematic
     * settings.yml
     *      All settings for configuring the game. If the schematic is copied, these settings are generally wanted too
     * config.yml
     *      Settings specific to this game (name and location)
     *
     * The zip file name is "thegamesname.game.zip". thegamesname is the same as the game's name with the following exceptions:
     *   - it is all lowercase
     *   - Al characters except a-z, and 0-9 are replaced with '_'
     */

    companion object {
        private val plugin = Bomberman.instance

        /**
         * Writes data to disk (overriding anything existing) and returns a valid save file
         */
        fun createNewSave(name: String, origin: Location, settings: GameSettings, schematic: Clipboard): GameSave {
            val zipPath = plugin.settings.gameSaves().resolve("${name.lowercase().replace(Regex("[^a-z0-9]"), "_")}.game.zip")
            FileSystems.newFileSystem(zipPath, mapOf(Pair("create", !zipPath.exists()))).use { fs ->

                // Write the schematic
                val arenaPath = fs.getPath("arena.schem")
                Files.newOutputStream(arenaPath).use { os ->
                    BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(os).use {
                            writer -> writer.write(schematic)
                    }
                }

                // Write the settings
                val settingsPath = fs.getPath("settings.yml")
                val settingsYML = YamlConfiguration()
                settingsYML.set("settings", settings)
                Files.writeString(settingsPath, settingsYML.saveToString())

                // Write config data
                val configPath = fs.getPath("config.yml")
                val configYML = YamlConfiguration()
                configYML.set("name", name)
                configYML.set("origin", origin)
                Files.writeString(configPath, configYML.saveToString())
            }

            val save = GameSave(name, origin, zipPath)

            // Set the cache so it doesn't have to read it out again
            save.schematicCache = WeakReference(schematic)
            save.settingsCache = settings

            return save
        }

        @JvmStatic
        fun loadGames() {
            val data = plugin.settings.gameSaves()
            val files = data.listDirectoryEntries("*.game.zip")
            for (f in files) {
                try {
                    loadGame(f)
                } catch (e: Exception) {
                    plugin.logger.log(Level.WARNING, "Exception occurred while loading: $f", e)
                }
            }
        }

        /**
         * Loads a game's save from the given path. The GameSave is just the data, so the Game will not be created
         * @throws IOException if the given ZipFile cannot be read
         */
        @Throws(IOException::class)
        fun loadSave(zipFile: Path): GameSave {
            plugin.logger.info("Reading ${zipFile.pathString}")
            // Open the zip
            return FileSystems.newFileSystem(zipFile).use { fs ->
                // Read the config
                val configPath = fs.getPath("config.yml")
                Files.newBufferedReader(configPath).use { reader ->
                    val configYml = YamlConfiguration.loadConfiguration(reader)
                    val name = configYml.getString("name") ?: throw IOException("Cannot read 'name' from 'config.yml' in '${zipFile.pathString}'")
                    val origin = configYml.getSerializable("origin", Location::class.java) ?: throw IOException("Cannot read 'origin' from 'config.yml' in '${zipFile.pathString}'")

                    plugin.logger.info("  Data read")

                    GameSave(name, origin, zipFile)
                }
            }
        }
        /**
         * Loads a game from the given path
         * @throws IOException if the given ZipFile cannot be read
         */
        @Throws(IOException::class)
        fun loadGame(zipFile: Path): Game {
            return Game(loadSave(zipFile))
        }

        /**
         * Updates saves from before version 0.8.0
         */
        @JvmStatic
        fun updatePre080Saves() {
            // Find old game files
            val files = plugin.settings.gameSaves().listDirectoryEntries("*.yml")
            files.forEach { file ->
                try {
                    // Convert the old file into the new format
                    plugin.logger.info("Updating old save: $file")

                    // Read all data out of old config file
                    val config = file.reader().use { YamlConfiguration.loadConfiguration(it) }
                    val name = config.getString("name")
                    val schema = config.getString("schema")
                    val origin = config.getSerializable("origin", Location::class.java)
                    val settings = (config.getSerializable("settings", GameSettings::class.java) ?: GameSettingsBuilder().build()).copy(
                        // copy out build flags which were in a separate section
                        skipAir = config.getBoolean("build-flags.skip-air", false),
                        deleteVoid = config.getBoolean("build-flags.delete-void", false),
                    )

                    if (name == null || schema == null || origin == null) {
                        // This data cannot be fudged
                        plugin.logger.info("  Skipping update as file missing data")
                    } else {
                        // Load the Schematic
                        val schemaFile = File(schema)
                        plugin.logger.info("  Loading schematic: " + schemaFile.path)
                        val format = ClipboardFormats.findByFile(schemaFile)
                            ?: throw IllegalArgumentException("Unknown file format: '${schemaFile.path}'")
                        val clipboard = format.getReader(FileInputStream(schemaFile)).use { it.read() }

                        // Create the new save file
                        // Note - this will not load the game, it will just create the save file.
                        createNewSave(name, origin, settings, clipboard)

                        // delete the old save file
                        file.deleteIfExists()

                        plugin.logger.info("  Save Updated")
                    }
                } catch (e: Exception) {
                    plugin.logger.log(Level.WARNING, "Exception occurred while updating $file", e)
                }
            }
        }
    }

    private var schematicCache: WeakReference<Clipboard>? = null
    private var settingsCache: GameSettings? = null

    /**
     * Gets the schematic from the save (maybe cached)
     * @throws IOException if the file cannot be read for any reason
     */
    @Throws(NoSuchFileException::class)
    fun getSchematic() : Clipboard {
        // Return the cache, or fetch if not existing
        return schematicCache?.get() ?: run {
            plugin.logger.info("Reading schematic data: " + zipPath.fileName)

            // Open the zip
            FileSystems.newFileSystem(zipPath).use { fs ->
                // Load the schematic
                val arenaPath = fs.getPath("arena.schem")
                val schematic = Files.newInputStream(arenaPath).use { reader ->
                    BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(reader).use { it.read() }
                }

                plugin.logger.info("Data read")

                schematicCache = WeakReference(schematic)
                schematic
            }
        }
    }

    /**
     * Reads the game settings from disk (maybe cached)
     * If the settings do not exist, then default settings will be used
    * @throws IOException if the file is formatted badly and cannot be read
    */
    @Throws(NoSuchFileException::class)
    fun getSettings() : GameSettings {
        return settingsCache ?: run {
            plugin.logger.info("Reading game settings: " + zipPath.fileName)

            // Open the zip
            FileSystems.newFileSystem(zipPath).use { fs ->
                // Read the settings
                val settingsPath = fs.getPath("settings.yml")
                Files.newBufferedReader(settingsPath).use { reader ->
                    val settingsYML = YamlConfiguration.loadConfiguration(reader)
                    val settings = settingsYML.getSerializable("settings", GameSettings::class.java)
                        ?: GameSettingsBuilder().build()
                    settingsCache = settings

                    plugin.logger.info("Data read")

                    settings
                }
            }
        }
    }

    /**
     * Updates the configuration data in the file
     * @throws IOException if the file cannot be written to for any reason
     */
    @Throws(IOException::class)
    fun updateSettings(settings: GameSettings) {
        settingsCache = null // reset cache so next read will be as on disk

        // Create a new YML file to write to the zip
        val yml = YamlConfiguration()
        yml.set("settings", settings)
        val ymlString = yml.saveToString()

        // Copy the yml string into the zip file
        FileSystems.newFileSystem(zipPath).use { fs ->
            val zipConfigPath: Path = fs.getPath("settings.yml")
            Files.copy(ByteArrayInputStream(ymlString.toByteArray()), zipConfigPath, StandardCopyOption.REPLACE_EXISTING)
        }

        // Update cache if all went well
        settingsCache = settings
    }
}