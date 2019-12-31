package io.github.mdsimmo.bomberman.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CompressedListTest {

    @Test
    public void testSimpleEncodeDecode() {
        List<String> input = new ArrayList<String>() {{
           add("aaaa");
           add("bb");
           add("ccc");
        }};

        String encoded = CompressedList.encode(input.iterator(), s -> s);
        List<String> decoded = CompressedList.decode(encoded, s -> s);
        assertEquals(input, decoded);
    }

    @Test
    public void testSimpleEncodeDecodeSemicolen() {
        List<String> input = new ArrayList<String>() {{
            add("aaa;a");
            add("b;b");
            add("c;cc");
        }};

        String encoded = CompressedList.encode(input.iterator(), s -> s);
        List<String> decoded = CompressedList.decode(encoded, s -> s);
        assertEquals(input, decoded);
    }

}