package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.file.ResourceService;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
import com.github.argon.sos.mod.testing.ModSdkExtension;
import com.github.argon.sos.mod.sdk.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModSdkExtension.class)
class JsonTest {

    private final ResourceService resourceService = new ResourceService();

    @Test
    void parse_JsonEAndProduceJsonE() throws IOException {
        String jsonString = resourceService.read("json/JsonE.txt").orElse("");
        Json json = new Json(jsonString, JsonWriters.jsonEPretty());

        String parsedJsonString = json.write();

        assertThat(parsedJsonString).isEqualToIgnoringWhitespace(jsonString);
    }

    @Test
    void parse_JsonEAndProduceJson() throws IOException {
        String jsonEString = resourceService.read("json/JsonE.txt").orElse("");
        String jsonString = resourceService.read("json/Json.txt").orElse("");
        Json json = new Json(jsonEString, JsonWriters.jsonPretty());

        String parsedJsonString = json.write();
        assertThat(parsedJsonString).isEqualToIgnoringWhitespace(jsonString);
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
                String jsonString = resourceService.read(file)
                    .orElseThrow(() -> new AssertionError("No file?" + file));
                Json json = new Json(jsonString, JsonWriters.jsonEPretty());
                assertThat(json.write()).as(file).isNotEmpty();
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
        String jsonEString = resourceService.read("json/MINE_GEM.txt").orElse("");
        Json json = new Json(jsonEString, JsonWriters.jsonEPretty());

        System.out.println(json.write());
    }
}