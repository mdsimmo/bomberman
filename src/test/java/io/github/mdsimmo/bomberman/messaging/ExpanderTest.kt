package io.github.mdsimmo.bomberman.messaging

import io.github.mdsimmo.bomberman.messaging.Expander.expand
import org.bukkit.ChatColor
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*

class ExpanderTest {
    @Test
    fun printsBasicString() {
        assertEquals(
                "Hello",
                expand("Hello", mapOf(), false).toString()
        )
    }

    @Test
    fun singleColorIsAdded() {
        val message = expand("hi {#red|boo}", mapOf(), false)
        assertEquals(
                "hi " + ChatColor.RED + "boo" + ChatColor.RESET, 
                message.toString()
        )
    }

    @Test
    fun embeddedColorsHandled() {
        val message = expand("{#green|hello, {#yellow|fred}.} You Stink", mapOf(), false)
        assertEquals(
                ChatColor.GREEN.toString() + "hello, " + ChatColor.YELLOW + "fred" + ChatColor.GREEN + "." + ChatColor.RESET + " You Stink", 
                message.toString()
        )
    }

    @Test
    fun argumentArePassed() {
        val message = expand("One, {two}, three", mapOf(Pair("two", Message.of("hello"))), false)
        assertEquals(
                "One, hello, three",
                message.toString()
        )
    }

    @Test
    fun formatablesGetFormatted() {
        val message =
                expand("Steve likes {steve|girlfriend}", mapOf(
                        Pair("steve", object : Formattable {
                            override fun format(args: List<Message>, elevated: Boolean): Message {
                                return if (args.size != 1 || args[0].toString() != "girlfriend")
                                    Message.of(args.toString()) // for debug
                                else
                                    Message.of("Alex")
                            }
                        })
                ), false)
        assertEquals("Steve likes Alex", message.toString())
    }

    @Test
    fun messagesEmbeddedInMessages() {
        val a = expand("This is A.", mapOf(), false)
        val b = expand("{a} This is B.", mapOf(Pair("a", a)), false)
        val c = expand("{b} This is C.", mapOf(Pair("b", b)), false)
        assertEquals("This is A. This is B. This is C.", c.toString())
    }

    @Test
    fun colorsPassedThroughMessages() {
        val a = expand("A{#red|B}C", mapOf(), false)
        val b = expand("{#Green|X{a}Y}", mapOf(Pair("a", a)), false)
        assertEquals(
                ChatColor.GREEN.toString() + "XA" + ChatColor.RED + "B" + ChatColor.GREEN + "CY" + ChatColor.RESET,
                b.toString()
        )
    }

    @Test
    fun noCachedDataRemains() {
        val a = expand("A{#red|B}C", mapOf(), false)
        val b = expand("{#Green|X{a}Y}", mapOf(Pair("a", a)), false)
        // run the test a few times with the same objects. If it fails, the cache is broken
        for (i in 0..4)
            assertEquals(
                    ChatColor.GREEN.toString() + "XA" + ChatColor.RED + "B" + ChatColor.GREEN + "CY" + ChatColor.RESET,
                    b.toString()
            )
    }

    @Test
    fun singleMessageThatIsSuperComplex() {
        val message = expand(
                "Hey, {#bold|Mr. {#Red|Red {#yellow|Bob} the {#yellow|Tob} BANG} BOOM} CRASH",
                mapOf(), false
        )
        assertEquals(
                "Hey, " + ChatColor.BOLD + "Mr. " + ChatColor.RED + ChatColor.BOLD +
                        "Red " + ChatColor.YELLOW + ChatColor.BOLD + "Bob" + ChatColor.RED + ChatColor.BOLD +
                        " the " + ChatColor.YELLOW + ChatColor.BOLD + "Tob" + ChatColor.RED + ChatColor.BOLD +
                        " BANG" + ChatColor.RESET + ChatColor.BOLD + " BOOM" + ChatColor.RESET + " CRASH"
                , message.toString())
    }

    @Test
    fun multipleFormatsGetApplied() {
        val message = expand("Hello, {#bold|{#italic|{#green|World}}}!", mapOf(), false)
        // TODO lazy apply color formats
        // note that the bold/italic is applied twice here because the green resets it
        assertEquals("Hello, " + ChatColor.BOLD + ChatColor.ITALIC + ChatColor.GREEN
                + ChatColor.BOLD + ChatColor.ITALIC + "World" + ChatColor.RESET + ChatColor.BOLD
                + ChatColor.ITALIC + ChatColor.RESET + ChatColor.BOLD + ChatColor.RESET + "!",
                // Yup, it's ugly, but that's how MC formatting codes work :P
                message.toString())
    }

    @Test
    fun resetDoesResetThings() {
        val message = expand("{#Green|{#Bold|Hi {#reset|world}!}}", mapOf(), false)
        assertEquals(
                ChatColor.GREEN.toString() + "" + ChatColor.BOLD + "Hi " + ChatColor.RESET
                        + "world" + ChatColor.GREEN + ChatColor.BOLD + "!" + ChatColor.GREEN
                        + ChatColor.RESET,
                message.toString())
    }

    @Test
    fun formatCodesThroughMessages() {
        val a = expand("{#Green|{#bold|A}}", mapOf(), false)
        val b = expand("{#Italic|{#Blue|{a}}}", mapOf(Pair("a", a)), false)
        assertEquals(ChatColor.ITALIC.toString() + "" + ChatColor.BLUE + ChatColor.ITALIC
                + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + "A" + ChatColor.GREEN
                + ChatColor.ITALIC + ChatColor.BLUE + ChatColor.ITALIC + ChatColor.RESET
                + ChatColor.ITALIC + ChatColor.RESET,
                b.toString())
    }

    @Test
    fun backslashEscapes() {
        val a = expand("{#Red|\\{#Green\\|Hello\\{\\}}", mapOf(), false)
        assertEquals(
                ChatColor.RED.toString() + "{#Green|Hello{}" + ChatColor.RESET,
                a.toString()
        )
    }

    @Test
    fun testExpandingMissingValueGivesRedHighlight() {
        val a = expand("Hello {friend|verb} friend", mapOf(), false)
        assertEquals(
                "Hello " + ChatColor.RED + "{friend|verb}" + ChatColor.RESET + " friend",
                a.toString()
        )
    }

    // TODO formatting error highlights in correct spot (no crash)


    @Test
    fun testMessagesLazyExpanded() {
        val badExpand = mock(Formattable::class.java)
        `when`(badExpand.format(anyList(), anyBoolean())).thenReturn(Message.of("no no"))

        val a = expand("{#switch|0|0|choose me|{bad}}", mapOf(Pair("bad", badExpand)), false)

        assertEquals("choose me", a.toString())
        verifyNoInteractions(badExpand)
    }
}