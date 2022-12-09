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
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import io.github.mdsimmo.bomberman.utils.WorldEditUtils.selectionBounds
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.logging.Level
import kotlin.io.path.*

class GameCreate(parent: Cmd) : Cmd(parent) {

    companion object {
        private val plugin = Bomberman.instance
        private val we = WorldEdit.getInstance()

        private const val F_SCHEMA = "schem"
        private const val F_WAND = "wand"

        private const val S_GAME = "g"
        private const val S_TEMPLATE = "t"
        private const val S_WORLDEDIT = "we"

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

    override fun name(): Message {
        return context(Text.CREATE_NAME).format()
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> BmGameListIntent.listGames().map(Game::name).toList()
            else -> emptyList()
        }
    }

    override fun flags(sender: CommandSender, args: List<String>, flags: Map<String, String>): Set<String> {
        return setOf(F_SCHEMA, F_WAND)
    }

    override fun flagOptions(sender: CommandSender, flag: String, args: List<String>, flags: Map<String, String>): Set<String> {
        return when (flag) {
            F_SCHEMA -> {
                val templatesDir = plugin.templates()
                val weDir = we.getWorkingDirectoryPath(we.configuration.saveDir)
                val schemaExtensions = BuiltInClipboardFormat.values().flatMap { it.fileExtensions }
                BmGameListIntent.listGames().map { "${S_GAME}:${it.name}" }
                    .plus(
                        allFiles(templatesDir, templatesDir).map { file -> "${S_TEMPLATE}:${file.pathString}" }
                            .filter { fileName -> fileName.endsWith(".game.zip") }
                    )
                    .plus(
                        allFiles(weDir, weDir).map { file -> "${S_WORLDEDIT}:${file.pathString}" }
                            .filter { fileName -> schemaExtensions.any { ext -> fileName.endsWith(ext) } }
                    )
                    .toSet()
            }
            else -> emptySet()
        }
    }

    override fun flagDescription(flag: String): Message {
        return when(flag) {
            F_SCHEMA -> context(Text.CREATE_FLAG_SCHEMA).format()
            F_WAND -> context(Text.CREATE_FLAG_WAND).format()
            else -> Message.empty
        }
    }

    override fun flagExtension(flag: String): Message {
        return when(flag) {
            F_SCHEMA -> context(Text.CREATE_FLAG_SCHEMA_EXT).format()
            else -> Message.empty
        }
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
        try {
            if (args.size != 1)
                return false
            if (sender !is Player) {
                context(Text.MUST_BE_PLAYER).sendTo(sender)
                return true
            }
            val gameName = args[0]
            BmGameLookupIntent.find(gameName)?.let { game ->
                context(Text.CREATE_GAME_EXISTS)
                        .with("game", game)
                        .sendTo(sender)
                return true
            }
            // Also make sure the save file is safe to use (in case file names have been altered or special characters used)
            if (plugin.gameSaves().resolve(GameSave.sanitize("${gameName}.game.zip")).exists()) {
                context(Text.CREATE_GAME_FILE_CONFLICT)
                    .with("game", gameName)
                    .with("file", GameSave.sanitize("${gameName}.game.zip"))
                    .sendTo(sender)
                return true
            }

            // cannot use -s and -w together
            if (flags[F_SCHEMA] != null && flags[F_WAND] != null) {
                return false
            }

            // Build from wand selection
            if (flags.containsKey(F_WAND)) {
                makeFromSelection(gameName, sender, GameSettingsBuilder().build())
                return true
            }

            // Get config to use
            flags[F_SCHEMA]?.let { arg ->
                val parts = arg.split(':', ignoreCase = true, limit = 2)
                if (parts.size < 2)
                    return false

                val type = parts[0].lowercase()
                val file = parts[1]
                val (schema, settings) = when (type.lowercase()) {
                    S_WORLDEDIT -> {
                        // Load schematic from path
                        val path = we.getWorkingDirectoryPath(we.configuration.saveDir).resolve(file)
                        if (!path.exists()) {
                            context(Text.CREATE_GAME_FILE_NOT_FOUND)
                                .with("file", path.toString())
                                .with("filename", path.name)
                                .sendTo(sender)
                            return true
                        }
                        val format = ClipboardFormats.findByFile(path.toFile())
                            ?: throw IllegalArgumentException("Unknown file format: '${file}'")
                        val clipboard = format.getReader(Files.newInputStream(path)).use { it.read() }

                        Pair(clipboard, GameSettingsBuilder().build())
                    }
                    S_GAME -> {
                        val existingGame = BmGameLookupIntent.find(file) ?: return false
                        Pair(existingGame.clipboard, existingGame.settings)
                    }
                    S_TEMPLATE -> {
                        val path = plugin.templates().resolve(file)
                        if (!path.exists()) {
                            context(Text.CREATE_GAME_FILE_NOT_FOUND)
                                .with("file", path.toString())
                                .with("filename", path.name)
                                .sendTo(sender)
                            return true
                        }
                        val save = GameSave.loadSave(path)
                        Pair(save.getSchematic(), save.getSettings())
                    }
                    else -> {
                        return false
                    }
                }
                val game = Game.buildGameFromSchema(gameName, sender.location, schema, settings)
                context(Text.CREATE_SUCCESS)
                    .with("game", game)
                    .sendTo(sender)
                return true
            }

            // Must specify either -s or -w
            return false

        } catch (e: Exception) {
            context(Text.CREATE_ERROR)
                .with("error", e.message ?: "")
                .sendTo(sender)
            plugin.logger.log(Level.WARNING, "Error creating game", e)
        }
        return true
    }

    private fun makeFromSelection(gameName: String, sender: Player, settings: GameSettings) {
        val owner: SessionOwner = BukkitAdapter.adapt(sender)
        val session = WorldEdit.getInstance().sessionManager.getIfPresent(owner)
        if (session == null || session.selectionWorld == null || !session.isSelectionDefined(session.selectionWorld)) {
            context(Text.CREATE_NEED_SELECTION).sendTo(sender)
        } else {
            val region = session.getSelection(session.selectionWorld)
            val box = selectionBounds(region)
                val game = Game.buildGameFromRegion(gameName, box, settings)
                context(Text.CREATE_SUCCESS)
                        .with("game", game)
                        .sendTo(sender)

        }
    }

    override fun permission(): Permission {
        return Permissions.CREATE
    }

    override fun example(): Message {
        return context(Text.CREATE_EXAMPLE).format()
    }

    override fun extra(): Message {
        return context(Text.CREATE_EXTRA).format()
    }

    override fun description(): Message {
        return context(Text.CREATE_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.CREATE_USAGE).format()
    }
}