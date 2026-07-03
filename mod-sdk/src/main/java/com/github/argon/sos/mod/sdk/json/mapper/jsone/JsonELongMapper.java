package com.github.argon.sos.mod.sdk.json.mapper.jsone;

import com.github.argon.sos.mod.sdk.json.element.JsonLong;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import snake2d.util.file.JsonE;

/**
 * For mapping standard JSON objects to the game JSON format.
 * Maps long values.
 */
public class JsonELongMapper implements JsonEMapper<JsonLong> {

    /**
     * Maps a {@link JsonLong} to the games {@link JsonE}.
     *
     * @param json game json to map into
     * @param key of the json element
     * @param jsonLong to map
     * @return mapped game json object
     */
    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonLong jsonLong) {
        json.add(key,  jsonLong.getValue().intValue());

        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonLong.class);
    }
}
