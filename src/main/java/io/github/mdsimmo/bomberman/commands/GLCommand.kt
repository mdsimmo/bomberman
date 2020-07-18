package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.events.BmJoinableListIntent
import io.github.mdsimmo.bomberman.events.BmGLLookupIntent
import io.github.mdsimmo.bomberman.game.GL
import org.bukkit.command.CommandSender

abstract class GLCommand(parent: Cmd) : Cmd(parent) {
    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return if (args.size <= 1) {
            BmJoinableListIntent.list().map(GL::name).toList()
        } else {
            emptyList()
        }
    }

    override fun run(sender: CommandSender, args: List<String>, flags: Map<String, String>): Boolean {
        return if (args.isEmpty()) {
            false
        } else {
            BmGLLookupIntent.find(args[0]) ?.let {
                glRun(sender, args.drop(1), flags, it)
            } ?: false
        }
    }

    abstract fun glRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, gl: GL): Boolean
}