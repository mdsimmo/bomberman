package io.github.mdsimmo.bomberman.utils;

import io.github.mdsimmo.bomberman.commands.game.Create;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class TestReadFromResources {

    @Test
    public void testReadResources() throws URISyntaxException, IOException {
        String filename = "test.txt";
        URL schemaURL = ClassLoader.getSystemResource(filename);
        File schemaFile = new File(schemaURL.toURI());
        FileInputStream inputStream = new FileInputStream(schemaFile);
        StringBuilder builder = new StringBuilder();
        int ch;
        while ((ch = inputStream.read()) != -1) {
            builder.append((char) ch);
        }

        assertEquals("Hello world", builder.toString());
    }
}
