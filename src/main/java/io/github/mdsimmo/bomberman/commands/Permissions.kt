package io.github.mdsimmo.bomberman.commands

import org.bukkit.command.CommandSender

interface Permission {
    fun isAllowedBy(sender: CommandSender): Boolean
}

enum class Permissions(val permission: String) : Permission {
    BASE("bomberman.bm"),
    NEWGAME("bomberman.newgame"),
    NEWLOBBY("bomberman.newlobby"),
    DELETE("bomberman.delete"),
    RELOAD("bomberman.reload"),
    CONFIGURE("bomberman.configure"),
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