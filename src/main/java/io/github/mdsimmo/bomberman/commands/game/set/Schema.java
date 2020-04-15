package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.List;

public class Schema extends GameCommand {

    public Schema(Cmd parent) {
        super(parent);
    }

    @Override
    public Message name() {
        return context(Text.SET_SCHEMA_NAME).format();
    }

    @Override
    public List<String> gameOptions(List<String> args) {
        if (args.size() == 1)
            // FIXME how to list avaliable schematics?
            return null;
        else
            return null;
    }

    @Override
    public boolean gameRun(final CommandSender sender, List<String> args, final Game game) {
        if (args.size() != 1)
            return false;
        // FIXME how to get schema from name?
        File newSchema = new File(args.get(0));
        /*if (newboard == null) {
            Chat.sendMessage(getMessage(
                    Text.INVALID_SCHEMA, sender)
                    .put("arena", args.get(0)));
            return true;
        }*/

        //game.switchSchema(newSchema);
        sender.sendMessage("Not implemented yet");
        return true;
    }

    @Override
    public Permission permission() {
        return Permission.GAME_DICTATE;
    }

    @Override
    public Message extra() {
        return context(Text.SET_SCHEMA_EXTRA).format();
    }

    @Override
    public Message example() {
        return context(Text.SET_SCHEMA_EXAMPLE).format();
    }

    @Override
    public Message description() {
        return context(Text.SET_SCHEMA_DESCRIPTION).format();
    }

    @Override
    public Message usage() {
        return context(Text.SET_SCHEMA_USAGE).format();
    }

}
