package io.github.mdsimmo.bomberman.commands.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.messaging.Message
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.event.HandlerList

class DevInfo(parent: Cmd) : Cmd(parent) {

    override fun name(): Message {
        return Message.of("dev")
    }

    override fun options(sender: CommandSender, args: List<String>): List<String> {
        return listOf("handlerlist", "handlercount", "handlerwatch", "nocancelled",
                "tasklist", "taskcount", "taskwatch", "watch")
    }

    override fun run(sender: CommandSender, args: List<String>, modifiers: Map<String, String>): Boolean {
        return when (args.getOrNull(0).toString().toLowerCase()) {
            "watch" -> {
                run(sender, listOf("nocancelled"), modifiers)
                run(sender, listOf("handlerwatch"), modifiers)
                run(sender, listOf("taskwatch"), modifiers)
                run(sender, listOf("taskcount"), modifiers)
                run(sender, listOf("handlercount"), modifiers)
                true
            }
            "handlerlist" -> {
                val handlers = HandlerList.getRegisteredListeners(Bomberman.instance).map {
                    it.listener
                }.filter { it::class.java.name.contains(modifiers["class"] ?: "") }
                sender.sendMessage(handlers.toString())
                true
            }
            "handlercount" -> {
                val handlers = HandlerList.getRegisteredListeners(Bomberman.instance)
                sender.sendMessage("Handlers: " + handlers.size.toString())
                true
            }
            "nocancelled" -> {
                Bukkit.getScheduler().scheduleSyncRepeatingTask(Bomberman.instance, {
                    HandlerList.getRegisteredListeners(Bomberman.instance).forEach {
                        if (!it.isIgnoringCancelled) {
                            sender.sendMessage("Not watching for cancelled: " + it.listener)
                        }
                    }
                }, 1, 1)
                sender.sendMessage("Watching for handlers watching for ignored events")
                true
            }
            "handlerwatch" -> {
                var handlers = HandlerList.getRegisteredListeners(Bomberman.instance).toSet()
                val startCount = handlers.size
                Bukkit.getScheduler().scheduleSyncRepeatingTask(Bomberman.instance, {
                    val updated = HandlerList.getRegisteredListeners(Bomberman.instance).toSet()
                    val added = updated.filterNot { handlers.contains(it) }
                    val removed = handlers.filterNot { updated.contains(it) }
                    added.forEach {
                        sender.sendMessage(" ${ChatColor.RED}+${ChatColor.RESET} " + it.listener)
                    }
                    removed.forEach {
                        sender.sendMessage(" ${ChatColor.GREEN}-${ChatColor.RESET} " + it.listener)
                    }
                    if (handlers.size != updated.size) {
                        val countDiff = updated.size - startCount
                        sender.sendMessage(if (countDiff > 0) {
                            ChatColor.RED
                        } else {
                            ChatColor.GREEN
                        }.toString() + " $countDiff handlers")
                    }
                    handlers = updated
                }, 1, 1)
                sender.sendMessage("Watching for new handlers")
                true
            }
            "tasklist" -> {
                Bukkit.getScheduler().pendingTasks.forEach { task ->
                    if (task.owner == Bomberman.instance) {
                        sender.sendMessage("Task with id: " + task.taskId)
                    }
                }
                true
            }
            "taskcount" -> {
                sender.sendMessage("Tasks: " + Bukkit.getScheduler().pendingTasks
                        .filter { it.owner == Bomberman.instance}
                        .count())
                true
            }
            "taskwatch" -> {
                var tasks = Bukkit.getScheduler().pendingTasks
                        .filter { it.owner == Bomberman.instance }.toSet()
                val startCount = tasks.size + 1 // allow for itself
                Bukkit.getScheduler().scheduleSyncRepeatingTask(Bomberman.instance, {
                    val updated = Bukkit.getScheduler().pendingTasks
                            .filter { it.owner == Bomberman.instance }.toSet()
                    val added = updated.filterNot { tasks.contains(it) }
                    val removed = tasks.filterNot { updated.contains(it) }
                    added.forEach {
                        sender.sendMessage(  " ${ChatColor.RED}+${ChatColor.RESET} " + it.taskId)
                    }
                    removed.forEach {
                        sender.sendMessage(" ${ChatColor.GREEN}-${ChatColor.RESET} " + it.taskId)
                    }

                    if (tasks.size != updated.size) {
                        val countDiff = updated.size - startCount
                        sender.sendMessage(
                                if (countDiff > 0) {
                                    ChatColor.RED
                                } else {
                                    ChatColor.GREEN
                                }.toString() + " $countDiff tasks")
                    }
                    tasks = updated
                }, 1, 1)
                sender.sendMessage("Watching for new tasks")
                true
            }
            else -> {
                false
            }
        }
    }

    override fun permission(): Permission {
        return Permission.GAME_DICTATE
    }

    override fun example(): Message {
        return Message.empty
    }

    override fun extra(): Message {
        return Message.empty
    }

    override fun description(): Message {
        return Message.of("Dev commands. Was meant to remove this before shipping")
    }

    override fun usage(): Message {
        return Message.empty
    }
}