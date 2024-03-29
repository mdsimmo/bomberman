package io.github.mdsimmo.bomberman.commands.game

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.session.SessionOwner
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmGameListIntent
import io.github.mdsimmo.bomberman.events.BmGameLookupIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.game.GameSave
import io.github.mdsimmo.bomberman.game.GameSettings
import io.github.mdsimmo.bomberman.game.GameSettingsBuilder
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import io.github.mdsimmo.bomberman.utils.WorldEditUtils.selectionBounds
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.logging.Level
import kotlin.io.path.*

class GameCreate(parent: Cmd) : Cmd(parent) {

    companion object {
        private val plugin = Bomberman.instance
        private val we = WorldEdit.getInstance()

        private const val F_SCHEMA = "s"
        private const val F_TEMPLATE = "t"
        private const val F_GAME = "g"
        private const val F_WAND = "w"
        private const val F_PLUGIN = "p" // for backwards compatibility

        /**
         * Gets all files (excluding directories) in the give directory recursively
         * @param root the directory to search. If a file is given, an empty set is returned
         * @param relative the path to specify paths relative to. If null, paths are specified in full
         */
        private fun allFiles(root: Path, relative: Path? = null): List<Path> {
            val files = if (root.isDirectory(LinkOption.NOFOLLOW_LINKS)) { root.listDirectoryEntries() } else { setOf() }
            val fileList: MutableList<Path> = ArrayList()
            for (f in files) {
                if (f.isDirectory(LinkOption.NOFOLLOW_LINKS)) {
                    val subFiles = allFiles(f, relative)
                    fileList.addAll(subFiles)
                } else {
                    fileList.add(if (relative != null) relative.relativize(f) else f)
                }
            }
            return fileList
        }
    }

    override fun name(): Formattable {
        return Text.CREATE_NAME
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> BmGameListIntent.listGames().map(Game::name).toList()
            else -> emptyList()
        }
    }

    override fun flags(sender: CommandSender): Set<String> {
        return setOf(F_SCHEMA, F_WAND, F_GAME, F_TEMPLATE)
    }

    override fun flagOptions(flag: String): Set<String> {
        return when (flag) {
            F_SCHEMA -> {
                val weDir = we.getWorkingDirectoryPath(we.configuration.saveDir)
                val schemaExtensions = BuiltInClipboardFormat.values().flatMap { it.fileExtensions }

                allFiles(weDir, weDir).map { it.pathString }
                        .filter { fileName -> schemaExtensions.any { ext -> fileName.endsWith(ext) } }
                        .toSet()
            }
            F_TEMPLATE -> {
                val templatesDir = plugin.templates()
                allFiles(templatesDir, templatesDir).map { it.pathString }
                    .filter { fileName -> fileName.endsWith(".game.zip") }
                    .map { it.replace(Regex("(.*)\\.game.zip"), "$1") }
                    .toSet()
            }
            F_GAME -> {
                BmGameListIntent.listGames().map { it.name }
                    .toSet()
            }
            else -> emptySet()
        }
    }

    override fun flagDescription(flag: String): Formattable {
        return when(flag) {
            F_SCHEMA -> Text.CREATE_FLAG_SCHEMA
            F_TEMPLATE -> Text.CREATE_FLAG_TEMPLATE
            F_GAME -> Text.CREATE_FLAG_GAME
            F_WAND -> Text.CREATE_FLAG_WAND
            else -> Message.empty
        }
    }

    override fun flagExtension(flag: String): Formattable {
        return when(flag) {
            F_SCHEMA -> Text.CREATE_FLAG_SCHEMA_EXT
            F_TEMPLATE -> Text.CREATE_FLAG_TEMPLATE_EXT
            F_GAME -> Text.CREATE_FLAG_GAME_EXT
            else -> Message.empty
        }
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
        try {
            if (args.size != 1)
                return false
            if (sender !is Player) {
                Text.MUST_BE_PLAYER.format(cmdContext()).sendTo(sender)
                return true
            }
            val gameName = args[0]
            BmGameLookupIntent.find(gameName)?.let { game ->
                Text.CREATE_GAME_EXISTS.format(cmdContext()
                        .plus("game", game))
                    .sendTo(sender)
                return true
            }
            // Also make sure the save file is safe to use (in case file names have been altered or special characters used)
            if (plugin.gameSaves().resolve(GameSave.sanitize("${gameName}.game.zip")).exists()) {
                Text.CREATE_GAME_FILE_CONFLICT.format(cmdContext()
                        .plus("game", gameName)
                        .plus("file", GameSave.sanitize("${gameName}.game.zip")))
                    .sendTo(sender)
                return true
            }

            // Check that exactly one flag is set
            if (1 !=
                (if (flags.containsKey(F_WAND))     1 else 0) +
                (if (flags.containsKey(F_GAME))     1 else 0) +
                (if (flags.containsKey(F_TEMPLATE)) 1 else 0) +
                (if (flags.containsKey(F_SCHEMA))   1 else 0) +
                (if (flags.containsKey(F_PLUGIN))   1 else 0))
                return false

            // Build from wand selection
            if (flags.containsKey(F_WAND)) {
                makeFromSelection(gameName, sender, GameSettingsBuilder().build())
                return true
            }

            // Get config/schema to use from the selected flag
            val (schema, settings) =
                flags[F_SCHEMA]?.let { file ->
                    if (file == "")
                        return false
                    // Load schematic from path
                    val path = we.getWorkingDirectoryPath(we.configuration.saveDir).resolve(file)
                    if (!path.exists()) {
                        Text.CREATE_GAME_FILE_NOT_FOUND.format(cmdContext()
                                .plus("file", path.toString())
                                .plus("filename", path.name))
                            .sendTo(sender)
                        return true
                    }
                    val format = ClipboardFormats.findByFile(path.toFile())
                        ?: throw IllegalArgumentException("Unknown file format: '${file}'")
                    val clipboard = format.getReader(Files.newInputStream(path)).use { it.read() }

                    Pair(clipboard, GameSettingsBuilder().build())
                } ?: flags[F_GAME]?.let { name ->
                    // Cope existing game
                    val existingGame = BmGameLookupIntent.find(name) ?: return false
                    Pair(existingGame.clipboard, existingGame.settings)
                } ?: flags[F_TEMPLATE]?.let { file ->
                    if (file == "")
                        return false
                    // Load template file
                    val fullFileName = if (file.endsWith(".game.zip", ignoreCase = true))
                        file
                    else
                        file.plus(".game.zip")
                    val path = plugin.templates().resolve(fullFileName)
                    if (!path.exists()) {
                        Text.CREATE_GAME_FILE_NOT_FOUND.format(cmdContext()
                                .plus("file", path.toString())
                                .plus("filename", path.name))
                            .sendTo(sender)
                        return true
                    }
                    val save = GameSave.loadSave(path)
                    Pair(save.getSchematic(), save.getSettings())
                } ?: flags[F_PLUGIN]?.let { value ->
                    // `\bm create <name> -p=bm` used to be advertised a lot, so keep it for backwards compatibility
                    if (value.lowercase() == "bm") {
                        val path = plugin.templates().resolve("purple.game.zip")
                        val save = GameSave.loadSave(path)
                        Pair(save.getSchematic(), save.getSettings())
                    } else {
                        return false
                    }
                } ?: throw RuntimeException("This should never happen")

                val game = Game.buildGameFromSchema(gameName, sender.location, schema, settings)
                Text.CREATE_SUCCESS.format(cmdContext()
                        .plus("game", game))
                    .sendTo(sender)
                return true

        } catch (e: Exception) {
            Text.CREATE_ERROR.format(cmdContext()
                    .plus("error", e.message ?: ""))
                .sendTo(sender)
            plugin.logger.log(Level.WARNING, "Error creating game", e)
        }
        return true
    }

    private fun makeFromSelection(gameName: String, sender: Player, settings: GameSettings) {
        val owner: SessionOwner = BukkitAdapter.adapt(sender)
        val session = WorldEdit.getInstance().sessionManager.getIfPresent(owner)
        if (session == null || session.selectionWorld == null || !session.isSelectionDefined(session.selectionWorld)) {
            Text.CREATE_NEED_SELECTION.format(cmdContext()).sendTo(sender)
        } else {
            val region = session.getSelection(session.selectionWorld)
            val box = selectionBounds(region)
                val game = Game.buildGameFromRegion(gameName, box, settings)
                Text.CREATE_SUCCESS.format(cmdContext()
                        .plus("game", game))
                    .sendTo(sender)

        }
    }

    override fun permission(): Permission {
        return Permissions.CREATE
    }

    override fun example(): Formattable {
        return Text.CREATE_EXAMPLE
    }

    override fun extra(): Formattable {
        return Text.CREATE_EXTRA
    }

    override fun description(): Formattable {
        return Text.CREATE_DESCRIPTION
    }

    override fun usage(): Formattable {
        return Text.CREATE_USAGE
    }
}