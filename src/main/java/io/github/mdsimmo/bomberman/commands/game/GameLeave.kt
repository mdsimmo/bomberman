package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmPlayerLeaveGameIntent
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameLeave(parent: Cmd) : Cmd(parent) {

    companion object {
        private const val F_TARGET = "t"
    }

    override fun name(): Message {
        return context(Text.LEAVE_NAME).format()
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return listOf()
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
            F_TARGET -> context(Text.LEAVE_FLAG_TARGET).format()
            else -> Message.empty
        }
    }

    override fun flagExtension(flag: String): Message {
        return when(flag) {
            F_TARGET -> context(Text.LEAVE_FLAG_TARGET_EXT).format()
            else -> Message.empty
        }
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
        if (args.isNotEmpty())
            return false

        // Get the targeted player (if not specified, defaults to the sender)
        val targets = if (flags[F_TARGET] != null) {
            val selection = flags[F_TARGET]!!

            // Deny permissions if not allowed to select others
            if (!Permissions.LEAVE_REMOTE.isAllowedBy(sender)) {
                context(Text.DENY_PERMISSION)
                    .sendTo(sender)
                return true
            }

            // select the targets
            GameJoin.select(selection, sender).fold(
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
            val e = BmPlayerLeaveGameIntent.leave(target)
            if (e.isHandled()) {
                Text.LEAVE_SUCCESS
                        .with("player", target)
                        .with("game", e.game ?: Message.error("none"))
                        .sendTo(target)
            } else {
                Text.LEAVE_NOT_JOINED
                        .with("player", target)
                        .sendTo(target)
            }
        }
        return true
    }

    override fun permission(): Permission {
        return Permissions.LEAVE
    }

    override fun extra(): Message {
        return context(Text.LEAVE_EXTRA).format()
    }

    override fun description(): Message {
        return context(Text.LEAVE_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.LEAVE_USAGE).format()
    }

    override fun example(): Message {
        return context(Text.JOIN_EXAMPLE).format()
    }
}