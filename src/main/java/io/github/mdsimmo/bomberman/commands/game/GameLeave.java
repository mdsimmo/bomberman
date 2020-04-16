package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.events.BmPlayerLeaveGameIntent;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GameLeave extends Cmd {

    public GameLeave(Cmd parent) {
        super(parent);
    }

    @Override
    public Message name() {
        return context(Text.LEAVE_NAME).format();
    }

    @Override
    public List<String> options(CommandSender sender, List<String> args) {
        return null;
    }

    @Override
    public boolean run(CommandSender sender, List<String> args) {
        if (args.size() != 0)
            return false;
        if (sender instanceof Player) {
            var e = BmPlayerLeaveGameIntent.leave( (Player) sender);
            if (e.isHandled()) {
                Text.LEAVE_SUCCESS.with("player", sender).sendTo(sender);
            } else {
                Text.LEAVE_NOT_JOINED.with("player", sender).sendTo(sender);
            }
        } else {
            context(Text.MUST_BE_PLAYER).sendTo(sender);
        }
        return true;
    }

    @Override
    public Permission permission() {
        return Permission.PLAYER;
    }

    @Override
    public Message extra() {
        return context(Text.LEAVE_EXTRA).format();
    }

    @Override
    public Message description() {
        return context(Text.LEAVE_DESCRIPTION).format();
    }

    @Override
    public Message usage() {
        return context(Text.LEAVE_USAGE).format();
    }

    @Override
    public Message example() {
        return context(Text.JOIN_EXAMPLE).format();
    }

}
