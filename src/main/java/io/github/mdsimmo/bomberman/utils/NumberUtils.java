package io.github.mdsimmo.bomberman.utils;

import java.util.OptionalInt;

public class NumberUtils {

    public static OptionalInt tryParseInt(String data) {
        try {
            return OptionalInt.of(Integer.parseInt(data));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

}
