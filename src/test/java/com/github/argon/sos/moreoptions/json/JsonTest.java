package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.config.ResourceService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

class JsonTest {

    private final ResourceService resourceService = ResourceService.getInstance();

    @Test
    void parse_JsonEAndProduceJsonE() throws IOException {
        String jsonString = resourceService.readResource("json/JsonE.txt");
        Json json = new Json(jsonString, JsonWriter.getJsonE());

        String parsedJsonString = json.toString();

        assertEqualsWithoutWhitespace(jsonString, parsedJsonString);
    }

    @Test
    void parse_JsonEAndProduceJson() throws IOException {
        String jsonEString = resourceService.readResource("json/JsonE.txt");
        String jsonString = resourceService.readResource("json/Json.txt");
        Json json = new Json(jsonEString, new JsonWriter(true, true, false, true));

        String parsedJsonString = json.toString();
        assertEqualsWithoutWhitespace(jsonString, parsedJsonString);
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