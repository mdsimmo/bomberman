package io.github.mdsimmo.bomberman.commands

import org.bukkit.command.CommandSender

interface Permission {
    fun isAllowedBy(sender: CommandSender): Boolean
}

enum class Permissions(val permission: String) : Permission {
    BASE("bomberman.bm"),
    CREATE("bomberman.create"),
    DELETE("bomberman.delete"),
    UNDO("bomberman.undo"),
    RELOAD("bomberman.reload"),
    CONFIGURE("bomberman.configure"),
    START("bomberman.start"),
    STOP("bomberman.stop"),
    INFO("bomberman.info"),
    LIST("bomberman.list"),
    JOIN("bomberman.join"),
    JOIN_REMOTE("bomberman.join.remote"),
    LEAVE("bomberman.leave"),
    LEAVE_REMOTE("bomberman.leave.remote");

    override fun isAllowedBy(sender: CommandSender): Boolean {
        return sender.hasPermission(permission)
    }
}