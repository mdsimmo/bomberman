package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.CommandGroup;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

public class Set extends CommandGroup {

    public Set(Cmd parent) {
        super(parent);
        addChildren(
                new Schema(this),
                new Lives(this),
                new MinPlayers(this)
        );
    }

    @Override
    public Message name() {
        return context(Text.SET_NAME).format();
    }

    @Override
    public Permission permission() {
        return Permission.GAME_DICTATE;
    }

    @Override
    public Message description() {
        return context(Text.SET_DESCRIPTION).format();
    }

}
