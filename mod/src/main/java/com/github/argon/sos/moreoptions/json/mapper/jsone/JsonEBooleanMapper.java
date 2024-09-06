package com.github.argon.sos.moreoptions.json.mapper.jsone;

import com.github.argon.sos.moreoptions.json.element.JsonBoolean;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import snake2d.util.file.JsonE;

public class JsonEBooleanMapper implements JsonEMapper<JsonBoolean> {

    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonBoolean jsonElement) {
        json.add(key, jsonElement.getValue());

        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonBoolean.class);
    }
}
