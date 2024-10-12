package com.github.argon.sos.mod.sdk.game.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import snake2d.Errors;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileApiIntegrationTest {
    private final Path path = Paths.get("RandomFileThatShouldBeDeletedEveryTime" + this.getClass().getSimpleName() + "RunsTests.save");

    @BeforeEach
    @AfterEach
    public void initAndCleanUp() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new Errors.DataError("Unable to process file", path);
        }
    }

    @Test
    public void happyFlow() throws IOException {
        // Arrange
        String key = "abc";
        Parent value = new Parent();
        value.child = new Child();
        value.child.five = new HashMap<>();
        value.child.five.put(5, 5);
        value.one = new int[1];
        value.one[0] = 1;
        value.word = "word";

        FilePutterApi fileSaver = new FilePutterApi();
        FilePutter filePutter = new FilePutter(path, (1 << 26));

        FileGetterApi fileLoader = new FileGetterApi();
        FileGetter fileGetter;

        // Act
        fileSaver.put(key, value);
        fileSaver.onGameSaved(filePutter);
        filePutter.zip();
        fileGetter = new FileGetter(path, true);
        fileLoader.onGameLoaded(fileGetter);
        Parent result = fileLoader.get(key, Parent.class);
        fileGetter.close();

        // Assert
        Assertions.assertEquals(value.child.five.get(5), result.child.five.get(5));
        Assertions.assertEquals(value.one[0], result.one[0]);
        Assertions.assertEquals(value.word, result.word);
    }

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Parent {
        public Child child;

        public int[] one;

        public String word;
    }

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Child {
        public HashMap<Integer, Integer> five;
    }
}
