package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.utils.Dim;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DimTest {

    @Test
    public void testStream() {
        Dim test = new Dim(2, 3, 4);

        Set<Dim> dims = test.stream().collect(Collectors.toSet());

        Assert.assertEquals(dims,
                Stream.of(
                        new Dim(0, 0, 0), new Dim(0, 0, 1), new Dim(0, 0, 2), new Dim(0, 0, 3),
                        new Dim(0, 1, 0), new Dim(0, 1, 1), new Dim(0, 1, 2), new Dim(0, 1, 3),
                        new Dim(0, 2, 0), new Dim(0, 2, 1), new Dim(0, 2, 2), new Dim(0, 2, 3),

                        new Dim(1, 0, 0), new Dim(1, 0, 1), new Dim(1, 0, 2), new Dim(1, 0, 3),
                        new Dim(1, 1, 0), new Dim(1, 1, 1), new Dim(1, 1, 2), new Dim(1, 1, 3),
                        new Dim(1, 2, 0), new Dim(1, 2, 1), new Dim(1, 2, 2), new Dim(1, 2, 3)
                ));
    }

}
