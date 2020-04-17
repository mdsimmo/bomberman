package io.github.mdsimmo.bomberman.commands.game

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.session.SessionOwner
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.game.Game.Companion.BuildGameFromRegion
import io.github.mdsimmo.bomberman.game.Game.Companion.BuildGameFromSchema
import io.github.mdsimmo.bomberman.game.GameRegistry
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import io.github.mdsimmo.bomberman.utils.WorldEditUtils.selectionBounds
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.util.*

class GameCreate(parent: Cmd) : Cmd(parent) {
    override fun name(): Message {
        return context(Text.CREATE_NAME).format()
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> GameRegistry.allGames().map(Game::name)
            2 -> listOf("WorldEdit", "Bomberman", "wand")
            3 -> {
                val root = root(args[1]) ?: return emptyList()
                val allFiles = allFiles(root)
                allFiles.map { it.path.substring(root.path.length + 1) }
            }
            4 -> listOf("skipAir")
            else -> emptyList()
        }
    }

    override fun run(sender: CommandSender, args: List<String>): Boolean {
        if (args.isEmpty())
            return false
        if (sender !is Player) {
            context(Text.MUST_BE_PLAYER).sendTo(sender)
            return true
        }
        val gameName = args[0]
        GameRegistry.byName(gameName)?.let { game ->
            context(Text.CREATE_GAME_EXISTS)
                    .with("game", game)
                    .sendTo(sender)
            return true
        }

        val mutArgs = args.toMutableList()
        if (mutArgs.size < 2) {
            mutArgs.add("bm")
            mutArgs.add("purple")
            mutArgs.add("skipAir")
        }
        if ("wand".equals(mutArgs[1], ignoreCase = true)) {
            return if (mutArgs.size == 2) {
                makeFromSelection(gameName, sender)
                true
            } else {
                false
            }
        }
        val skipAir = mutArgs.getOrNull(3)
                .equals("skipAir", ignoreCase = true)
        if (mutArgs.size >= 3) {
            val saveDir = root(mutArgs[1]) ?: return false
            makeFromFile(gameName, mutArgs[2], saveDir, sender, skipAir)
            return true
        } else {
            return false
        }
    }

    private fun makeFromSelection(gameName: String, sender: CommandSender) {
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
                val game = BuildGameFromRegion(gameName, box)
                Text.CREATE_SUCCESS.with("game", game).sendTo(sender)
            } catch (e: IncompleteRegionException) { // FIXME can selection occur in world other than selection?
                throw RuntimeException("Selection World different to selection", e)
            }
        }
    }

    private fun makeFromFile(gameName: String, schemaName: String, saveDir: File, player: Player, skipAir: Boolean) {
        val matches = saveDir.listFiles { dir: File -> dir.path.contains(schemaName) } ?: emptyArray()
        matches // The minimum length path will be the closest match
            .minBy { it.name.length }?.also { file: File ->
                val game = BuildGameFromSchema(gameName, player.location, file, skipAir)
                GameRegistry.saveGame(game)
                context(Text.CREATE_SUCCESS)
                        .with("game", game)
                        .sendTo(player)
            } ?: run {
                context(Text.CREATE_SCHEMA_NOT_FOUND
                        .with("schema", Message.of(schemaName)))
                        .sendTo(player)
            }
    }

    override fun permission(): Permission {
        return Permission.GAME_DICTATE
    }

    override fun example(): Message {
        return context(Text.CONVERT_EXAMPLE).format()
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

private val bm = Bomberman.instance
private val we = WorldEdit.getInstance()
private fun root(plugin: String): File? {
    if (plugin.equals("bomberman", ignoreCase = true)
            || plugin.equals("bm", ignoreCase = true)) {
        return bm.settings.builtinSaves()
    }
    if (plugin.equals("worldedit", ignoreCase = true) || plugin.equals("we", ignoreCase = true))
        return we.getWorkingDirectoryFile(we.configuration.saveDir)
    return null
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