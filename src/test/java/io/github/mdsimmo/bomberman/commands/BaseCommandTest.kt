package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.messaging.Message
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class BaseCommandTest {
    private fun <T> any(): T = Mockito.any()
    private fun <T> eq(obj: T): T = Mockito.eq(obj)

    @Test
    fun testBaseCommandFindsCorrectChildAndGivesCorrectArguments() {
        val group = BaseCommand(false)

        val child = mock(Cmd::class.java, "child")
        group.addChildren(child)
        `when`(child.name()).thenReturn(Message.of("Child"))
        `when`(child.permission()).thenReturn(Permissions.CREATE)
        `when`(child.run(any(), eq(listOf("hello", "world")), eq(emptyMap()))).thenReturn(true)

        val child2 = mock(Cmd::class.java, "child2")
        group.addChildren(child2)
        `when`(child2.name()).thenReturn(Message.of("child2"))
        `when`(child2.permission()).thenReturn(Permissions.CREATE)

        val sender = mock(CommandSender::class.java, "sender")
        `when`(sender.hasPermission(anyString())).thenReturn(true)

        group.run(sender, listOf("chIlD", "hello", "world"), emptyMap())

        verify(child).run(sender, listOf("hello", "world"), emptyMap())

        // child2 should not be touched
        verify(child2, atMostOnce()).name()
        verifyNoMoreInteractions(child2)

        // Nothing should be sent to player
        verify(sender, never()).sendMessage(anyString())
    }

    @Test
    fun testBaseCommandTabCompletesCommands() {
        val group = BaseCommand(false)

        val sender = mock(CommandSender::class.java, "sender")
        `when`(sender.hasPermission(anyString())).thenReturn(true)
        val permission = mock(Permission::class.java)
        `when`(permission.isAllowedBy(any())).thenReturn(true)

        val child = mock(Cmd::class.java, "child")
        group.addChildren(child)
        `when`(child.name()).thenReturn(Message.of("child"))
        `when`(child.permission()).thenReturn(permission)
        `when`(child.options(sender, listOf("hello", "world", ""))).thenReturn(listOf("all", "good"))

        val child2 = mock(Cmd::class.java, "child2")
        group.addChildren(child2)
        `when`(child2.name()).thenReturn(Message.of("child2"))
        `when`(child2.permission()).thenReturn(permission)

        val result = group.onTabComplete(sender, mock(Command::class.java), "", arrayOf("chIlD", "hello", "world", ""))

        verify(child).name()
        verify(child).permission()
        verify(child).options(sender, listOf("hello", "world", ""))
        verifyNoMoreInteractions(child)

        // child2 should not be touched
        verify(child2, atMostOnce()).name()
        verify(child2, atMostOnce()).permission()
        verifyNoMoreInteractions(child2)

        assertEquals(listOf("all", "good"), result)

        // Nothing should be sent to player
        verify(sender, never()).sendMessage(anyString())
    }

    @Test
    fun testBaseCommandTabCompletesFlagOptions() {
        val sender = mock(CommandSender::class.java, "sender")
        `when`(sender.hasPermission(anyString())).thenReturn(true)
        val permission = mock(Permission::class.java)
        `when`(permission.isAllowedBy(any())).thenReturn(true)

        val subCommand = mock(Cmd::class.java)
        `when`(subCommand.name()).thenReturn(Message.of("dummy"))
        `when`(subCommand.permission()).thenReturn(permission)
        `when`(subCommand.flags(any())).thenReturn(setOf("a", "b"))
        `when`(subCommand.flagExtension("a")).thenReturn(Message.of("<something>"))
        `when`(subCommand.flagExtension("b")).thenReturn(Message.of(""))

        val base = BaseCommand(false)
        base.addChildren(subCommand)
        val result = base.onTabComplete(sender, mock(Command::class.java), "", arrayOf("dummy", "-"))

        assertArrayEquals(arrayOf( "-?", "-a", "-b"), result.toSortedSet().toTypedArray())
    }

}