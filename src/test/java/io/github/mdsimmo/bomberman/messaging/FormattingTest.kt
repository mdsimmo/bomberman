package io.github.mdsimmo.bomberman.messaging

import io.github.mdsimmo.bomberman.messaging.Expander.expand
import io.github.mdsimmo.bomberman.messaging.Message.Companion.of
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

class FormattingTest {
    @Test
    fun testAllEnglishStatementsExistAndHaveNoErrors() {
        for (t in Text.values()) {
            // We should be able to evaluate every message without throwing an exception
            // Missing arguments should just be formatted red
            assertNotNull(t.format())
        }
    }

    @Test
    fun testCustomFormat() {
        val a = expand("A map: \n{c|format.map|key|value}\nWow!", mapOf())
        assertEquals("A map: \n key: value\nWow!", ChatColor.stripColor(a.toString()))
    }

    @Test
    fun wrapperMessageAppendedOnSendTo() {
        val sender = mock(CommandSender::class.java)
        val message = of("Hello World").color(ChatColor.AQUA)

        message.sendTo(sender)

        verify(sender).sendMessage(ChatColor.GREEN.toString() + "[Bomberman]" + ChatColor.RESET
                + " " + ChatColor.AQUA + "Hello World" + ChatColor.RESET)
    }

    @Test
    fun testNoWrapperMessageAppendedWithRaw() {
        val sender = mock(CommandSender::class.java)
        val message = of("Hello World").color(ChatColor.AQUA).append(Message.rawFlag)

        message.sendTo(sender)

        verify(sender).sendMessage(ChatColor.AQUA.toString() + "Hello World" + ChatColor.RESET)
    }

    @Test
    fun testEmptyMessageDoesNotGetSent() {
        val sender = mock(CommandSender::class.java)
        val message = of("")

        message.sendTo(sender)

        verify(sender, never()).sendMessage(ArgumentMatchers.anyString())
    }

    @Test
    fun testTitleGetsSent() {
        val sender = mock(Player::class.java)
        val message = expand(
                "{title|Chapter {chapter}|The story ends|1|2|3}",
                mapOf(Pair("chapter", of(1)))
        )

        message.sendTo(sender)

        verify(sender).sendTitle("Chapter 1", "The story ends", 1, 2, 3)
    }

    @Test
    fun testEquationsExpand() {
        val a = expand("Equation {=|2+3}", mapOf())
        assertEquals("Equation 5", a.toString())
    }

    @Test
    fun testSwitchExpands() {
        val a = expand("{switch|a|a|1|b|2|3} {switch|b|a|1|b|2|3} {switch|c|a|1|b|2|3}", mapOf())
        assertEquals("1 2 3", a.toString())
    }

    @Test
    fun testSwitchNoMatchReturnsEmpty() {
        val a = expand("{switch|c|a|1|b|2}", mapOf())
        assertEquals("", a.toString())
    }

    @Test
    fun testSwitchEvaluatesOnlyTheSelectedArgument() {
        val no = mock(Formattable::class.java)
        val yes = mock(Formattable::class.java)
        `when`(no.format(anyList())).thenReturn(of("No"))
        `when`(yes.format(anyList())).thenReturn(of("Yes"))

        val a = expand("{switch|1|0|{no}|1|{yes}|2|{no}|{no}}", mapOf(
                Pair("no", no),
                Pair("yes", yes)
        ))

        assertEquals("Yes", a.toString())
        verify(yes).format(emptyList())
        verifyNoMoreInteractions(yes)
        verifyNoInteractions(no)
    }

    @Test
    fun testSwitchEvaluatesArgmentsInOrder() {
        val one = mock(Formattable::class.java)
        val two = mock(Formattable::class.java)
        val three = mock(Formattable::class.java)
        `when`(one.format(anyList())).thenReturn(of("1"))
        `when`(two.format(anyList())).thenReturn(of("2"))
        `when`(three.format(anyList())).thenReturn(of("3"))

        val a = expand("{switch|2|{one}|One|{two}|Two|{three}|Three|Four+}", mapOf(
                Pair("one", one),
                Pair("two", two),
                Pair("three", three)
        ))

        assertEquals("Two", a.toString())
        verify(one).format(emptyList())
        verifyNoMoreInteractions(one)
        verify(two).format(emptyList())
        verifyNoMoreInteractions(two)
        verifyNoInteractions(three)
    }

    @Test
    fun testListForeach() {
        val mylist = listOf(of("Hello"), of("Small"), of("World"))
        val a = expand(
                "{mylist|foreach|\\{i\\}:\\{value\\}|-}",
                mapOf(Pair("mylist", CollectionWrapper(mylist))))
        assertEquals("0:Hello-1:Small-2:World", a.toString())
    }

    @Test
    fun testListSize() {
        val mylist = listOf(of("Hello"), of("Small"), of("World"))
        val a = expand("{mylist|length}", mapOf(Pair("mylist", CollectionWrapper(mylist))))
        assertEquals("3", a.toString())
    }

    @Test
    fun testStringLength() {
        val a = expand("{len|Hello}", emptyMap())
        assertEquals("5", a.toString())
    }

    @Test
    fun testPadLeft() {
        val a = expand("{padl|abc|8|xyz}", emptyMap())
        assertEquals("xyzxyabc", a.toString())
    }

    @Test
    fun testPadRight() {
        val a = expand("{padr|abc|8|xyz}", emptyMap())
        assertEquals("abcxyzxy", a.toString())
    }

    @Test
    fun testSubStringPosNone() {
        val a = expand("{sub|icecream|3}", emptyMap())
        assertEquals("cream", a.toString())
    }

    @Test
    fun testSubStringPosPos() {
        val a = expand("{sub|icecream|3|2}", emptyMap())
        assertEquals("cr", a.toString())
    }

    @Test
    fun testSubStringPosNeg() {
        val a = expand("{sub|icecream|3|-2}", emptyMap())
        assertEquals("cre", a.toString())
    }

    @Test
    fun testSubStringNegNone() {
        val a = expand("{sub|icecream|-3}", emptyMap())
        assertEquals("eam", a.toString())
    }

    @Test
    fun testSubStringNegPos() {
        val a = expand("{sub|icecream|-3|2}", emptyMap())
        assertEquals("ea", a.toString())
    }

    @Test
    fun testSubStringNegNeg() {
        val a = expand("{sub|icecream|-4|-2}", emptyMap())
        assertEquals("re", a.toString())
    }

    @Test
    fun testRegex() {
        val a = expand("{regex|The little bird says|i(\\\\w+)|a\$1d}", emptyMap())
        assertEquals("The lattled bardd says", a.toString())
    }
}