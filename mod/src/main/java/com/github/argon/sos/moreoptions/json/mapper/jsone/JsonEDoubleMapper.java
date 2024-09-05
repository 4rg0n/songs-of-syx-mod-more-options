package com.github.argon.sos.moreoptions.json.mapper.jsone;

import com.github.argon.sos.moreoptions.json.element.JsonDouble;
import com.github.argon.sos.moreoptions.util.ClassUtil;
import snake2d.util.file.JsonE;

public class JsonEDoubleMapper implements JsonEMapper<JsonDouble> {

    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonDouble jsonElement) {
        json.add(key, jsonElement.getValue());

        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonDouble.class);
    }
}
