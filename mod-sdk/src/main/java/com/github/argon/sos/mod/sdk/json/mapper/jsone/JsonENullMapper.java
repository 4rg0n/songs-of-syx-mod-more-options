package com.github.argon.sos.mod.sdk.json.mapper.jsone;

import com.github.argon.sos.mod.sdk.json.element.JsonNull;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import snake2d.util.file.JsonE;

public class JsonENullMapper implements JsonEMapper<JsonNull> {

    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonNull jsonElement) {
        json.add(key, "null");
        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonNull.class);
    }
}
