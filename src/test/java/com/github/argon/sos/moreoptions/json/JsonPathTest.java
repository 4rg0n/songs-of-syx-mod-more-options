package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.json.element.JsonBoolean;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonPathTest {

    @Test
    void extract() {
        JsonPath jsonPath = JsonPath.get("TEST.TEST");

        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.put("TEST", new JsonBoolean(true));

        JsonObject json = new JsonObject();
        json.put("TEST", jsonObject1);

        Optional<JsonElement> result = jsonPath.extract(json);

        Assertions.assertThat(result.isPresent()).isTrue();
        Assertions.assertThat(result.get()).isInstanceOf(JsonBoolean.class);
    }
}