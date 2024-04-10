package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.io.ResourceService;
import com.github.argon.sos.moreoptions.json.writer.JsonWriters;
import com.github.argon.sos.moreoptions.util.Lists;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

class JsonTest {

    private final ResourceService resourceService = ResourceService.getInstance();

    @Test
    void parse_JsonEAndProduceJsonE() throws IOException {
        String jsonString = resourceService.readResource("json/JsonE.txt").orElse("");
        Json json = new Json(jsonString, JsonWriters.jsonEPretty());

        String parsedJsonString = json.write();

        assertEqualsWithoutWhitespace(jsonString, parsedJsonString);
    }

    @Test
    void parse_JsonEAndProduceJson() throws IOException {
        String jsonEString = resourceService.readResource("json/JsonE.txt").orElse("");
        String jsonString = resourceService.readResource("json/Json.txt").orElse("");
        Json json = new Json(jsonEString, JsonWriters.jsonPretty());

        String parsedJsonString = json.write();
        assertEqualsWithoutWhitespace(jsonString, parsedJsonString);
    }

    @Test
    void parse_gameConfigs() {
        List<String> files = Lists.of(
            "json/ARGONOSH.txt",
            "json/CRETONIAN.txt",
            "json/MINE_GEM.txt"
        );

        Map<String, Throwable> errors = new HashMap<>();

        files.forEach(file -> {
            try {
                String jsonString = resourceService.readResource(file)
                    .orElseThrow(() -> new AssertionError("No file?" + file));
                Json json = new Json(jsonString, JsonWriters.jsonEPretty());
                Assertions.assertThat(json.write()).as(file).isNotEmpty();
            } catch (Exception e) {
                errors.put(file, e);
            }
        });

        if (!errors.isEmpty()) {
            errors.forEach((file, throwable) -> {
                System.err.println(file);
                throwable.printStackTrace();
            });
            throw new AssertionError("There are parsing errors");
        }
    }

    @Test
    void prettyPrint_JsonE() throws IOException {
        String jsonEString = resourceService.readResource("json/MINE_GEM.txt").orElse("");
        Json json = new Json(jsonEString, JsonWriters.jsonEPretty());

        System.out.println(json.write());
    }

    private void assertEqualsWithoutWhitespace(String actual, String expected) {
        Assertions.assertThat(actual.replaceAll("\\s+",""))
            .isEqualTo(expected.replaceAll("\\s+",""));
    }

    private void assertEqualsContent(String actual, String expected) {
        actual = actual.replaceAll("\\s+","");
        expected = expected.replaceAll("\\s+","");

        char[] actualChars = actual.toCharArray();
        Arrays.sort(actualChars);
        actual = new String(actualChars);

        char[] expectedChars = expected.toCharArray();
        Arrays.sort(expectedChars);
        expected = new String(expectedChars);

        Assertions.assertThat(actual)
            .isEqualTo(expected);
    }
}