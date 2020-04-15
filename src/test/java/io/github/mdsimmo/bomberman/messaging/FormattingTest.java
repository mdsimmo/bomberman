package io.github.mdsimmo.bomberman.messaging;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class FormattingTest {

    @Test
    public void testAllEnglishStatementsExist() {
        // Load one text message will cause initialisers of all Texts to verify they exist in english.yml
        Assert.assertNotNull(Text.TRUE.format());
    }

    @Test
    public void testExpandMap() {
        Message a = Expander.expand("A simple map \n{map|a|1|b|2}", Map.of());
        assertEquals("A simple map \n a: 1\n b: 2\n", ChatColor.stripColor(a.toString()));
    }

    @Test
    public void textExpandHeading() {
        Message a = Expander.expand("{heading|hello}", Map.of());
        assertEquals("-------- hello ---------------", ChatColor.stripColor(a.toString()));
    }

    @Test
    public void textExpandList() {
        Message a = Expander.expand("{list|1|2|3|4}", Map.of());
        assertEquals(" * 1\n * 2\n * 3\n * 4\n", ChatColor.stripColor(a.toString()));
    }

    @Test
    public void wrapperMessageAppendedOnSendTo() {
        CommandSender sender = mock(CommandSender.class);

        Message message = Message.of("Hello World").color(ChatColor.AQUA);
        message.sendTo(sender);

        verify(sender).sendMessage(ChatColor.GREEN + "[Bomberman]" + ChatColor.RESET + " " + ChatColor.AQUA + "Hello World" + ChatColor.RESET);
    }

    @Test
    public void testNoWrapperMessageAppendedWithRaw() {
        CommandSender sender = mock(CommandSender.class);

        Message message = Message.of("Hello World").color(ChatColor.AQUA).append(Message.rawFlag());
        message.sendTo(sender);

        verify(sender).sendMessage(ChatColor.AQUA + "Hello World" + ChatColor.RESET);
    }

    @Test
    public void testEmptyMessageDoesNotGetSent() {
        CommandSender sender = mock(CommandSender.class);

        Message message = Message.of("");
        message.sendTo(sender);

        verify(sender, never()).sendMessage(anyString());
    }

    @Test
    public void testTitleGetsSent() {
        Player sender = mock(Player.class);
        Message message = Expander.expand("{title|Chapter {chapter}|The story ends|1|2|3}",
                Map.of("chapter", Message.of(1)));

        message.sendTo(sender);

        verify(sender).sendTitle("Chapter 1", "The story ends", 1, 2, 3);
    }

    @Test
    public void testEquationsExpand() {
        Message a = Expander.expand("Equation {=|2+3}", Map.of());
        assertEquals("Equation 5", a.toString());
    }

    @Test
    public void testSignFunction() {
        Message a = Expander.expand("{=|sign(5)} {=|sign(0)} {=|sign(-2.3)}", Map.of());
        assertEquals("1 0 -1", a.toString());
    }

    @Test
    public void testSwitchExpands() {
        Message a = Expander.expand("{switch|a|a|1|b|2|3} {switch|b|a|1|b|2|3} {switch|c|a|1|b|2|3}", Map.of());
        assertEquals("1 2 3", a.toString());
    }

    @Test
    public void testListForeach() {
        List<Message> mylist = List.of(
                Message.of("Hello"),
                Message.of("Small"),
                Message.of("World"));
        Message a = Expander.expand("{mylist|foreach|\\{i\\}:\\{value\\}|-}",
                Map.of("mylist", new Formattable.CollectionWrapper<>(mylist)));
        assertEquals("0:Hello-1:Small-2:World", a.toString());
    }

    @Test
    public void testListSize() {
        List<Message> mylist = List.of(
                Message.of("Hello"),
                Message.of("Small"),
                Message.of("World"));
        Message a = Expander.expand("{mylist|length}",
                Map.of("mylist", new Formattable.CollectionWrapper<>(mylist)));
        assertEquals("3", a.toString());
    }

}
