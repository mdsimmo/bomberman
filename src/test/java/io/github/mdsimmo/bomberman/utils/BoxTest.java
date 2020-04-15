package io.github.mdsimmo.bomberman.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Test;

import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BoxTest {

    @Test
    public void testBoxFromExtremes() {
        var world = mock(World.class);
        var box = new Box(world, new Dim(1, 2, 3), new Dim(4, 5, 6));

        assertEquals(box.getP1(), new Dim(1, 2, 3));
        assertEquals(box.getP2(), new Dim(4, 5, 6));
    }

    @Test
    public void testBoxFromSize() {
        var world = mock(World.class);
        var box = new Box(world, 4, 5, 6, 1, 2, 3);

        assertEquals(new Dim(4, 5, 6), box.getP1());
        assertEquals(new Dim(4+1-1, 5+2-1, 6+3-1), box.getP2());
    }

    @Test
    public void testBoxSize() {
        var world = mock(World.class);
        var box = new Box(world, 10, 11, 12, 5, 6, 7);

        assertEquals(new Dim(5, 6, 7), box.getSize());
    }

    @Test
    public void testBoxStream() {
        var world = mock(World.class);
        when(world.toString()).thenReturn("mockWorld");
        var box = new Box(world, 10, 11, 12, 2, 3, 4);

        var actual = box.stream().collect(Collectors.toSet());

        var expected = new HashSet<>();
        for (int x = 10; x < 10 + 2; ++x) {
            for (int y = 11; y < 11 + 3; ++y) {
                for (int z = 12; z < 12 + 4; ++z) {
                    expected.add(new Location(world, x, y, z));
                }
            }
        }
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

}
