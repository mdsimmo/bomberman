package io.github.mdsimmo.bomberman.commands;

import io.github.mdsimmo.bomberman.messaging.Contexted;
import io.github.mdsimmo.bomberman.messaging.Formattable;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Cmd implements Formattable {

    public enum Permission {

        PLAYER("bomberman.player"),
        GAME_OPERATE("bomberman.operator"),
        GAME_DICTATE("bomberman.dictator");

        public final String permission;

        Permission(String permission) {
            this.permission = permission;
        }

        public boolean isAllowedBy(CommandSender sender) {
            return sender.hasPermission(permission);
        }
    }

    protected Cmd parent;

    public Cmd(Cmd parent) {
        this.parent = parent;
    }

    /**
     * Gets the commands name.
     * This should not include the path (eg, "fare" instead of "bm.game.set.fare")
     * Do not put any spaces
     *
     * @return the name
     */
    public abstract Message name();

    /**
     * Gets a list of possible values to return.
     *
     * @param args the current arguments typed
     * @return the options
     */
    @Nullable
    public abstract List<String> options(CommandSender sender, List<String> args);

    /**
     * Execute the command
     *
     * @param sender the sender
     * @param args   the arguments
     * @return true if correctly typed. False will display info
     */
    public abstract boolean run(CommandSender sender, List<String> args);

    public boolean execute(CommandSender sender, List<String> args) {
        if (isAllowedBy(sender)) {
            if (run(sender, args))
                return true;
            else {
                if (args.size() == 0) {
                    // assume asking for help
                    help(sender);
                    return true;
                } else
                    return false;
            }

        } else {
            context(Text.DENY_PERMISSION).sendTo(sender);
            return true;
        }
    }

    /**
     * Sends help to the sender
     *
     * @param sender the player to help
     */
    public void help(CommandSender sender) {
        context(Text.COMMAND_FORMAT).sendTo(sender);
    }

    public abstract Message extra();

    public abstract Message example();

    public abstract Message description();

    public abstract Message usage();

    /**
     * @return the permission needed to run this command
     */
    public abstract Permission permission();

    /**
     * gets if the given sender has permission to run this command
     *
     * @param sender the sender
     * @return true if they have permission
     */
    public boolean isAllowedBy(CommandSender sender) {
        return permission().isAllowedBy(sender);
    }

    /**
     * short for path(" ");
     */
    public String path() {
        return path(" ");
    }

    /**
     * gets the path to the command
     *
     * @param separator what to separate parent/child commands by
     * @return the path
     */
    private String path(String separator) {
        String path = "";
        if (parent != null)
            path += parent.path(separator) + separator;
        path += name().toString();
        return path;
    }

    public void incorrectUsage(CommandSender sender, List<String> args) {
        context(Text.INCORRECT_USAGE)
				.with("attempt", new Formattable.CollectionWrapper<>(
				        args.stream()
                                .map(Message::of)
                                .collect(Collectors.toList())
                ))
                .sendTo(sender);
    }

    public Contexted context(Contexted text) {
        return text.with("command", this);
    }

    @Override
    public Message format(@Nonnull List<Message> args) {
        if (args.size() == 0)
            args.add(Message.of("name"));
        switch (args.remove(0).toString()) {
            case "name":
                return name();
            case "path":
                return Message.of(path());
            case "usage":
                return usage();
            case "extra":
                return extra();
            case "example":
                return example();
            case "description":
                return description();
            default:
                throw new RuntimeException("Unknown value " + args.get(0));
        }
    }
}
