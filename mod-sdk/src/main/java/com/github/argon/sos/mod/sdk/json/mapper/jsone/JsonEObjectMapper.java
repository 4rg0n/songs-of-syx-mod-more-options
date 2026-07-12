package com.github.argon.sos.mod.sdk.json.mapper.jsone;

import com.github.argon.sos.mod.sdk.json.JsonMapper;
import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import snake2d.util.file.JsonE;

/**
 * For mapping standard JSON objects to the game JSON format.
 * Maps objects.
 */
public class JsonEObjectMapper implements JsonEMapper<JsonObject> {

    /** Creates a new {@link JsonEObjectMapper}. */
    public JsonEObjectMapper() {
    }

    /**
     * Maps a {@link JsonObject} to the games {@link JsonE}.
     *
     * @param json game json to map into
     * @param key of the json element
     * @param jsonObject to map
     * @return mapped game json object
     */
    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonObject jsonObject) {
        json.add(key, JsonMapper.mapJson(jsonObject));

        return json;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonObject.class);
    }
}
