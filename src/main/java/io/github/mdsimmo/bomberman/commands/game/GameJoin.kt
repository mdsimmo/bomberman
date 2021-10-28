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
import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class GameJoin(parent: Cmd) : GameCommand(parent) {

    companion object {
        private const val F_TARGET = "t"

        fun select(name: String, source: CommandSender): CommandSender? {
            return if (name.equals("@p", ignoreCase = true)) {
                // closest player to source
                val location = when (source) {
                    is Entity -> source.location
                    is BlockCommandSender -> source.block.location
                    else -> return null
                }
                location.world?.players?.minByOrNull { location.distanceSquared(it.location) }
            } else {
                Bukkit.getOnlinePlayers().firstOrNull { name.equals(it.name, ignoreCase = true) }
            }
        }
    }

    override fun name(): Message {
        return context(Text.JOIN_NAME).format()
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
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

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
        if (args.isNotEmpty())
            return false

        val target = flags[F_TARGET]?.let { name ->
            select(name, sender)
                ?: run {
                    Text.INVALID_PLAYER
                        .with("player", name)
                        .sendTo(sender)
                    return true
                }
        } ?: sender

        if (target !is Player) {
            context(Text.MUST_BE_PLAYER)
                    .sendTo(sender)
            return true
        }
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