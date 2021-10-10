package io.github.mdsimmo.bomberman.messaging

import io.github.mdsimmo.bomberman.messaging.Message.Companion.of
import org.bukkit.ChatColor
import org.junit.Assert.*
import org.junit.Test

class MessageTest {
    @Test
    fun ofString() {
        val msg = of("Hia")
        assertEquals("Hia", msg.toString())
    }

    @Test
    fun ofInt() {
        val msg = of(123)
        assertEquals("123", msg.toString())
    }

    @Test
    fun empty() {
        val msg = Message.empty
        assertEquals("", msg.toString())
    }

    @Test
    fun color() {
        val blue = of("I am blue").color(ChatColor.BLUE)
        assertEquals(ChatColor.BLUE.toString() + "I am blue" + ChatColor.RESET.toString(), blue.toString())
    }

    @Test
    fun append() {
        val a = of("Part a")
        val b = of("Part b")
        val c = a.append(b)
        assertEquals("Part a", a.toString())
        assertEquals("Part b", b.toString())
        assertEquals("Part aPart b", c.toString())
    }

    @Test
    fun formatReturnsItself() {
        val hello = of("I am fancy")
        val formatted = hello.format(listOf(),)
        assertEquals(hello, formatted)
    }
}