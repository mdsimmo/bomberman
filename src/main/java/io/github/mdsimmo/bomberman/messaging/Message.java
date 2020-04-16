package io.github.mdsimmo.bomberman.messaging;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.*;

@CheckReturnValue
public final class Message implements Formattable {

    public static Message of(String text) {
        return new Message(new StringNode(text));
    }

    public static Message of(int num) {
        return of(String.valueOf(num));
    }

    private static final Message emptyMessage = Message.of("");
    public static Message empty() {
        return emptyMessage;
    }

    public static Message title(Message text, Message subtitle, int fadein, int duration, int fadeout) {
        return new Message(new TitleNode(text, subtitle, fadein, duration, fadeout));
    }

    private static Message rawMessage = new Message(new RawNode());
    public static Message rawFlag() {
        return rawMessage;
    }

    public static Message error(String s) {
        return Message.of(s).color(ChatColor.RED);
    }

    private static class Style {
        final ChatColor color;
        final Set<ChatColor> formats;

        Style(ChatColor color, Set<ChatColor> formats) {
            this.color = color;
            this.formats = formats;
        }
    }

    private static class Cursor {
        final StringBuilder content = new StringBuilder();
        final Deque<Style> colorStack = new ArrayDeque<>() {{ add(new Style(ChatColor.RESET, Set.of())); }};

        void addStyle(ChatColor color) {
            // Get what the current style is (should always be one on the stack)
            Style currentStyle = colorStack.getLast();

            // Determine what the color change will make the new style be
            Style newStyle;
            if (color.isColor()) {
                newStyle = new Style(color, currentStyle.formats);
            } else if (color.isFormat()) {
                // LinkedHashSet is used here to ensure consistent order for unit tests
                Set<ChatColor> newFormats = new LinkedHashSet<>(currentStyle.formats);
                newFormats.add(color);
                newStyle = new Style(currentStyle.color, newFormats);
            } else /* color == RESET */ {
                newStyle = new Style(ChatColor.RESET, Set.of());
            }

            // Change to the new style
            colorStack.addLast(newStyle);
            appendConversionString(currentStyle, newStyle);
        }

        private void appendConversionString(Style from, Style to) {
            // Minecraft ChatColor rules:
            //  * Adding a color/reset format will remove all previous formats
            //  * Adding a format will keep existing formats and colors

            // LinkedHashSet is used here to ensure consistent order for unit tests
            Set<ChatColor> formatsRemoved = new LinkedHashSet<>(from.formats);
            formatsRemoved.removeAll(to.formats);

            Set<ChatColor> formatsAdded = new LinkedHashSet<>(to.formats);
            formatsAdded.removeAll(from.formats);

            if (from.color != to.color || !formatsRemoved.isEmpty()) {
                // A complete reapplication is needed
                content.append(to.color);
                to.formats.forEach(content::append);
            } else if (!formatsAdded.isEmpty()) {
                // Can just apply the new formats directly
                formatsAdded.forEach(content::append);
            }
        }

        void write(String text) {
            content.append(text);
        }

        void popColor() {
            Style from = colorStack.removeLast();
            Style to = colorStack.getLast();
            appendConversionString(from, to);
        }

        @Override
        public String toString() {
            return content.toString();
        }
    }

    static class Title {
        final String title, subtitle;
        final int fadeIn, stay, fadeOut;

        Title(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            this.title = title;
            this.subtitle = subtitle;
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }
    }

    private interface TreeNode {
        void expand(Cursor cursor);

        // TODO handling of {raw} and {title} is very messy
        boolean isRaw();
        Optional<Title> expandTitle();
    }

    private static class StringNode implements TreeNode {
        final String text;

        private StringNode(String text) {
            this.text = text;
        }

        @Override
        public void expand(Cursor cursor) {
            cursor.write(text);
        }

        @Override
        public boolean isRaw() {
            return false;
        }

        @Override
        public Optional<Title> expandTitle() {
            return Optional.empty();
        }
    }

    private static class Joined implements TreeNode {
        final List<TreeNode> parts;

        private Joined(List<TreeNode> parts) {
            this.parts = new ArrayList<>();
            // Flatten multiple joined nodes
            for (TreeNode part : parts) {
                if (part instanceof Joined) {
                    this.parts.addAll(((Joined) part).parts);
                } else if (part != emptyMessage.contents) {
                    this.parts.add(part);
                }
            }
        }

        private Joined(TreeNode ... parts) {
            this(Arrays.asList(parts));
        }

        @Override
        public void expand(Cursor cursor) {
            for (TreeNode part : parts) {
                part.expand(cursor);
            }
        }

        @Override
        public boolean isRaw() {
            return parts.stream().anyMatch(TreeNode::isRaw);
        }

        @Override
        public Optional<Title> expandTitle() {
            return parts.stream()
                    .map(TreeNode::expandTitle)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findAny();
        }
    }

    private static class Colored implements TreeNode {
        final TreeNode content;
        final ChatColor color;

        private Colored(TreeNode content, ChatColor color) {
            this.content = content;
            this.color = color;
        }

        @Override
        public void expand(Cursor cursor) {
            cursor.addStyle(color);
            content.expand(cursor);
            cursor.popColor();
        }

        @Override
        public boolean isRaw() {
            return content.isRaw();
        }

        @Override
        public Optional<Title> expandTitle() {
            return Optional.empty();
        }
    }

    // TODO Raw/Title formatting is really strange
    private static class RawNode implements TreeNode {
        @Override
        public void expand(Cursor cursor) {
        }

        @Override
        public boolean isRaw() {
            return true;
        }

        @Override
        public Optional<Title> expandTitle() {
            return Optional.empty();
        }
    }

    private static class TitleNode implements TreeNode {
        private final Message title, subtitle;
        private final int fadein, stay, fadeout;

        TitleNode(Message title, Message subtitle, int fadein, int stay, int fadeout) {
            this.title = title;
            this.subtitle = subtitle;
            this.fadein = fadein;
            this.stay = stay;
            this.fadeout = fadeout;
        }

        @Override
        public void expand(Cursor cursor) {
        }

        @Override
        public boolean isRaw() {
            return false;
        }

        @Override
        public Optional<Title> expandTitle() {
            Cursor titleCursor = new Cursor();
            title.contents.expand(titleCursor);
            String titleString = titleCursor.toString();

            Cursor subtitleCursor = new Cursor();
            subtitle.contents.expand(subtitleCursor);
            String subtitleString = subtitleCursor.toString();

            return Optional.of(new Title(titleString, subtitleString, fadein, stay, fadeout));
        }
    }

    private final TreeNode contents;

    private Message(TreeNode contents) {
        this.contents = contents;
    }

    public Message color(ChatColor color) {
        return new Message(new Colored(contents, color));
    }

    public Message append(Message text) {
        return new Message(new Joined(contents, text.contents));
    }

    public Message format(@Nonnull List<Message> args) {
        return this;
    }

    public void sendTo(CommandSender sender) {
        try {
            var sendContents = contents.isRaw() ? contents : Text.MESSAGE_FORMAT
                    .with("message", this)
                    .format().contents;
            Cursor cursor = new Cursor();
            sendContents.expand(cursor);
            if (!cursor.toString().isBlank())
                sender.sendMessage(cursor.toString());

            // Handle possible title
            if (sender instanceof Player) {
                contents.expandTitle().ifPresent(title ->
                        ((Player) sender).sendTitle(title.title, title.subtitle, title.fadeIn, title.stay, title.fadeOut));
            }

        } catch (RuntimeException e) {
            sender.sendMessage(ChatColor.RED + "Message format invalid");
        }
    }

    @Override
    public String toString() {
        Cursor cursor = new Cursor();
        contents.expand(cursor);
        return cursor.toString();
    }
}
