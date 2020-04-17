package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender
import java.util.*

abstract class CommandGroup(parent: Cmd?) : Cmd(parent) {
    private val children: MutableList<Cmd> = ArrayList()

    /**
     * Adds some child commands. Should be called from the constructor of every implementation
     *
     * @param children the child commands
     */
    fun addChildren(vararg children: Cmd) {
        this.children.addAll(listOf(*children))
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            in 0..1 -> children.map {
                it.name().toString()
            }
            else -> {
                children.firstOrNull { c -> c.name().toString().equals(args.first(), ignoreCase = true) }
                        ?.options(sender, args.drop(1))
                        ?: emptyList()
            }
        }
    }

    override fun extra(): Message {
        return Text.COMMAND_GROUP_EXTRA.format()
    }

    override fun example(): Message {
        return context(Text.COMMAND_GROUP_EXAMPLE).format()
    }

    override fun usage(): Message {
        return context(Text.COMMAND_GROUP_USAGE).format()
    }

    override fun run(sender: CommandSender, args: List<String>): Boolean {
        return if (args.isEmpty()) {
            help(sender)
            true
        } else {
            for (c in children) {
                if (c.name().toString().equals(args[0], ignoreCase = true)) {
                    return c.execute(sender, args.drop(1))
                }
            }
            context(Text.UNKNOWN_COMMAND)
                    .with("attempt", args[0])
                    .sendTo(sender)
            help(sender)
            true
        }
    }
}