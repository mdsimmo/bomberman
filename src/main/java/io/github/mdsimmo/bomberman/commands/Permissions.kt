package io.github.mdsimmo.bomberman.commands

import org.bukkit.command.CommandSender

interface Permission {
    fun isAllowedBy(sender: CommandSender): Boolean
}

enum class Permissions(val permission: String) : Permission {
    BASE("bomberman.bm"),
    CREATE("bomberman.create"),
    DELETE("bomberman.delete"),
    RELOAD("bomberman.reload"),
    SET("bomberman.set"),
    START("bomberman.start"),
    STOP("bomberman.stop"),
    INFO("bomberman.info"),
    LIST("bomberman.list"),
    JOIN("bomberman.join"),
    LEAVE("bomberman.leave");

    override fun isAllowedBy(sender: CommandSender): Boolean {
        return sender.hasPermission(permission)
    }
}