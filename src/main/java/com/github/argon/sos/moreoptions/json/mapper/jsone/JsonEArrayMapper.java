package com.github.argon.sos.moreoptions.json.mapper.jsone;

import com.github.argon.sos.moreoptions.json.mapper.JsonMapper;
import com.github.argon.sos.moreoptions.json.element.*;
import com.github.argon.sos.moreoptions.json.mapper.JsonMapperException;
import com.github.argon.sos.moreoptions.util.ClassUtil;
import com.github.argon.sos.moreoptions.util.PrimitivesCastUtil;
import snake2d.util.file.JsonE;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JsonEArrayMapper implements JsonEMapper<JsonArray> {

    @Override
    public JsonE mapJsonE(JsonE json, String key, JsonArray jsonArray) {
        List<JsonElement> elements = jsonArray.getElements();

        if (elements.isEmpty()) {
            json.add(key, "[]");
            return json;
        }

        JsonElement jsonElement = elements.get(0);

        if (jsonElement instanceof JsonLong) {
            List<Long> longList = elements.stream()
                .map(JsonLong.class::cast)
                .map(JsonLong::getValue)
                .collect(Collectors.toList());
            json.add(key, PrimitivesCastUtil.toIntegerArrayLong(longList));
        } else if (jsonElement instanceof JsonDouble) {
            List<Double> doubleList = elements.stream()
                .map(JsonDouble.class::cast)
                .map(JsonDouble::getValue)
                .collect(Collectors.toList());
            json.add(key, PrimitivesCastUtil.toDoubleArrayDouble(doubleList));
        } else if (jsonElement instanceof JsonBoolean) {
            String[] strings = elements.stream()
                .map(JsonBoolean.class::cast)
                .map(jsonBoolean -> jsonBoolean.getValue().toString())
                .toArray(String[]::new);
            json.add(key, strings);
        } else if (jsonElement instanceof JsonNull) {
            String[] nulls = Collections.nCopies(elements.size(), "null")
                .toArray(new String[0]);
            json.add(key, nulls);
        } else if (jsonElement instanceof JsonString) {
            String[] strings = elements.stream()
                .map(JsonString.class::cast)
                .map(JsonString::getValue).toArray(String[]::new);
            json.addStrings(key, strings);
        } else if (jsonElement instanceof JsonArray) {
            throw new JsonMapperException("An array in an array is not supported in JsonE");
        } else if (jsonElement instanceof JsonObject) {
            JsonE[] jsons = elements.stream()
                .map(JsonObject.class::cast)
                .map(JsonMapper::mapJson).toArray(JsonE[]::new);
            json.add(key, jsons);
        }

        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonArray.class);
    }
}
