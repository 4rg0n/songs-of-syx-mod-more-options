package com.github.argon.sos.mod.sdk.json.mapper.jsone;

import com.github.argon.sos.mod.sdk.json.element.JsonBoolean;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import snake2d.util.file.JsonE;

/**
 * For mapping standard JSON objects to the game JSON format.
 * Maps boolean values.
 */
public class JsonEBooleanMapper implements JsonEMapper<JsonBoolean> {

    /** Creates a new {@link JsonEBooleanMapper}. */
    public JsonEBooleanMapper() {
    }

    /**
     * Maps a {@link JsonBoolean} to the games {@link JsonE}.
     *
     * @param json game json to map into
     * @param key of the json element
     * @param jsonBoolean to map
     * @return mapped game json object
     */
    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonBoolean jsonBoolean) {
        json.add(key, jsonBoolean.getValue());

        return json;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonBoolean.class);
    }
}
