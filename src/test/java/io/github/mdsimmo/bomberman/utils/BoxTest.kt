package io.github.mdsimmo.bomberman.utils

import org.bukkit.Location
import org.bukkit.World
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import java.util.stream.Collectors

class BoxTest {
    @Test
    fun testBoxFromExtremes() {
        val world = mock(World::class.java)
        val box = Box(world, Dim(1, 2, 3), Dim(4, 5, 6))
        assertEquals(box.p1, Dim(1, 2, 3))
        assertEquals(box.p2, Dim(4, 5, 6))
    }

    @Test
    fun testBoxFromSize() {
        val world = mock(World::class.java)
        val box = Box(world, 4, 5, 6, 1, 2, 3)
        assertEquals(Dim(4, 5, 6), box.p1)
        assertEquals(Dim(4 + 1 - 1, 5 + 2 - 1, 6 + 3 - 1), box.p2)
    }

    @Test
    fun testBoxSize() {
        val world = mock(World::class.java)
        val box = Box(world, 10, 11, 12, 5, 6, 7)
        assertEquals(Dim(5, 6, 7), box.size)
    }

    @Test
    fun testBoxStream() {
        val world = mock(World::class.java)
        `when`(world.toString()).thenReturn("mockWorld")
        val box = Box(world, 10, 11, 12, 2, 3, 4)
        val actual = box.stream().collect(Collectors.toSet())
        val expected = HashSet<Any>()
        for (x in 10 until 10 + 2) {
            for (y in 11 until 11 + 3) {
                for (z in 12 until 12 + 4) {
                    expected.add(Location(world, x.toDouble(), y.toDouble(), z.toDouble()))
                }
            }
        }
        assertEquals(expected.size, actual.size)
        assertEquals(expected, actual)
    }
}