package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.commands.game.*;
import io.github.mdsimmo.bomberman.commands.game.set.Set;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseCommand extends CommandGroup implements TabCompleter, CommandExecutor {

    private static final JavaPlugin plugin = Bomberman.instance;

    public BaseCommand() {
        super(null);
        plugin.getCommand("bomberman").setExecutor(this);
        plugin.getCommand("bomberman").setTabCompleter(this);

        addChildren(
                new Set(this),
                new Create(this),
                new Info(this),
                new Join(this),
                new Leave(this),
                new Delete(this),
                new Start(this),
                new Stop(this),
                new GameList(this)
        );
    }

    @Override
    public Message name() {
        return Message.of("bomberman");
    }

    @Override
    public Permission permission() {
        return Permission.PLAYER;
    }

    @Override
    public Message description() {
        return context(Text.BOMBERMAN_DESCRIPTION).format();
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String s,
                             @Nonnull String[] args) {
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        Cmd c = getCommand(sender, arguments);
        if (arguments.size() > 0
                && arguments.get(arguments.size() - 1).equals("?")) {
            c.help(sender);
            return true;
        }
        if (!c.execute(sender, arguments)) {
            c.incorrectUsage(sender, arguments);
            c.help(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command,
                                      @Nonnull String s, @Nonnull String[] args) {
        List<String> arguments = new ArrayList<String>(Arrays.asList(args));
        Cmd c = getCommand(sender, arguments);
        List<String> options = new ArrayList<>();
        List<String> all = c.options(sender, arguments);
        if (all == null)
            all = new ArrayList<>();
        else {
            all  = new ArrayList<>(all); // allow immutable return from options
        }
        all.add("?");
        for (String option : all) {
            if (StringUtil
                    .startsWithIgnoreCase(option, args[args.length - 1]))
                options.add(option);
        }
        return options;
    }
}
