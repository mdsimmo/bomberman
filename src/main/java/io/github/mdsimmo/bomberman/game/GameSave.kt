package io.github.mdsimmo.bomberman.game

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
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.reader

/**
 * Handles reading/writing a game's data to disk
 */
class GameSave(private val zipPath: Path) {

    companion object {
        private val plugin = Bomberman.instance

        /**
         * Writes data to disk (overriding anything existing) and returns a valid save file
         */
        fun createNewSave(name: String, settings: GameSettings, arena: Arena): GameSave {
            val zipPath = plugin.settings.gameSaves().resolve("$name.game.zip")
            FileSystems.newFileSystem(zipPath, mapOf(Pair("create", !zipPath.exists()))).use { fs ->

                // Write the schematic
                val arenaPath = fs.getPath("arena.schem")
                Files.newOutputStream(arenaPath).use { os ->
                    BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(os).use {
                            writer -> writer.write(arena.clipboard)
                    }
                }

                // Write the arena data
                val arenaDataPath = fs.getPath("arena.yml")
                val arenaYML = YamlConfiguration()
                arenaYML.set("config", arena.settings)
                Files.writeString(arenaDataPath, arenaYML.saveToString())

                // Write the settings
                val settingsPath = fs.getPath("settings.yml")
                val settingsYML = YamlConfiguration()
                settingsYML.set("settings", settings)
                Files.writeString(settingsPath, settingsYML.saveToString())

                // Write additional config data
                val configPath = fs.getPath("config.yml")
                val configYML = YamlConfiguration()
                configYML.set("name", name)
                Files.writeString(configPath, configYML.saveToString())
            }

            val save = GameSave(zipPath)

            save.nameCache = name
            save.arenaCache = WeakReference(arena)
            save.settingsCache = settings

            return save
        }

        @JvmStatic
        fun loadGames() {
            val data = plugin.settings.gameSaves()
            val files = data.listDirectoryEntries("*.game.zip")
            for (f in files) {
                try {
                    Game(GameSave(f))
                } catch (e: Exception) {
                    plugin.logger.log(Level.WARNING, "Exception occurred while loading: $f", e)
                }
            }
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
                    val settings = config.getSerializable("settings", GameSettings::class.java) ?: GameSettings()
                    val buildFlags = config.getSerializable("build-flags", BuildFlags::class.java) ?: BuildFlags()

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
                        createNewSave(name, settings, Arena(origin, buildFlags, clipboard))

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

    private var nameCache: String? = null
    private var arenaCache: WeakReference<Arena>? = null
    private var settingsCache: GameSettings? = null

    /**
     * Gets the schematic from the save (maybe cached)
     * @throws IOException if the file cannot be read for any reason
     */
    @Throws(NoSuchFileException::class)
    fun getArena() : Arena {
        // Return the cache, or fetch if not existing
        return arenaCache?.get() ?: run {
            plugin.logger.info("Reading schematic data: " + zipPath.fileName)

            // Open the zip
            FileSystems.newFileSystem(zipPath).use { fs ->
                // Load the schematic
                val arenaPath = fs.getPath("arena.schem")
                val schematic = Files.newInputStream(arenaPath).use { reader ->
                    BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(reader).use { it.read() }
                }

                // Read the settings
                val arenaDataPath = fs.getPath("arena.yml")
                val arenaSettings = Files.newBufferedReader(arenaDataPath).use { reader ->
                    val arenaYML = YamlConfiguration.loadConfiguration(reader)
                    arenaYML.getSerializable("config", Arena.ArenaSettings::class.java)!!
                }

                plugin.logger.info("Data read")

                val arena = Arena(arenaSettings, schematic)
                arenaCache = WeakReference(arena)
                arena
            }
        }
    }

    /**
     * Reads the game settings from disk
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
                        ?: plugin.settings.defaultGameSettings()
                    settingsCache = settings

                    plugin.logger.info("Data read")

                    settings
                }
            }
        }
    }

    /**
    * Reads the game config from disk
    * @throws IOException if the config does not exist or cannot be read for any reason
    */
    @Throws(NoSuchFileException::class)
    fun getName() : String {
        return nameCache ?: run {
            plugin.logger.info("Reading game config: " + zipPath.fileName)

            // Open the zip
            FileSystems.newFileSystem(zipPath).use { fs ->
                // Read the config
                val configPath = fs.getPath("config.yml")
                Files.newBufferedReader(configPath).use { reader ->
                    val configYML = YamlConfiguration.loadConfiguration(reader)
                    val name = configYML.getString("name") ?: throw IOException("Cannot read 'name' from config.yml")
                    nameCache = name

                    plugin.logger.info("Data read")

                    name
                }
            }
        }
    }

    /**
     * Removes the cache so that the next reads will match the file
     */
    fun reload() {
        nameCache = null
        arenaCache = null
        settingsCache = null
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