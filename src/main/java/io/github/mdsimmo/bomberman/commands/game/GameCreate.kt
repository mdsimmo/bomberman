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

        private const val F_SCHEMA = "s"
        private const val F_CONFIG = "c"
        private const val F_WAND = "w"

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
        return setOf(F_SCHEMA, F_CONFIG, F_WAND)
    }

    override fun flagOptions(sender: CommandSender, flag: String, args: List<String>, flags: Map<String, String>): Set<String> {
        val validExtensions = when(flag) {
            F_SCHEMA -> BuiltInClipboardFormat.values().flatMap { it.fileExtensions }.toSet().plus("game.zip")
            F_CONFIG -> setOf(".yml", ".game.zip")
            else -> setOf()
        }
        return when (flag) {
            F_SCHEMA, F_CONFIG -> {
                arrayOf(
                    Pair("bm", plugin.templates()),
                    Pair("we", we.getWorkingDirectoryPath(we.configuration.saveDir))
                )
                .flatMap { (type, dir) -> allFiles(dir, dir).map { file -> "$type:${file.pathString}" } }
                .filter { fileName -> validExtensions.any { ext -> fileName.endsWith(ext) } }
                .plus("bm:purple")
                .plus(BmGameListIntent.listGames().map { "g:${it.name}" })
                .toSet()
            }
            else -> emptySet()
        }
    }

    override fun flagDescription(flag: String): Message {
        return when(flag) {
            F_SCHEMA -> context(Text.CREATE_FLAG_SCHEMA).format()
            F_CONFIG -> context(Text.CREATE_FLAG_CONFIG).format()
            F_WAND -> context(Text.CREATE_FLAG_WAND).format()
            else -> Message.empty
        }
    }

    override fun flagExtension(flag: String): Message {
        return when(flag) {
            F_SCHEMA -> context(Text.CREATE_FLAG_SCHEMA_EXT).format()
            F_CONFIG -> context(Text.CREATE_FLAG_CONFIG_EXT).format()
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

            // cannot use -s and -w together
            if (flags[F_SCHEMA] != null && flags[F_WAND] != null) {
                return false
            }

            // Get config to use
            val settings = (
                    flags[F_CONFIG]
                    ?: flags[F_SCHEMA]?.let { if (it.endsWith(".game.zip")) { it } else { null }  }
            )?.let { arg ->
                val parts = arg.split(':', ignoreCase = true, limit = 2)
                if (parts.size < 2)
                    return false

                val type = parts[0].lowercase()
                val file = parts[1]
                when (parts[0].lowercase()) {
                    "bm", "we" -> {
                        val base = if (type == "bm")
                            plugin.templates()
                        else
                            we.getWorkingDirectoryPath(we.configuration.saveDir)

                        val path = base.resolve(file)
                        if (!path.exists()) {
                            throw FileNotFoundException(path.pathString)
                        } else if (file.endsWith(".game.zip", ignoreCase = true)) {
                            GameSave.loadSave(path).getSettings()
                        } else if (file.endsWith(".yml", ignoreCase = true)) {
                            path.reader().use {
                                YamlConfiguration.loadConfiguration(it)
                            }
                                .getSerializable("settings", GameSettings::class.java)
                                ?: throw IllegalArgumentException("The YML file does not contains game settings")
                        } else {
                            throw IllegalArgumentException("Unknown file type: ${path.fileName}")
                        }
                    }
                    "g" -> {
                        val game = BmGameLookupIntent.find(parts[0]) ?: return false
                        game.settings
                    }
                    else -> return false
                }
            } ?: GameSettingsBuilder().build()


            // Build from wand selection
            if (flags.containsKey(F_WAND)) {
                makeFromSelection(gameName, sender, settings)
                return true
            }

            // Load clipboard
            val clipboard = (flags[F_SCHEMA]
                    ?: flags[F_CONFIG]?.let { if (it.endsWith(".game.zip")) { it } else { null }  }
                    ?: "bm:purple.game.zip"
            ).let { arg ->
                val parts = arg.split(':', ignoreCase = true, limit = 2)
                if (parts.size < 2)
                    return false

                val type = parts[0].lowercase()
                val file = parts[1]
                when (type) {
                    "bm", "we" -> {
                        val base = if (type == "bm")
                            plugin.templates()
                        else
                            we.getWorkingDirectoryPath(we.configuration.saveDir)

                        val path = base.resolve(file)
                        if (!path.exists()) {
                            throw FileNotFoundException(path.pathString)
                        } else if (file.endsWith(".game.zip")) {
                            GameSave.loadSave(path).getSchematic()
                        } else {
                            val hjg = path.toFile()
                            println("$hjg " + hjg.absoluteFile )
                            val format = ClipboardFormats.findByFile(path.toFile())
                                ?: throw IllegalArgumentException("Unknown file format: '${file}'")
                            format.getReader(Files.newInputStream(path)).use { it.read() }
                        }
                    }
                    "g" -> {
                        val game = BmGameLookupIntent.find(file) ?: return false
                        game.clipboard
                    }
                    else -> return false
                }
            }

            // Create the game
            val game = Game.buildGameFromSchema(gameName, sender.location, clipboard, settings)
            context(Text.CREATE_SUCCESS)
                .with("game", game)
                .sendTo(sender)
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