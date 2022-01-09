package io.github.mdsimmo.bomberman.commands.game

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
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
import io.github.mdsimmo.bomberman.game.GameSettings
import io.github.mdsimmo.bomberman.game.GameSettingsBuilder
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import io.github.mdsimmo.bomberman.utils.WorldEditUtils.selectionBounds
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.logging.Level
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.pathString

class GameCreate(parent: Cmd) : Cmd(parent) {

    companion object {
        private val plugin = Bomberman.instance
        private val we = WorldEdit.getInstance()

        private const val F_PLUGIN = "p"
        private const val F_SCHEMA = "f"
        private const val F_WAND = "w"
        private const val F_SKIP_AIR = "a"
        private const val F_VOID_TO_AIR = "v"

        private fun getBombermanOptions(): Map<String, ()->InputStream> {
            return mapOf (
                Pair("purple") { GameCreate::class.java.classLoader.getResourceAsStream("purple.schem")!! },
                Pair("bm") { GameCreate::class.java.classLoader.getResourceAsStream("purple.schem")!! }, // for backwards compatibility
            )
        }

        private fun allFiles(root: Path): List<Path> {
            val files = if (root.isDirectory()) { root.listDirectoryEntries() } else { setOf() }
            val fileList: MutableList<Path> = ArrayList()
            for (f in files) {
                if (f.isDirectory()) {
                    val subFiles = allFiles(f)
                    // empty subdir
                    fileList.addAll(subFiles)
                } else {
                    fileList.add(f)
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
        return setOf(F_PLUGIN, F_SCHEMA, F_WAND, F_SKIP_AIR, F_VOID_TO_AIR)
    }

    override fun flagOptions(sender: CommandSender, flag: String, args: List<String>, flags: Map<String, String>): Set<String> {
        return when (flag) {
            F_PLUGIN -> setOf("purple")
            F_SCHEMA -> {
                we.getWorkingDirectoryPath(we.configuration.saveDir).let { fileDir ->
                    allFiles(fileDir)
                        .map { file -> file.pathString }
                        .toSet()
                }
            }
            else -> emptySet()
        }
    }

    override fun flagDescription(flag: String): Message {
        return when(flag) {
            F_PLUGIN -> context(Text.CREATE_FLAG_PLUGIN).format()
            F_SCHEMA -> context(Text.CREATE_FLAG_SCHEMA).format()
            F_WAND -> context(Text.CREATE_FLAG_WAND).format()
            F_SKIP_AIR -> context(Text.CREATE_FLAG_SKIP_AIR).format()
            F_VOID_TO_AIR -> context(Text.CREATE_FLAG_VOID_TO_AIR).format()
            else -> Message.empty
        }
    }

    override fun flagExtension(flag: String): Message {
        return when(flag) {
            F_PLUGIN -> context(Text.CREATE_FLAG_PLUGIN_EXT).format()
            F_SCHEMA -> context(Text.CREATE_FLAG_SCHEMA_EXT).format()
            else -> Message.empty
        }
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
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

        // Read the build flags
        val settings = GameSettingsBuilder().also {
            it.skipAir = flags.containsKey(F_SKIP_AIR)
            it.deleteVoid = flags.containsKey(F_VOID_TO_AIR)
        }.build()

        // Build from wand selection
        if (flags.containsKey(F_WAND)) {
            makeFromSelection(gameName, sender, settings)
            return true
        }

        // Build from plugin
        flags[F_PLUGIN]?.let { schema ->
            if (schema != "we") { // Use WorldEdit (for backwards compatibility)
                val input = getBombermanOptions()[schema.lowercase()]
                if (input == null) {
                    context(
                        Text.CREATE_SCHEMA_NOT_FOUND
                            .with("schema", Message.of(schema))
                    )
                        .sendTo(sender)
                } else {
                    plugin.logger.info("Reading default schematic: $schema")
                    makeFromSchema(gameName, input().use { inputStream ->
                        BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(inputStream).use { it.read() }
                    }, sender, settings)
                    return true
                }
            }
        }

        // Load from WorldEdit
        flags[F_SCHEMA]?.let { schema ->
            val matches = we.getWorkingDirectoryPath(we.configuration.saveDir).listDirectoryEntries("**/$schema")
            matches // The minimum length path will be the closest match
                .minByOrNull { it.name.length }?.also { file: Path ->
                    // load schema
                    try {
                        plugin.logger.info("Reading schematic data: $file")
                        val format = ClipboardFormats.findByFile(file.toFile())
                            ?: throw IllegalArgumentException("Unknown file format: '${file}'")
                        val clipboard = format.getReader(Files.newInputStream(file)).use { it.read() }
                        plugin.logger.info("Schematic read")
                        makeFromSchema(gameName, clipboard, sender, settings)
                    } catch (e: Exception) {
                        context(Text.CREATE_ERROR)
                            .with("error", e.message ?: "")
                            .sendTo(sender)
                        e.printStackTrace()
                    }
                } ?: run {
                context(Text.CREATE_SCHEMA_NOT_FOUND
                    .with("schema", Message.of(schema)))
                    .sendTo(sender)
            }
        }

        // -p, -f or -w must be included
        return false
    }

    private fun makeFromSelection(gameName: String, sender: CommandSender, settings: GameSettings) {
        if (sender !is Player) {
            context(Text.MUST_BE_PLAYER).sendTo(sender)
            return
        }
        val owner: SessionOwner = BukkitAdapter.adapt(sender)
        val session = WorldEdit.getInstance().sessionManager.getIfPresent(owner)
        if (session == null || session.selectionWorld == null || !session.isSelectionDefined(session.selectionWorld)) {
            context(Text.CREATE_NEED_SELECTION).sendTo(sender)
        } else {
            try {
                val region = session.getSelection(session.selectionWorld)
                val box = selectionBounds(region)
                try {
                    val game = Game.buildGameFromRegion(gameName, box, settings)
                    context(Text.CREATE_SUCCESS)
                            .with("game", game)
                            .sendTo(sender)
                } catch (e: Exception) {
                    context(Text.CREATE_ERROR)
                            .with("error", e.message ?: "")
                            .sendTo(sender)
                    e.printStackTrace()
                }
            } catch (e: IncompleteRegionException) {
                context(Text.CREATE_NEED_SELECTION).sendTo(sender)

                // This should never happen
                plugin.logger.log(Level.WARNING, "Could not get selection", e)
            }
        }
    }

    private fun makeFromSchema(gameName: String, schema: Clipboard, player: Player, settings: GameSettings) {
        // Create the game
        val game = Game.buildGameFromSchema(gameName, player.location, schema, settings)
        context(Text.CREATE_SUCCESS)
                .with("game", game)
                .sendTo(player)
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