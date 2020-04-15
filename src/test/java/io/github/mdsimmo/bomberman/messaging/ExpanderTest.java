package io.github.mdsimmo.bomberman.messaging;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.bukkit.ChatColor;
import org.junit.Test;

public class ExpanderTest {

    @Test
    public void printsBasicString() {
        assertEquals("Hello", Expander.expand("Hello", Map.of()).toString());
    }

    @Test
    public void singleColorIsAdded() {
        Message message = Expander.expand("hi {red|boo}", Map.of());
        assertEquals("hi " + ChatColor.RED + "boo" + ChatColor.RESET, message.toString());
    }

    @Test
    public void embeddedColorsHandled() {
        Message message = Expander.expand("{green|hello, {yellow|fred}.} You Stink", Map.of());
        assertEquals(ChatColor.GREEN + "hello, " + ChatColor.YELLOW + "fred" + ChatColor.GREEN + "."
                + ChatColor.RESET + " You Stink", message.toString());
    }

    @Test
    public void argumentArePassed() {
        Message message = Expander.expand("One, {two}, three", Map.of(
                "two", Message.of("hello")
        ));
        assertEquals("One, hello, three", message.toString());
    }

    @Test
    public void formatablesGetFormatted() {
        Message message = Expander.expand("Steve likes {steve|girlfriend}",
                Map.of("steve", args -> {
                    if (args.size() != 1 || !args.get(0).toString().equals("girlfriend"))
                        return Message.of(args.toString()); // for debug
                    return Message.of("Alex");
                }));
        assertEquals("Steve likes Alex", message.toString());
    }

    @Test
    public void messagesEmbeddedInMessages() {
        Message A = Expander.expand("This is A.", Map.of());
        Message B = Expander.expand("{a} This is B.", Map.of("a", A));
        Message C = Expander.expand("{b} This is C.", Map.of("b", B));

        assertEquals("This is A. This is B. This is C.", C.toString());
    }

    @Test
    public void colorsPassedThroughMessages() {
        Message A = Expander.expand("A{red|B}C", Map.of());
        Message B = Expander.expand("{Green|X{a}Y}", Map.of("a", A));

        assertEquals(ChatColor.GREEN + "XA" + ChatColor.RED + "B" + ChatColor.GREEN + "CY"
                + ChatColor.RESET, B.toString());
    }

    @Test
    public void noCachedDataRemains() {
        Message A = Expander.expand("A{red|B}C", Map.of());
        Message B = Expander.expand("{Green|X{a}Y}", Map.of("a", A));

        // run the test a few times with the same objects. If it fails, the cache is broken
        for (int i = 0; i < 5; i++)
            assertEquals(ChatColor.GREEN + "XA" + ChatColor.RED + "B" + ChatColor.GREEN + "CY"
                    + ChatColor.RESET, B.toString());
    }

    @Test
    public void singleMessageThatIsSuperComplex() {
        Message message = Expander.expand("Hey, {bold|Mr. {Red|Red {yellow|Bob} the {yellow|Tob} BANG} BOOM} CRASH",
                Map.of());

        assertEquals(
                "Hey, " + ChatColor.BOLD + "Mr. " + ChatColor.RED + ChatColor.BOLD + "Red " + ChatColor.YELLOW
                        + ChatColor.BOLD + "Bob" + ChatColor.RED + ChatColor.BOLD + " the " + ChatColor.YELLOW
                        + ChatColor.BOLD + "Tob" + ChatColor.RED + ChatColor.BOLD + " BANG"
                        + ChatColor.RESET + ChatColor.BOLD + " BOOM" + ChatColor.RESET + " CRASH",
                message.toString());
    }

    @Test
    public void multipleFormatsGetApplied() {
        Message message = Expander.expand("Hello, {bold|{italic|{green|World}}}!", Map.of());

        // TODO lazy apply color formats
        // note that the bold/italic is applied twice here because the green resets it
        assertEquals(
                "Hello, " + ChatColor.BOLD + ChatColor.ITALIC + ChatColor.GREEN + ChatColor.BOLD
                        + ChatColor.ITALIC + "World" + ChatColor.RESET + ChatColor.BOLD + ChatColor.ITALIC
                        + ChatColor.RESET + ChatColor.BOLD + ChatColor.RESET + "!",
                // Yup, it's ugly, but that's how MC formatting codes work :P
                message.toString());
    }

    @Test
    public void resetDoesResetThings() {
        Message message = Expander.expand("{Green|{Bold|Hi {reset|world}!}}", Map.of());
        assertEquals(ChatColor.GREEN + "" + ChatColor.BOLD + "Hi " + ChatColor.RESET + "world"
                + ChatColor.GREEN + ChatColor.BOLD + "!" + ChatColor.GREEN + ChatColor.RESET, message.toString());
    }

    @Test
    public void formatCodesThroughMessages() {
        Message a = Expander.expand("{Green|{bold|A}}", Map.of());
        Message b = Expander.expand("{Italic|{Blue|{a}}}", Map.of("a", a));
        assertEquals(ChatColor.ITALIC + "" + ChatColor.BLUE + ChatColor.ITALIC + ChatColor.GREEN
                        + ChatColor.ITALIC + ChatColor.BOLD + "A" + ChatColor.GREEN + ChatColor.ITALIC
                        + ChatColor.BLUE + ChatColor.ITALIC + ChatColor.RESET + ChatColor.ITALIC + ChatColor.RESET,
                b.toString());
    }

    @Test
    public void backslashEscapes() {
        Message a = Expander.expand("{Red|\\{Green\\|Hello\\{\\}}", Map.of());
        assertEquals(ChatColor.RED+"{Green|Hello{}"+ChatColor.RESET, a.toString());
    }

    @Test
    public void testExpandingMissingValueGivesRedHighlight() {
        Message a = Expander.expand("Hello {friend|verb} friend", Map.of());
        assertEquals("Hello "+ ChatColor.RED + "{friend|verb}" + ChatColor.RESET + " friend", a.toString());
    }

    // TODO formatting error highlights in correct spot (no crash)
}
