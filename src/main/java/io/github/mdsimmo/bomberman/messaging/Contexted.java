package io.github.mdsimmo.bomberman.messaging;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Collection;

@CheckReturnValue
@Nonnull
public interface Contexted {

    @Nonnull Contexted with(String key, Formattable arg);

    @Nonnull Message format();

    @Nonnull
    default Contexted with(@Nonnull String key, @Nonnull String value) {
        return with(key, new Formattable.StringWrapper(value));
    }

    @Nonnull
    default Contexted with(String key, int value) {
        return with(key, new Formattable.StringWrapper(String.valueOf(value)));
    }

    default Contexted with(String key, Collection<? extends Formattable> value) {
        return with(key, new Formattable.CollectionWrapper<>(value));
    }

    default Contexted with(String key, ItemStack stack) {
        return with(key, new Formattable.ItemWrapper(stack));
    }

    default Contexted with(String key, CommandSender sender) {
        return with(key, new Formattable.SenderWrapper(sender));
    }

    default void sendTo(CommandSender sender) {
        format().sendTo(sender);
    }

}
