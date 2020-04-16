package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.events.BmRunStoppedIntent;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RunStop extends GameCommand {

    public RunStop(Cmd parent) {
        super(parent);
    }

    @Override
    public Message name() {
        return context(Text.STOP_NAME).format();
    }

    @Override
    public boolean gameRun(CommandSender sender, List<String> args, Game game) {
        if (args.size() != 0)
            return false;
        var e = BmRunStoppedIntent.stopGame(game);
        if (!e.isCancelled())
            Text.STOP_SUCCESS
                    .with("game", game)
                    .sendTo(sender);
        else {
            e.cancelledReason()
                    .orElseGet(() -> Text.COMMAND_CANCELLED
                            .with("command", this)
                            .format())
                    .sendTo(sender);
        }
        return true;
    }

    @Override
    public Permission permission() {
        return Permission.GAME_OPERATE;
    }

    @Override
    public List<String> gameOptions(List<String> args) {
        return null;
    }

    @Override
    public Message extra() {
        return context(Text.STOP_EXTRA).format();
    }

    @Override
    public Message example() {
        return context(Text.STOP_EXAMPLE).format();
    }

    @Override
    public Message description() {
        return context(Text.STOP_DESCRIPTION).format();
    }

    @Override
    public Message usage() {
        return context(Text.STOP_USAGE).format();
    }

}
