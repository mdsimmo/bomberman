package io.github.mdsimmo.bomberman.messaging

import io.github.mdsimmo.bomberman.messaging.Expander.expand
import org.bukkit.ChatColor
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class ExpanderTest {
    private fun <T> any(): T = Mockito.any()

    @Test
    fun printsBasicString() {
        assertEquals(
                "Hello",
                expand("Hello", Context()).toString()
        )
    }

    @Test
    fun singleColorIsAdded() {
        val message = expand("hi {#red|boo}", Context())
        assertEquals(
                "hi " + ChatColor.RED + "boo" + ChatColor.RESET, 
                message.toString()
        )
    }

    @Test
    fun embeddedColorsHandled() {
        val message = expand("{#green|hello, {#yellow|fred}.} You Stink", Context())
        assertEquals(
                ChatColor.GREEN.toString() + "hello, " + ChatColor.YELLOW + "fred" + ChatColor.GREEN + "." + ChatColor.RESET + " You Stink", 
                message.toString()
        )
    }

    @Test
    fun argumentArePassed() {
        val message = expand("One, {two}, three", Context().plus("two", Message.of("hello")))
        assertEquals(
                "One, hello, three",
                message.toString()
        )
    }

    @Test
    fun formattablesGetFormatted() {
        
        val alex = Message.of("Alex")

        val steve = object : Formattable {
            override fun applyModifier(arg: Message): Formattable {
                return if (arg.toString() == "girlfriend")
                    alex
                else
                    Message.of("Bad: $arg")
            }

            override fun format(context: Context): Message {
                return Message.of("Second arg required")
            }
        }
        
        val message = expand("Steve likes {steve|girlfriend}", Context().plus("steve", steve))
        assertEquals("Steve likes Alex", message.toString())
    }

    @Test
    fun messagesEmbeddedInMessages() {
        val a = expand("This is A.", Context())
        val b = expand("{a} This is B.", Context().plus("a", a))
        val c = expand("{b} This is C.", Context().plus("b", b))
        assertEquals("This is A. This is B. This is C.", c.toString())
    }

    @Test
    fun colorsPassedThroughMessages() {
        val a = expand("A{#red|B}C", Context())
        val b = expand("{#Green|X{a}Y}", Context().plus("a", a))
        assertEquals(
                ChatColor.GREEN.toString() + "XA" + ChatColor.RED + "B" + ChatColor.GREEN + "CY" + ChatColor.RESET,
                b.toString()
        )
    }

    @Test
    fun noCachedDataRemains() {
        val a = expand("A{#red|B}C", Context())
        val b = expand("{#Green|X{a}Y}", Context().plus("a", a))
        // run the test a few times with the same objects. If it fails, the cache is broken
        for (i in 0..4)
            assertEquals(
                    ChatColor.GREEN.toString() + "XA" + ChatColor.RED + "B" + ChatColor.GREEN + "CY" + ChatColor.RESET,
                    b.toString()
            )
    }

    @Test
    fun singleMessageWithNestedColours() {
        val message = expand(
                "Hey, {#bold|Mr. {#Red|Red {#yellow|Bob} the {#yellow|Tob} BANG} BOOM} CRASH",
            Context()
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
        val message = expand("Hello, {#bold|{#italic|{#green|World}}}!", Context())
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
        val message = expand("{#Green|{#Bold|Hi {#reset|world}!}}", Context())
        assertEquals(
                ChatColor.GREEN.toString() + "" + ChatColor.BOLD + "Hi " + ChatColor.RESET
                        + "world" + ChatColor.GREEN + ChatColor.BOLD + "!" + ChatColor.GREEN
                        + ChatColor.RESET,
                message.toString())
    }

    @Test
    fun formatCodesThroughMessages() {
        val a = expand("{#Green|{#bold|A}}", Context())
        val b = expand("{#Italic|{#Blue|{a}}}", Context().plus("a", a))
        assertEquals(ChatColor.ITALIC.toString() + "" + ChatColor.BLUE + ChatColor.ITALIC
                + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + "A" + ChatColor.GREEN
                + ChatColor.ITALIC + ChatColor.BLUE + ChatColor.ITALIC + ChatColor.RESET
                + ChatColor.ITALIC + ChatColor.RESET,
                b.toString())
    }

    @Test
    fun backslashEscapes() {
        val a = expand("{#Red|\\{#Green\\|Hello\\{\\}}", Context())
        assertEquals(
                ChatColor.RED.toString() + "{#Green|Hello{}" + ChatColor.RESET,
                a.toString()
        )
    }

    @Test
    fun testExpandingMissingValueGivesRedHighlight() {
        val a = expand("Hello {friend|verb} friend", Context())
        assertEquals(
                "Hello " + ChatColor.RED + "{friend|verb}" + ChatColor.RESET + " friend",
                a.toString()
        )
    }

    // TODO formatting error highlights in correct spot (no crash)


    @Test
    fun testMessagesLazyExpanded() {
        val badExpand = mock(Formattable::class.java)
        `when`(badExpand.format(any())).thenReturn(Message.of("no no"))

        val a = expand("{#switch|0|0|choose me|{bad}}", Context().plus("bad", badExpand))

        assertEquals("choose me", a.toString())
        verifyNoInteractions(badExpand)
    }

    @Test
    fun testCustomFormat() {
        val context = Context().addFunctions { key ->
            when (key) {
                "custom.path" -> "{arg0}: {arg1}"
                else -> null
            }
        }
        val a = expand("A map: \n{#|custom.path|key|value}\nWow!", context)
        assertEquals("A map: \nkey: value\nWow!", a.toString())
    }

    @Test
    fun testObjectsArePassedToFunctions() {
        val context = Context()
            .plus("obj", PartialRequired { arg ->
                when (arg.toString()) {
                    "value" -> Message.of("Good")
                    else -> arg
                }
            })
            .addFunctions { key ->
                when (key) {
                    "custom.path" -> "{arg0|value}"
                    else -> null
                }
            }

        val a = expand("{#|custom.path|{@obj}}", context)

        assertEquals("Good", a.toString())
    }

    @Test
    fun testContextObjectsAreKeptInScope() {
        val context = Context()
            .plus("list", listOf(Message.of("A"), Message.of("B"), Message.of("C")))
            .plus("obj", object : Formattable {
                override fun applyModifier(arg: Message): Formattable {
                    return Message.of(
                        when (arg.toString()) {
                            "A" -> "1"
                            "B" -> "2"
                            "C" -> "3"
                            else -> throw IllegalArgumentException("Unknown $arg")
                        }
                    )
                }

                override fun format(context: Context): Message {
                    throw IllegalArgumentException("Second argument required")
                }
            })
        val a = expand("{list|foreach|{!obj|{it}}|}", context)
        assertEquals("123", a.toString())
    }

    @Test
    fun testExclaimDoesNotEvaluate() {
        val text = "Hello {!bob}"
        val result = expand(text, Context())
        assertEquals("Hello {bob}", result.toString())
    }

    @Test
    fun testMultipleExclaimEvaluations() {
        val text = "{!Bob is|{!The best}}"
        val result = expand(text, Context())
        assertEquals("{Bob is|{!The best}}", result.toString())
    }

    @Test
    fun testFormatTwiceDoesNotDualExpand() {
        val text = "Hello {!Bob}"
        val result = expand(text, Context()).format(Context())
        assertEquals("Hello {Bob}", result.toString())
    }
}