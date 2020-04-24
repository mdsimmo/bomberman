package io.github.mdsimmo.bomberman.commands.game.set

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.GameCommand
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.command.CommandSender

class SetSchema(parent: Cmd) : GameCommand(parent) {
    override fun name(): Message {
        return context(Text.SET_SCHEMA_NAME).format()
    }

    override fun gameOptions(args: List<String>): List<String> {
        return emptyList()
    }

    override fun gameRun(sender: CommandSender, args: List<String>, flags: Map<String, String>, game: Game): Boolean {
//        if (args.size != 1) return false
        // FIXME how to get schema from name?
  //      val newSchema = File(args[0])
        /*if (newboard == null) {
            Chat.sendMessage(getMessage(
                    Text.INVALID_SCHEMA, sender)
                    .put("arena", args.get(0)));
            return true;
        }*/
//game.switchSchema(newSchema);
        sender.sendMessage("Not implemented yet")
        return true
    }

    override fun permission(): Permission {
        return Permissions.SET
    }

    override fun extra(): Message {
        return context(Text.SET_SCHEMA_EXTRA).format()
    }

    override fun example(): Message {
        return context(Text.SET_SCHEMA_EXAMPLE).format()
    }

    override fun description(): Message {
        return context(Text.SET_SCHEMA_DESCRIPTION).format()
    }

    override fun usage(): Message {
        return context(Text.SET_SCHEMA_USAGE).format()
    }
}