package io.github.mdsimmo.bomberman.commands.game;

import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GameRegistry;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;

public class GameList extends Cmd {

    public GameList(Cmd parent) {
        super(parent);
    }

    @Override
    public Message name() {
        return context(Text.GAMELIST_NAME).format();
    }

    @Override
    public List<String> options(CommandSender sender, List<String> args) {
        return null;
    }

    @Override
    public boolean run(CommandSender sender, List<String> args) {
        if (args.size() != 0)
            return false;
        Set<Game> games = GameRegistry.allGames();
        context(Text.GAMELIST_GAMES)
                .with("games", games)
                .sendTo(sender);
        return true;
    }

    @Override
    public Permission permission() {
        return Permission.PLAYER;
    }

    @Override
    public Message example() {
        return context(Text.GAMELIST_EXAMPLE).format();
    }

    @Override
    public Message extra() {
        return context(Text.GAMELIST_EXTRA).format();
    }

    @Override
    public Message description() {
        return context(Text.GAMELIST_DESCRIPTION).format();
    }

    @Override
    public Message usage() {
        return context(Text.GAMELIST_USAGE).format();
    }

}
