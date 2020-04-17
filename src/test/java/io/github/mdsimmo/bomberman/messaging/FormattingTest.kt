package io.github.mdsimmo.bomberman.messaging

import io.github.mdsimmo.bomberman.messaging.Expander.expand
import io.github.mdsimmo.bomberman.messaging.Message.Companion.of
import io.github.mdsimmo.bomberman.messaging.Message.Companion.rawFlag
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
        val message = of("Hello World").color(ChatColor.AQUA).append(rawFlag())

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
}