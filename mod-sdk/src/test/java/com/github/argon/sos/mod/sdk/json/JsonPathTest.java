package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.json.element.*;
import com.github.argon.sos.mod.testing.ModSdkExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModSdkExtension.class)
class JsonPathTest {

    @Test
    void of_Simple() {
        JsonPath jsonPath = JsonPath.of("TEST.TEST");

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("TEST", new JsonBoolean(true));

        JsonObject json = new JsonObject();
        json.put("TEST", jsonObject);

        Optional<JsonElement> result = jsonPath.of(json);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isInstanceOf(JsonBoolean.class);
        assertThat(result.get())
            .extracting(JsonBoolean.class::cast)
            .extracting(JsonBoolean::getValue)
            .isEqualTo(true);
    }


    @Test
    void of_Array() {
        JsonPath jsonPath = JsonPath.of("TEST.TEST[1]");

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(JsonLong.of(1));
        jsonArray.add(JsonLong.of(2));
        jsonArray.add(JsonLong.of(3));

        JsonObject json = new JsonObject();
        json.put("TEST", jsonArray);

        Optional<JsonElement> result = jsonPath.of(json);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isInstanceOf(JsonLong.class);
        assertThat(result.get())
            .extracting(JsonLong.class::cast)
            .extracting(JsonLong::getValue)
            .isEqualTo(2L);
    }

    @Test
    void of_ArrayNested() {
        JsonPath jsonPath = JsonPath.of("TEST[1].TEST[2]");

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(JsonLong.of(1));
        jsonArray.add(JsonLong.of(2));
        jsonArray.add(JsonLong.of(3));

        JsonObject json = JsonObject.of(Map.of("TEST",
            JsonArray.of(
                JsonObject.of(Map.of("TEST", jsonArray)),
                JsonObject.of(Map.of("TEST", jsonArray)),
                JsonObject.of(Map.of("TEST", jsonArray))
            )));

        Optional<JsonElement> result = jsonPath.of(json);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isInstanceOf(JsonLong.class);
        assertThat(result.get())
            .extracting(JsonLong.class::cast)
            .extracting(JsonLong::getValue)
            .isEqualTo(3L);
    }

    @Test
    void put_ArrayNested() {
        JsonPath jsonPath = JsonPath.of("TEST.TEST[1].TEST.TEST[2]");

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(JsonLong.of(1));
        jsonArray.add(JsonLong.of(2));
        jsonArray.add(JsonLong.of(3));

        JsonObject json = JsonObject.of(Map.of("TEST",
            JsonArray.of(
                JsonObject.of(Map.of("TEST", jsonArray)),
                JsonObject.of(Map.of("TEST", jsonArray)),
                JsonObject.of(Map.of("TEST", jsonArray))
            )));

        jsonPath.put(json, JsonLong.of(1337));
        assertThat(jsonArray.get(2)).isEqualTo(JsonLong.of(1337));
    }

    @Test
    void put_Simple() {
        JsonPath jsonPath = JsonPath.of("TEST.TEST");

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("TEST", JsonBoolean.of(true));

        JsonObject json = new JsonObject();
        json.put("TEST", jsonObject);

        jsonPath.put(json, JsonBoolean.of(false));
        assertThat(jsonObject.get("TEST")).isEqualTo(JsonBoolean.of(false));
    }

    @Test
    void put_Tuple() {
        JsonTuple testTuple = JsonTuple.of("TEST", JsonLong.of(2));
        JsonObject json = JsonObject.of("TEST", JsonArray.of(
            JsonTuple.of("TEST", JsonLong.of(1)),
            testTuple,
            JsonTuple.of("TEST", JsonLong.of(3))
        ));

        JsonPath jsonPath = JsonPath.of("TEST[1].TEST");
        jsonPath.put(json, JsonLong.of(1337));
        assertThat(testTuple.getValue()).isEqualTo(JsonLong.of(1337));
    }

    @Test
    void of_Tuple() {
        JsonObject json = JsonObject.of("TEST", JsonArray.of(
            JsonTuple.of("MUH", JsonLong.of(1)),
            JsonTuple.of("TEST", JsonLong.of(1337)),
            JsonTuple.of("BLUB", JsonLong.of(3))
        ));

        JsonPath jsonPath = JsonPath.of("TEST[1].TEST");
        Optional<JsonElement> jsonElement = jsonPath.of(json);
        assertThat(jsonElement).isPresent();
        assertThat(jsonElement.get()).isEqualTo(JsonLong.of(1337));
    }

    @Test
    void of_Empty() {
        JsonObject json = new JsonObject();

        JsonPath jsonPath = JsonPath.of("NOT.PRESENT");
        Optional<JsonElement> jsonElement = jsonPath.of(json);
        assertThat(jsonElement).isEmpty();
    }

    @Test
    void of_EmptyTuple() {
        JsonObject json = JsonObject.of("TEST", JsonArray.of(
            JsonTuple.of("MUH", JsonLong.of(1)),
            JsonTuple.of("TEST", JsonLong.of(1337)),
            JsonTuple.of("BLUB", JsonLong.of(3))
        ));

        JsonPath jsonPath = JsonPath.of("TEST[1].FOO");
        Optional<JsonElement> jsonElement = jsonPath.of(json);
        assertThat(jsonElement).isEmpty();
    }

    @Test
    void of_EmptyArray() {
        JsonObject json = JsonObject.of("TEST", JsonArray.of(
            JsonLong.of(1),
            JsonLong.of(1337),
            JsonLong.of(3)
        ));

        JsonPath jsonPath = JsonPath.of("TEST[4]");
        Optional<JsonElement> jsonElement = jsonPath.of(json);
        assertThat(jsonElement).isEmpty();
    }

    @Test
    void of_EmptyObject() {
        JsonObject json = JsonObject.of("TEST", JsonObject.of("TEST", JsonLong.of(1337)));

        JsonPath jsonPath = JsonPath.of("TEST.NOT.PRESENT");
        Optional<JsonElement> jsonElement = jsonPath.of(json);
        assertThat(jsonElement).isEmpty();
    }
}