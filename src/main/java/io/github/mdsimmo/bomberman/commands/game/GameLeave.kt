package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.events.BmPlayerLeaveGameIntent
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class GameLeave(parent: Cmd) : Cmd(parent) {

    companion object {
        private const val F_TARGET = "t"
    }

    override fun name(): Message {
        return context(Text.LEAVE_NAME).format()
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return listOf(F_TARGET)
    }

    override fun flags(sender: CommandSender, args: List<String>, flags: Map<String, String>): Set<String> {
        return setOf(F_TARGET)
    }

    override fun flagOptions(sender: CommandSender, flag: String, args: List<String>, flags: Map<String, String>): Set<String> {
        return when (flag) {
            F_TARGET -> Bukkit.getOnlinePlayers().map { it.name }.toSet()
            else -> emptySet()
        }
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
        if (args.isNotEmpty())
            return false

        val target = flags[F_TARGET]?.let { name ->
            GameJoin.select(name, sender)
                ?: run {
                    Text.INVALID_PLAYER
                        .with("player", name)
                        .sendTo(sender)
                    return true
                }
        } ?: sender

        if (target is Player) {
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
        } else {
            context(Text.MUST_BE_PLAYER)
                    .sendTo(target)
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