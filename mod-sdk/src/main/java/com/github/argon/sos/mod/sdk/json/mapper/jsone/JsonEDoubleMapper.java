package com.github.argon.sos.mod.sdk.json.mapper.jsone;

import com.github.argon.sos.mod.sdk.json.element.JsonDouble;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import snake2d.util.file.JsonE;

/**
 * For mapping standard JSON objects to the game JSON format.
 * Maps double values.
 */
public class JsonEDoubleMapper implements JsonEMapper<JsonDouble> {

    /** Creates a new {@link JsonEDoubleMapper}. */
    public JsonEDoubleMapper() {
    }

    /**
     * Maps a {@link JsonDouble} to the games {@link JsonE}.
     *
     * @param json game json to map into
     * @param key of the json element
     * @param jsonDouble to map
     * @return mapped game json object
     */
    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonDouble jsonDouble) {
        json.add(key, jsonDouble.getValue());

        return json;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonDouble.class);
    }
}
