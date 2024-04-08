package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.json.element.*;
import com.github.argon.sos.moreoptions.util.Maps;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class JsonPathTest {

    @Test
    void getSimple() {
        JsonPath jsonPath = JsonPath.get("TEST.TEST");

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("TEST", new JsonBoolean(true));

        JsonObject json = new JsonObject();
        json.put("TEST", jsonObject);

        Optional<JsonElement> result = jsonPath.get(json);

        Assertions.assertThat(result.isPresent()).isTrue();
        Assertions.assertThat(result.get()).isInstanceOf(JsonBoolean.class);
        Assertions.assertThat(result.get())
            .extracting(JsonBoolean.class::cast)
            .extracting(JsonBoolean::getValue)
            .isEqualTo(true);
    }


    @Test
    void getArray() {
        JsonPath jsonPath = JsonPath.get("TEST.TEST[1]");

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(JsonLong.of(1));
        jsonArray.add(JsonLong.of(2));
        jsonArray.add(JsonLong.of(3));

        JsonObject json = new JsonObject();
        json.put("TEST", jsonArray);

        Optional<JsonElement> result = jsonPath.get(json);
        Assertions.assertThat(result.isPresent()).isTrue();
        Assertions.assertThat(result.get()).isInstanceOf(JsonLong.class);
        Assertions.assertThat(result.get())
            .extracting(JsonLong.class::cast)
            .extracting(JsonLong::getValue)
            .isEqualTo(2L);
    }

    @Test
    void getArrayNested() {
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

        Optional<JsonElement> result = jsonPath.get(json);
        Assertions.assertThat(result.isPresent()).isTrue();
        Assertions.assertThat(result.get()).isInstanceOf(JsonLong.class);
        Assertions.assertThat(result.get())
            .extracting(JsonLong.class::cast)
            .extracting(JsonLong::getValue)
            .isEqualTo(3L);
    }

    @Test
    void putArrayNested() {
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
        Assertions.assertThat(jsonArray.get(2)).isEqualTo(JsonLong.of(1337));
    }

    @Test
    void putSimple() {
        JsonPath jsonPath = JsonPath.get("TEST.TEST");

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("TEST", JsonBoolean.of(true));

        JsonObject json = new JsonObject();
        json.put("TEST", jsonObject);

        jsonPath.put(json, JsonBoolean.of(false));
        Assertions.assertThat(jsonObject.get("TEST")).isEqualTo(JsonBoolean.of(false));
    }
}