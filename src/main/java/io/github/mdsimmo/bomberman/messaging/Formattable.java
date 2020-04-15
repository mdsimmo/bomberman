package io.github.mdsimmo.bomberman.messaging;

import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public interface Formattable {

    Message format(@Nonnull List<Message> args);

    class StringWrapper implements Formattable {

        final String text;

        public StringWrapper(String text) {
            this.text = text;
        }

        @Override
        @Nonnull
        public Message format(@Nonnull List<Message> args) {
            return Message.of(text);
        }
    }

    class ItemWrapper implements Formattable {

        private ItemStack item;

        public ItemWrapper(ItemStack item) {
            this.item = item;
        }

        @Override
        public Message format(@Nonnull List<Message> args) {
            if (args.size() == 0)
                return Text.ITEM_FORMAT.with("item", this).format();
            switch (args.get(0).toString()) {
                case "amount":
                    return Message.of(item == null ? 0 : item.getAmount());
                case "type":
                    return Message.of(item == null ? "missingno" : item.getType().getKey().toString());
                default:
                    return Message.empty();
            }
        }
    }

    class SenderWrapper implements Formattable {

        private final CommandSender sender;

        public SenderWrapper(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public Message format(@Nonnull List<Message> args) {
            return Message.of(sender.getName());
        }

    }

    class ColorWrapper implements Formattable {

        private final ChatColor color;

        public ColorWrapper(ChatColor color) {
            this.color = color;
        }

        @Override
        public Message format(@Nonnull List<Message> args) {
            if (args.size() != 1)
                throw new RuntimeException("Colors must have exactly one argument. Given " + args.size());
            return args.get(0).color(color);
        }
    }

    class CollectionWrapper<T extends Formattable> implements Formattable {

        private final Collection<T> list;

        public CollectionWrapper(Collection<T> list) {
            this.list = list;
        }

        @Override
        public Message format(@Nonnull List<Message> args) {
            if (args.size() == 0) {
                args.add(Message.of("foreach"));
            }

            switch (args.get(0).toString()) {
                case "foreach":
                    // Join all elements by applying arg[1] to each item separated by arg[2]
                    if (args.size() < 2)
                        args.add(Message.of("{item}"));
                    if (args.size() < 3)
                        args.add(Message.of(", "));

                    var mapper = args.get(1).toString();
                    var separator = args.get(2);

                    AtomicInteger i = new AtomicInteger(0);
                    return list.stream()
                            .map(item -> Expander.expand(mapper,
                                    Map.of("value", item, "i", Message.of(i.getAndIncrement()))))
                            .reduce((a, b) -> a.append(separator).append(b))
                            .orElseGet(Message::empty);
                case "length":
                    return Message.of(list.size());
                default:
                    throw new IllegalArgumentException("Unknown list option: " + args.get(0));
            }
        }
    }

    class MapExpander implements Formattable {

        @Override
        public Message format(@Nonnull List<Message> args) {
            final int size = args.size();
            if (size % 2 != 0)
                throw new RuntimeException("map needs an even amount of arguments");

            Message text = Message.empty();
            for (int i = 0; i < size; i += 2) {
                var row = Text.MAP_FORMAT
                        .with("key", args.get(i))
                        .with("value", args.get(i + 1))
                        .format();
                text = text.append(row);
            }
            return text;
        }
    }

    class ListExpander implements Formattable {

        @Override
        public Message format(@Nonnull List<Message> args) {

            Message text = Message.empty();
            for (Message arg : args) {
                var row = Text.LIST_FORMAT
                        .with("value", arg)
                        .format();
                text = text.append(row);
            }
            return text;
        }
    }

    class HeadingExpander implements Formattable {

        @Override
        public Message format(@Nonnull List<Message> args) {
            if (args.size() != 1)
                throw new RuntimeException("Header must have one argument");
            return Text.HEADING_FORMAT
                    .with("value", args.get(0))
                    .format();
        }

    }

    class RawExpander implements Formattable {

        @Override
        public Message format(@Nonnull List<Message> args) {
            if (args.size() != 0)
                throw new IllegalArgumentException("{raw} cannot be used with arguments");
            return Message.rawFlag();
        }
    }

    class TitleExpander implements Formattable {

        @Override
        public Message format(@Nonnull List<Message> args) {
            if (args.size() == 0)
                throw new IllegalArgumentException("{title} needs at least one argument");
            Message text = args.get(0);
            Message subtitle = args.size() >= 2 ? args.get(1) : Message.empty();
            int fadein = args.size() >= 3 ? Integer.parseInt(args.get(2).toString()) : 0;
            int duration = args.size() >= 4 ? Integer.parseInt(args.get(3).toString()) : 20;
            int fadeout = args.size() >= 5 ? Integer.parseInt(args.get(4).toString()) : 0;

            return Message.title(text, subtitle, fadein, duration, fadeout);
        }

    }

    class Switch implements Formattable {

        @Override
        public Message format(@Nonnull List<Message> args) {
            final int size = args.size();
            if (size < 4)
                throw new RuntimeException("switch needs at least 4 arguments");
            if (size % 2 != 0)
                throw new RuntimeException("switch needs an even amount of arguments");

            String val = args.get(0).toString();
            for (int i = 1; i < size - 1; i += 2) {
                String test = args.get(i).toString();
                if (equal(val, test))
                    return args.get(i + 1);
            }
            // return the default value
            return args.get(size - 1);
        }

        private boolean equal(String start, String arg) {
            String[] parts = arg.split(",");
            for (String part : parts) {
                if (part.trim().equalsIgnoreCase(start))
                    return true;
            }
            return false;
        }
    }

    class Equation implements Formattable {

        @Override
        public Message format(@Nonnull List<Message> args) {
            if (args.size() != 1)
                throw new RuntimeException("Equation must have exactly one argument");
            try {
                double answer = new ExpressionBuilder(args.get(0).toString())
                        .function(Signum.instance)
                        .build().evaluate();
                return Message.of(BigDecimal.valueOf(answer).stripTrailingZeros().toPlainString());
            } catch (Exception e) {
                throw new RuntimeException("Expression has invalid numerical inputs: " + args.get(0), e);
            }
        }
    }

    class Signum extends Function {

        public static Signum instance = new Signum();

        public Signum() {
            super("sign", 1);
        }

        @Override
        public double apply(double... args) {
            if (args.length != 1)
                throw new IllegalArgumentException("Sign function can only have one argument");
            double val = args[0];
            if (val > 0)
                return 1;
            else if (val < 0)
                return -1;
            else if (val == 0)
                return 0;
            else
                return Double.NaN;
        }
    }

}
