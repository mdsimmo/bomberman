package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.messaging.Message
import org.bukkit.command.CommandSender
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class CommandGroupTest {

    fun <T> any(): T = Mockito.any()
    fun <T> eq(obj: T): T = Mockito.eq(obj)

    @Test
    fun testCommandGroupFindsCorrectChildAndGivesCorrectArguments() {
        val group = object : CommandGroup(null) {
            override fun name(): Message = Message.of("upper")

            override fun description(): Message = Message.of("desc")

            override fun permission(): Permission = Permissions.NEWGAME
        }

        val child = mock(Cmd::class.java, "child")
        group.addChildren(child)
        `when`(child.name()).thenReturn(Message.of("Child"))
        `when`(child.permission()).thenReturn(Permissions.NEWGAME)
        `when`(child.run(any(), eq(listOf("hello", "world")), eq(emptyMap()))).thenReturn(true)

        val child2 = mock(Cmd::class.java, "child2")
        group.addChildren(child2)
        `when`(child2.name()).thenReturn(Message.of("child2"))
        `when`(child2.permission()).thenReturn(Permissions.NEWGAME)

        val sender = mock(CommandSender::class.java, "sender")
        `when`(sender.hasPermission(anyString())).thenReturn(true)

        group.execute(sender, listOf("chIlD", "hello", "world"), emptyMap())

        verify(child).run(sender, listOf("hello", "world"), emptyMap())

        // child2 should not be touched
        verify(child2, atMostOnce()).name()
        verifyNoMoreInteractions(child2)

        // Nothing should be sent to player
        verify(sender, never()).sendMessage(anyString())
    }

    @Test
    fun testCommandGroupGetsOptions() {
        val group = object : CommandGroup(null) {
            override fun name(): Message = Message.of("upper")

            override fun description(): Message = Message.of("desc")

            override fun permission(): Permission = Permissions.NEWGAME
        }

        val sender = mock(CommandSender::class.java, "sender")
        `when`(sender.hasPermission(anyString())).thenReturn(true)
        val permission = mock(Permission::class.java)
        `when`(permission.isAllowedBy(any())).thenReturn(true)

        val child = mock(Cmd::class.java, "child")
        group.addChildren(child)
        `when`(child.name()).thenReturn(Message.of("child"))
        `when`(child.permission()).thenReturn(permission)
        `when`(child.options(sender, listOf("hello", "world"))).thenReturn(listOf("all", "good"))

        val child2 = mock(Cmd::class.java, "child2")
        group.addChildren(child2)
        `when`(child2.name()).thenReturn(Message.of("child2"))
        `when`(child2.permission()).thenReturn(permission)

        val result = group.options(sender, listOf("chIlD", "hello", "world"))

        assertEquals(listOf("all", "good"), result)

        verify(child).name()
        verify(child).permission()
        verify(child).options(sender, listOf("hello", "world"))
        verifyNoMoreInteractions(child)

        // child2 should not be touched
        verify(child2, atMostOnce()).name()
        verify(child2, atMostOnce()).permission()
        verifyNoMoreInteractions(child2)

        // Nothing should be sent to player
        verify(sender, never()).sendMessage(anyString())
    }
}