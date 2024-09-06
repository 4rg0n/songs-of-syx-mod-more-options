package com.github.argon.sos.moreoptions.json.mapper.jsone;

import com.github.argon.sos.moreoptions.json.element.JsonString;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import snake2d.util.file.JsonE;

public class JsonEStringMapper implements JsonEMapper<JsonString> {

    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonString jsonElement) {
        json.addString(key, jsonElement.getValue());

        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonString.class);
    }
}
