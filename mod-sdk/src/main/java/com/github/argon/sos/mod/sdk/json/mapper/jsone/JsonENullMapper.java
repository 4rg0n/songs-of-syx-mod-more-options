package com.github.argon.sos.mod.sdk.json.mapper.jsone;

import com.github.argon.sos.mod.sdk.json.element.JsonNull;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import snake2d.util.file.JsonE;

/**
 * For mapping standard JSON objects to the game JSON format.
 * Maps null values.
 */
public class JsonENullMapper implements JsonEMapper<JsonNull> {

    /**
     * Maps a {@link JsonNull} to the games {@link JsonE}.
     *
     * @param json game json to map into
     * @param key of the json element
     * @param jsonNull to map
     * @return mapped game json object
     */
    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonNull jsonNull) {
        json.add(key, "null");
        return json;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonNull.class);
    }
}
