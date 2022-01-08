package io.github.mdsimmo.bomberman.commands.game

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.session.SessionOwner
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmGameListIntent
import io.github.mdsimmo.bomberman.events.BmGameLookupIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.game.Game.Companion.buildGameFromRegion
import io.github.mdsimmo.bomberman.game.Game.Companion.buildGameFromSchema
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import io.github.mdsimmo.bomberman.utils.WorldEditUtils.selectionBounds
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.util.*

class GameCreate(parent: Cmd) : Cmd(parent) {

    companion object {
        private val bm = Bomberman.instance
        private val we = WorldEdit.getInstance()

        private const val F_PLUGIN = "p"
        private const val F_SCHEMA = "f"
        private const val F_SKIP_AIR = "a"
        private const val F_VOID_TO_AIR = "v"

        private fun root(plugin: String): File? {
            return when (plugin.lowercase()) {
                "bomberman", "bm" -> bm.settings.builtinSaves()
                "worldedit", "we" -> we.getWorkingDirectoryPath(we.configuration.saveDir).toFile()
                else -> null
            }
        }

        private fun defaultSchema(plugin: String): String? {
            return when(plugin.lowercase()) {
                "bm", "bomberman" -> "purple"
                else -> null
            }
        }

        private fun allFiles(root: File): List<File> {
            val files = root.listFiles() ?: return listOf()
            val fileList: MutableList<File> = ArrayList()
            for (f in files) {
                if (f.isDirectory) {
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
        return setOf(F_PLUGIN, F_SCHEMA, F_SKIP_AIR, F_VOID_TO_AIR)
    }

    override fun flagOptions(sender: CommandSender, flag: String, args: List<String>, flags: Map<String, String>): Set<String> {
        return when (flag) {
            F_PLUGIN -> setOf("we", "bm")
            F_SCHEMA -> {
                val plugin = flags[F_PLUGIN] ?: "we"
                root(plugin)?.let { fileDir ->
                    allFiles(fileDir)
                            .map { file -> file.path }
                            .toSet()
                } ?: emptySet()
            }
            else -> emptySet()
        }
    }

    override fun flagDescription(flag: String): Message {
        return when(flag) {
            F_PLUGIN -> context(Text.CREATE_FLAG_PLUGIN).format()
            F_SCHEMA -> context(Text.CREATE_FLAG_SCHEMA).format()
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
        val buildFlags = Game.BuildFlags()
        buildFlags.skipAir = flags.containsKey(F_SKIP_AIR)
        buildFlags.deleteVoid = flags.containsKey(F_VOID_TO_AIR)

        val (root, schema) = Pair(flags[F_PLUGIN], flags[F_SCHEMA]).let { (plug, schema) ->
            if (plug.isNullOrBlank()) {
                if (schema.isNullOrBlank()) {
                    Pair(null, null)
                } else {
                    Pair(root("we"), schema)
                }
            } else {
                if (schema.isNullOrBlank()) {
                    Pair(root(plug), defaultSchema(plug))
                } else {
                    Pair(root(plug), schema)
                }
            }
        }

        return if (root != null && schema != null) {
            makeFromFile(gameName, schema, root, sender, buildFlags)
            true
        } else {
            makeFromSelection(gameName, sender, buildFlags)
            true
        }
    }

    private fun makeFromSelection(gameName: String, sender: CommandSender, flags: Game.BuildFlags) {
        if (sender !is Player) {
            context(Text.MUST_BE_PLAYER).sendTo(sender)
            return
        }
        val owner: SessionOwner = BukkitAdapter.adapt(sender)
        val session = WorldEdit.getInstance().sessionManager.getIfPresent(owner)
        if (session == null || session.selectionWorld == null) {
            context(Text.CREATE_NEED_SELECTION).sendTo(sender)
        } else {
            try {
                val region = session.getSelection(session.selectionWorld)
                val box = selectionBounds(region)
                try {
                    val game = buildGameFromRegion(gameName, box, flags)
                    context(Text.CREATE_SUCCESS)
                            .with("game", game)
                            .sendTo(sender)
                } catch (e: Exception) {
                    context(Text.CREATE_ERROR)
                            .with("error", e.message ?: "")
                            .sendTo(sender)
                    e.printStackTrace()
                }
            } catch (e: IncompleteRegionException) { // FIXME can selection occur in world other than selection?
                throw RuntimeException("Selection World different to selection", e)
            }
        }
    }

    private fun makeFromFile(gameName: String, schemaName: String, saveDir: File, player: Player, flags: Game.BuildFlags) {
        val matches = saveDir.listFiles { dir: File -> dir.path.contains(schemaName) } ?: emptyArray()
        matches // The minimum length path will be the closest match
            .minByOrNull { it.name.length }?.also { file: File ->
                    try {
                        val game = buildGameFromSchema(gameName, player.location, file, flags)
                        context(Text.CREATE_SUCCESS)
                                .with("game", game)
                                .sendTo(player)
                    } catch (e: Exception) {
                        context(Text.CREATE_ERROR)
                                .with("error", e.message ?: "")
                                .sendTo(player)
                        e.printStackTrace()
                    }
            } ?: run {
                context(Text.CREATE_SCHEMA_NOT_FOUND
                        .with("schema", Message.of(schemaName)))
                        .sendTo(player)
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