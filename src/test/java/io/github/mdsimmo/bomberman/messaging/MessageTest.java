package io.github.mdsimmo.bomberman.messaging;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MessageTest {

    @Test
    public void ofString() {
        Message msg = Message.of("Hia");
        assertEquals("Hia", msg.toString());
    }

    @Test
    public void ofInt() {
        Message msg = Message.of(123);
        assertEquals("123", msg.toString());
    }

    @Test
    public void empty() {
        Message msg = Message.empty();
        assertEquals("", msg.toString());
    }

    @Test
    public void color() {
        Message blue = Message.of("I am blue").color(ChatColor.BLUE);
        assertEquals(ChatColor.BLUE.toString() + "I am blue" + ChatColor.RESET.toString(), blue.toString());
    }

    @Test
    public void append() {
        Message a = Message.of("Part a");
        Message b = Message.of("Part b");
        Message c = a.append(b);

        assertEquals("Part a", a.toString());
        assertEquals("Part b", b.toString());
        assertEquals("Part aPart b", c.toString());
    }

    @Test
    public void formatReturnsItself() {
        Message hello = Message.of("I am fancy");
        var formatted = hello.format(List.of());
        assertEquals(hello, formatted);
    }
}