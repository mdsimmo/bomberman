package io.github.mdsimmo.bomberman.commands.game

import com.sk89q.worldedit.BlockVector
import com.sk89q.worldedit.CuboidClipboard
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.world.World
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmGameDeletedIntent
import io.github.mdsimmo.bomberman.events.BmGameLookupIntent
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import io.github.mdsimmo.bomberman.utils.Box
import io.github.mdsimmo.bomberman.utils.BukkitUtils
import io.github.mdsimmo.bomberman.utils.WorldEditUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender

class UndoBuild(parent: Cmd) : Cmd(parent) {

    companion object {
        // A collection of games that have the old state remembered
        private val gameMemory = mutableMapOf<String, Triple<Location, CuboidClipboard, Int>>()

        fun retainHistory(name: String, box: Box?) {
            if (box == null) {
                // delete memory
                gameMemory.remove(name)
            } else {

                // Copy the blocks to a clipboard
                val clipboard = CuboidClipboard(WorldEditUtils.convert(box.size), WorldEditUtils.convert(box.p1))
                val editSession = WorldEdit.getInstance().editSessionFactory.getEditSession(BukkitWorld(box.world) as World, -1)
                clipboard.copy(editSession)
                editSession.flushQueue()

                // remove the memory after a delay
                var handle = 0
                handle = Bukkit.getScheduler().scheduleSyncDelayedTask(Bomberman.instance, {
                    // check if same task handler (in case same game name has been destroyed/created multiple times)
                    if (gameMemory[name]?.third == handle) {
                        gameMemory.remove(name)
                        Bomberman.instance.logger.info("Game '$name' undo history expired")
                    }
                }, 10 * 60 * 20L) // 10 minutes

                // Retain the games memory
                gameMemory[name] = Triple(BukkitUtils.boxLoc1(box), clipboard, handle)
            }
        }
    }

    override fun name(): Message {
        return context(Text.UNDO_NAME).format()
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> gameMemory.keys.toList().sortedBy { it }
            else -> emptyList()
        }
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
        if (args.size != 1)
            return false
        val gameName = args[0]

        // Find the clipboard
        val (origin, clipboard, _) = gameMemory[gameName] ?: Triple(null, null, 0)
        if (clipboard == null || origin  == null) {
            context(Text.UNDO_UNKNOWN_GAME)
                    .with("game", gameName)
                    .sendTo(sender)
            return true
        }

        // Delete the game if it exists still
        BmGameLookupIntent.find(gameName)?.let { game ->
            BmGameDeletedIntent.delete(game, true)
            context(Text.UNDO_DELETED)
                    .with("game", game)
                    .sendTo(sender)
        }

        // Paste the old clipboard
        val editSession = WorldEdit.getInstance().editSessionFactory.getEditSession(BukkitWorld(origin.world), -1)
        clipboard.paste(editSession, BlockVector(origin.blockX, origin.blockY, origin.blockZ), false)
        editSession.flushQueue()

        // Remove memory of game
        gameMemory.remove(gameName)

        // Signal success
        context(Text.UNDO_SUCCESS)
            .with("game", gameName)
            .sendTo(sender)
        return true
    }

    override fun permission(): Permission {
        return Permissions.UNDO
    }

    override fun example(): Message {
        return context(Text.UNDO_EXAMPLE).format()
    }

    override fun extra(): Message {
        return context(Text.UNDO_EXTRA).format()
    }

    override fun description(): Message {
        return context(Text.UNDO_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.UNDO_USAGE).format()
    }
}