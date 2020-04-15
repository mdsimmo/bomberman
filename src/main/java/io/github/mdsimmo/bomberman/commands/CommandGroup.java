package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CommandGroup extends Cmd {

    List<Cmd> children = new ArrayList<>();

    public CommandGroup(Cmd parent, Cmd ... children) {
        super(parent);
        addChildren(children);
    }

    /**
     * Adds some child commands. Should be called from the constructor of every implementation
     *
     * @param children the child commands
     */
    protected void addChildren(Cmd... children) {
        this.children.addAll(Arrays.asList(children));
    }

    @Override
    public List<String> options(CommandSender sender, List<String> args) {
        if (args.size() != 1)
            return null;
        List<String> options = new ArrayList<>();
        for (Cmd c : children) {
            options.add(c.name().toString());
        }
        return options;
    }

    @Override
    public Message extra() {
        return Text.COMMAND_GROUP_EXTRA.format();
    }

    @Override
    public Message example() {
        return context(Text.COMMAND_GROUP_EXAMPLE).format();
    }

    public Message usage() {
        return context(Text.COMMAND_GROUP_USAGE).format();
    }

    @Override
    public boolean run(CommandSender sender, List<String> args) {
        if (args.size() == 0) {
            help(sender);
            return true;
        } else {
            for (Cmd c : children) {
                if (c.name().toString()
                        .equalsIgnoreCase(args.get(0))) {
                    args.remove(0);
                    return c.execute(sender, args);
                }
            }
            context(Text.UNKNOWN_COMMAND)
                    .with("attempt", args.get(0))
                    .sendTo(sender);
            help(sender);
            return true;
        }
    }

    public Cmd getCommand(CommandSender sender, List<String> args) {
        if (args.size() == 0)
            return this;
        for (Cmd cmd : children) {
            if (cmd.name().toString().equals(args.get(0))
                    && cmd.isAllowedBy(sender)) {
                args.remove(0);
                if (cmd instanceof CommandGroup)
                    return ((CommandGroup) cmd).getCommand(sender, args);
                else
                    return cmd;
            }
        }
        return this;
    }

}
