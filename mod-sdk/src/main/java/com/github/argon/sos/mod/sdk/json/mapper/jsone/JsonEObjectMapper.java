package com.github.argon.sos.mod.sdk.json.mapper.jsone;

import com.github.argon.sos.mod.sdk.json.JsonMapper;
import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import snake2d.util.file.JsonE;

public class JsonEObjectMapper implements JsonEMapper<JsonObject> {

    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonObject jsonElement) {
        json.add(key, JsonMapper.mapJson(jsonElement));

        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonObject.class);
    }
}
