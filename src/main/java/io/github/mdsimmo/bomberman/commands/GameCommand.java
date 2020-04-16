package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GameRegistry;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GameCommand extends Cmd {

    public GameCommand(Cmd parent) {
        super(parent);
    }

    @Override
    public final List<String> options(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            return GameRegistry.allGames().stream().map(Game::getName).collect(Collectors.toList());
        } else {
            args.remove(0);
            return gameOptions(args);
        }
    }

    public abstract List<String> gameOptions(List<String> args);

    @Override
    public final boolean run(CommandSender sender, List<String> args) {
        if (args.size() <= 0)
            return false;
        return GameRegistry.byName(args.get(0)).map(game -> {
            args.remove(0);
            return gameRun(sender, args, game);
        }).orElse(false);
    }

    public abstract boolean gameRun(CommandSender sender, List<String> args, Game game);
}