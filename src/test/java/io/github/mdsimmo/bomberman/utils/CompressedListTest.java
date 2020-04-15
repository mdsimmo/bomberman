package io.github.mdsimmo.bomberman.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CompressedListTest {

    @Test
    public void testSimpleEncodeDecode() {
        List<String> input = List.of("aaaa", "bb", "ccc");
        String encoded = CompressedList.encode(input.iterator(), s -> s);
        List<String> decoded = CompressedList.decode(encoded, s -> s);
        assertEquals(input, decoded);
    }

    @Test
    public void testMultiple() {
        List<String> input = List.of("abc", "abc", "efg", "abc");
        String encoded = CompressedList.encode(input.iterator(), s -> s);
        List<String> decoded = CompressedList.decode(encoded, s -> s);
        assertEquals(input, decoded);
    }

    @Test
    public void testEncodeDecodeSemicolon() {
        List<String> input = List.of("aaa;a", "b;b", "c;cc;");

        String encoded = CompressedList.encode(input.iterator(), s -> s);
        List<String> decoded = CompressedList.decode(encoded, s -> s);
        assertEquals(input, decoded);
    }

    @Test
    public void testEncodeDecodeMultiple() {
        List<String> input = List.of("a!aa!2", "bb!4;", "bb!4");

        String encoded = CompressedList.encode(input.iterator(), s -> s);
        List<String> decoded = CompressedList.decode(encoded, s -> s);
        assertEquals(input, decoded);
    }

    @Test
    public void testEncodeDecodeBackslash() {
        List<String> input = List.of("a!aa\\!2;", "b;\\b!4\\;", "bb!4");

        String encoded = CompressedList.encode(input.iterator(), s -> s);
        List<String> decoded = CompressedList.decode(encoded, s -> s);
        assertEquals(input, decoded);
    }
}