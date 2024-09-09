package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.json.JsonPath;
import com.github.argon.sos.mod.sdk.json.element.*;
import com.github.argon.sos.mod.sdk.util.Maps;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JsonPathTest {

    @Test
    void get_Simple() {
        JsonPath jsonPath = JsonPath.get("TEST.TEST");

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("TEST", new JsonBoolean(true));

        JsonObject json = new JsonObject();
        json.put("TEST", jsonObject);

        Optional<JsonElement> result = jsonPath.get(json);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isInstanceOf(JsonBoolean.class);
        assertThat(result.get())
            .extracting(JsonBoolean.class::cast)
            .extracting(JsonBoolean::getValue)
            .isEqualTo(true);
    }


    @Test
    void get_Array() {
        JsonPath jsonPath = JsonPath.get("TEST.TEST[1]");

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(JsonLong.of(1));
        jsonArray.add(JsonLong.of(2));
        jsonArray.add(JsonLong.of(3));

        JsonObject json = new JsonObject();
        json.put("TEST", jsonArray);

        Optional<JsonElement> result = jsonPath.get(json);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isInstanceOf(JsonLong.class);
        assertThat(result.get())
            .extracting(JsonLong.class::cast)
            .extracting(JsonLong::getValue)
            .isEqualTo(2L);
    }

    @Test
    void get_ArrayNested() {
        JsonPath jsonPath = JsonPath.get("TEST[1].TEST[2]");

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(JsonLong.of(1));
        jsonArray.add(JsonLong.of(2));
        jsonArray.add(JsonLong.of(3));

        JsonObject json = JsonObject.of(Maps.of("TEST",
            JsonArray.of(
                JsonObject.of(Maps.of("TEST", jsonArray)),
                JsonObject.of(Maps.of("TEST", jsonArray)),
                JsonObject.of(Maps.of("TEST", jsonArray))
            )));

        Optional<JsonElement> result = jsonPath.get(json);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isInstanceOf(JsonLong.class);
        assertThat(result.get())
            .extracting(JsonLong.class::cast)
            .extracting(JsonLong::getValue)
            .isEqualTo(3L);
    }

    @Test
    void put_ArrayNested() {
        JsonPath jsonPath = JsonPath.get("TEST.TEST[1].TEST.TEST[2]");

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(JsonLong.of(1));
        jsonArray.add(JsonLong.of(2));
        jsonArray.add(JsonLong.of(3));

        JsonObject json = JsonObject.of(Maps.of("TEST",
            JsonArray.of(
                JsonObject.of(Maps.of("TEST", jsonArray)),
                JsonObject.of(Maps.of("TEST", jsonArray)),
                JsonObject.of(Maps.of("TEST", jsonArray))
            )));

        jsonPath.put(json, JsonLong.of(1337));
        assertThat(jsonArray.get(2)).isEqualTo(JsonLong.of(1337));
    }

    @Test
    void put_Simple() {
        JsonPath jsonPath = JsonPath.get("TEST.TEST");

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

        JsonPath jsonPath = JsonPath.get("TEST[1].TEST");
        jsonPath.put(json, JsonLong.of(1337));
        assertThat(testTuple.getValue()).isEqualTo(JsonLong.of(1337));
    }

    @Test
    void get_Tuple() {
        JsonObject json = JsonObject.of("TEST", JsonArray.of(
            JsonTuple.of("MUH", JsonLong.of(1)),
            JsonTuple.of("TEST", JsonLong.of(1337)),
            JsonTuple.of("BLUB", JsonLong.of(3))
        ));

        JsonPath jsonPath = JsonPath.get("TEST[1].TEST");
        Optional<JsonElement> jsonElement = jsonPath.get(json);
        assertThat(jsonElement).isPresent();
        assertThat(jsonElement.get()).isEqualTo(JsonLong.of(1337));
    }

    @Test
    void get_Empty() {
        JsonObject json = new JsonObject();

        JsonPath jsonPath = JsonPath.get("NOT.PRESENT");
        Optional<JsonElement> jsonElement = jsonPath.get(json);
        assertThat(jsonElement).isEmpty();
    }

    @Test
    void get_EmptyTuple() {
        JsonObject json = JsonObject.of("TEST", JsonArray.of(
            JsonTuple.of("MUH", JsonLong.of(1)),
            JsonTuple.of("TEST", JsonLong.of(1337)),
            JsonTuple.of("BLUB", JsonLong.of(3))
        ));

        JsonPath jsonPath = JsonPath.get("TEST[1].FOO");
        Optional<JsonElement> jsonElement = jsonPath.get(json);
        assertThat(jsonElement).isEmpty();
    }

    @Test
    void get_EmptyArray() {
        JsonObject json = JsonObject.of("TEST", JsonArray.of(
            JsonLong.of(1),
            JsonLong.of(1337),
            JsonLong.of(3)
        ));

        JsonPath jsonPath = JsonPath.get("TEST[4]");
        Optional<JsonElement> jsonElement = jsonPath.get(json);
        assertThat(jsonElement).isEmpty();
    }

    @Test
    void get_EmptyObject() {
        JsonObject json = JsonObject.of("TEST", JsonObject.of("TEST", JsonLong.of(1337)));

        JsonPath jsonPath = JsonPath.get("TEST.NOT.PRESENT");
        Optional<JsonElement> jsonElement = jsonPath.get(json);
        assertThat(jsonElement).isEmpty();
    }
}