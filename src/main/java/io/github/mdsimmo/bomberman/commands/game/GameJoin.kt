package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmPlayerJoinGameIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.IllegalArgumentException

class GameJoin(parent: Cmd) : GameCommand(parent) {

    companion object {
        private const val F_TARGET = "t"

        fun select(target: String, source: CommandSender): Result<List<Player>> {
            return try {
                Result.success(Bukkit.selectEntities(source, target).filterIsInstance<Player>())
            } catch (e: IllegalArgumentException) {
                Result.failure(e)
            }
        }
    }

    override fun name(): Message {
        return context(Text.JOIN_NAME).format()
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun flags(sender: CommandSender): Set<String> {
        return setOf(F_TARGET)
    }

    override fun flagOptions(flag: String): Set<String> {
        return when (flag) {
            F_TARGET -> Bukkit.getOnlinePlayers().map { it.name }.toSet().plus(arrayOf("@a", "@p", "@r", "@s"))
            else -> emptySet()
        }
    }

    override fun flagDescription(flag: String): Message {
        return when(flag) {
            F_TARGET -> context(Text.JOIN_FLAG_TARGET).format()
            else -> Message.empty
        }
    }

    override fun flagExtension(flag: String): Message {
        return when(flag) {
            F_TARGET -> context(Text.JOIN_FLAG_TARGET_EXT).format()
            else -> Message.empty
        }
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false

        // Get the targeted player (if not specified, defaults to the sender)
        val targets = if (flags[F_TARGET] != null) {
            val selection = flags[F_TARGET]!!

            // Deny permissions if not allowed to select others
            if (!Permissions.JOIN_REMOTE.isAllowedBy(sender)) {
                context(Text.DENY_PERMISSION)
                    .sendTo(sender)
                return true
            }

            // select the targets
            select(selection, sender).fold(
                onSuccess = { it },
                onFailure = {
                    Text.INVALID_TARGET_SELECTOR
                        .with("selector", selection)
                        .sendTo(sender)
                    return true
                }
            )
        } else {
            // no target selector, apply to sender
            if (sender !is Player) {
                context(Text.MUST_BE_PLAYER).sendTo(sender)
                return true
            }
            listOf(sender)
        }

        targets.forEach { target ->
            val e = BmPlayerJoinGameIntent.join(game, target)

            if (e.isCancelled) {
                e.cancelledReason()
                    ?.apply {
                        sendTo(sender)
                    }
                    ?: run {
                        context(Text.COMMAND_CANCELLED)
                            .with("game", game)
                            .with("player", target)
                            .sendTo(target)
                    }
            } else {
                context(Text.JOIN_SUCCESS)
                    .with("game", game)
                    .with("player", target)
                    .sendTo(target)
            }
        }
        return true
    }

    override fun permission(): Permission {
        return Permissions.JOIN
    }

    override fun extra(): Message {
        return context(Text.JOIN_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.JOIN_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.JOIN_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.JOIN_USAGE).format()
    }
}