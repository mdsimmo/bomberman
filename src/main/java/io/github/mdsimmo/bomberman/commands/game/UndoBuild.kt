package io.github.mdsimmo.bomberman.commands.game

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmGameDeletedIntent
import io.github.mdsimmo.bomberman.events.BmGameLookupIntent
import io.github.mdsimmo.bomberman.messaging.Formattable
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
        private val gameMemory = mutableMapOf<String, Triple<Location, Clipboard, Int>>()

        fun retainHistory(name: String, box: Box) {
            // Copy the blocks to a clipboard
            val region = WorldEditUtils.convert(box)
            val clipboard = BlockArrayClipboard(region)
            WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(box.world))
                .use { editSession ->
                    val forwardExtentCopy = ForwardExtentCopy(
                        editSession, region, clipboard, region.minimumPoint
                    )
                    Operations.complete(forwardExtentCopy)
                }

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

        fun removeHistory(name: String) {
            gameMemory.remove(name)
        }
    }



    override fun name(): Formattable {
        return Text.UNDO_NAME
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
            Text.UNDO_UNKNOWN_GAME.format(cmdContext()
                    .plus("game", gameName))
                    .sendTo(sender)
            return true
        }

        // Delete the game if it exists still
        BmGameLookupIntent.find(gameName)?.let { game ->
            BmGameDeletedIntent.delete(game, true)
            Text.UNDO_DELETED.format(cmdContext()
                    .plus("game", game))
                    .sendTo(sender)
        }

        // Paste the old clipboard
        WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(origin.world))
            .use { editSession ->

                val operation = ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(origin.blockX, origin.blockY, origin.blockZ))
                    .copyEntities(true)
                    .build()
                Operations.complete(operation)

                editSession.close()
            }

        // Remove memory of game
        gameMemory.remove(gameName)

        // Signal success
        Text.UNDO_SUCCESS.format(cmdContext()
            .plus("game", gameName))
            .sendTo(sender)
        return true
    }

    override fun permission(): Permission {
        return Permissions.UNDO
    }

    override fun example(): Formattable {
        return Text.UNDO_EXAMPLE
    }

    override fun extra(): Formattable {
        return Text.UNDO_EXTRA
    }

    override fun description(): Formattable {
        return Text.UNDO_DESCRIPTION
    }

    override fun usage(): Formattable {
        return Text.UNDO_USAGE
    }
}