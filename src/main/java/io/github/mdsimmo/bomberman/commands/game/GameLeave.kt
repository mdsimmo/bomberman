package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmPlayerLeaveGameIntent
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameLeave(parent: Cmd) : Cmd(parent) {

    companion object {
        private const val F_TARGET = "t"
    }

    override fun name(): Formattable {
        return Text.LEAVE_NAME
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

    override fun flagDescription(flag: String): Formattable {
        return when(flag) {
            F_TARGET -> Text.LEAVE_FLAG_TARGET
            else -> Message.empty
        }
    }

    override fun flagExtension(flag: String): Formattable {
        return when(flag) {
            F_TARGET -> Text.LEAVE_FLAG_TARGET_EXT
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
                Text.DENY_PERMISSION.format(cmdContext()).sendTo(sender)
                return true
            }

            // select the targets
            GameJoin.select(selection, sender).fold(
                onSuccess = { it },
                onFailure = {
                    Text.INVALID_TARGET_SELECTOR.format(cmdContext()
                            .plus("selector", selection))
                        .sendTo(sender)
                    return true
                }
            )
        } else {
            // no target selector, apply to sender
            if (sender !is Player) {
                Text.MUST_BE_PLAYER.format(cmdContext()).sendTo(sender)
                return true
            }
            listOf(sender)
        }

        targets.forEach { target ->
            val e = BmPlayerLeaveGameIntent.leave(target)
            if (e.isHandled()) {
                Text.LEAVE_SUCCESS.format(cmdContext()
                            .plus("player", target)
                            .plus("game", e.game ?: Message.error("none")))
                        .sendTo(target)
            } else {
                Text.LEAVE_NOT_JOINED.format(cmdContext()
                            .plus("player", target))
                        .sendTo(target)
            }
        }
        return true
    }

    override fun permission(): Permission {
        return Permissions.LEAVE
    }

    override fun extra(): Formattable {
        return Text.LEAVE_EXTRA
    }

    override fun description(): Formattable {
        return Text.LEAVE_DESCRIPTION
    }

    override fun usage(): Formattable {
        return Text.LEAVE_USAGE
    }

    override fun example(): Formattable {
        return Text.JOIN_EXAMPLE
    }
}