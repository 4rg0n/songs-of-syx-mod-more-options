package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.file.ResourceService;
import com.github.argon.sos.mod.sdk.json.annotation.JsonProperty;
import com.github.argon.sos.mod.sdk.json.element.*;
import com.github.argon.sos.mod.sdk.json.mapper.TypeInfo;
import com.github.argon.sos.mod.sdk.util.Maps;
import lombok.Data;
import org.junit.jupiter.api.Test;
import snake2d.util.file.JsonE;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JsonMapperTest {

    @Test
    void mapJson_simpleTypes() {
        // String
        JsonString jsonString = new JsonString("TEST1");
        String mappedString = JsonMapper.mapJson(jsonString, TypeInfo.get(String.class));
        assertThat(mappedString).isEqualTo("TEST1");

        // Enum
        TestEnum mappedEnum = JsonMapper.mapJson(jsonString, TypeInfo.get(TestEnum.class));
        assertThat(mappedEnum).isEqualTo(TestEnum.TEST1);

        // Long
        JsonLong jsonLong = new JsonLong(1L);
        Long mappedLong = JsonMapper.mapJson(jsonLong, TypeInfo.get(Long.class));
        assertThat(mappedLong).isEqualTo(1L);

        Integer mappedInteger = JsonMapper.mapJson(jsonLong, TypeInfo.get(Integer.class));
        assertThat(mappedInteger).isEqualTo(1);

        Byte mappedByte = JsonMapper.mapJson(jsonLong, TypeInfo.get(Byte.class));
        assertThat(mappedByte).isEqualTo(Integer.valueOf(1).byteValue());

        Short mappedShort = JsonMapper.mapJson(jsonLong, TypeInfo.get(Short.class));
        assertThat(mappedShort).isEqualTo(Integer.valueOf(1).shortValue());

        BigInteger mappedBigInteger = JsonMapper.mapJson(jsonLong, TypeInfo.get(BigInteger.class));
        assertThat(mappedBigInteger).isEqualTo(BigInteger.valueOf(1L));

        // Double
        JsonDouble jsonDouble = new JsonDouble(1.1d);
        Double mappedDouble = JsonMapper.mapJson(jsonDouble, TypeInfo.get(Double.class));
        assertThat(mappedDouble).isEqualTo(1.1d);

        Float mappedFloat = JsonMapper.mapJson(jsonDouble, TypeInfo.get(Float.class));
        assertThat(mappedFloat).isEqualTo(1.1f);

        BigDecimal mappedBigDecimal = JsonMapper.mapJson(jsonDouble, TypeInfo.get(BigDecimal.class));
        assertThat(mappedBigDecimal).isEqualTo(BigDecimal.valueOf(1.1d));

        // null
        JsonNull jsonNull = new JsonNull();
        Object mappedNull = JsonMapper.mapJson(jsonNull, Void.class);
        assertThat(mappedNull).isNull();

        // Boolean
        JsonBoolean jsonBoolean = new JsonBoolean(true);
        Boolean mappedBoolean = JsonMapper.mapJson(jsonBoolean, TypeInfo.get(Boolean.class));
        assertThat(mappedBoolean).isTrue();
    }

    @Test
    void mapJson_listTypes() {
        // Array
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(new JsonLong(1L));
        jsonArray.add(new JsonLong(2L));
        jsonArray.add(new JsonLong(3L));

        List<Long> mappedLongList = JsonMapper.mapJson(jsonArray, new TypeInfo<List<Long>>(){});
        assertThat(mappedLongList).isEqualTo(Arrays.asList(1L, 2L, 3L));

        // Array in Array
        JsonArray jsonArrayArray = new JsonArray();
        jsonArrayArray.add(jsonArray);
        jsonArrayArray.add(jsonArray);
        jsonArrayArray.add(jsonArray);

        List<List<Long>> mappedLongListList = JsonMapper.mapJson(jsonArrayArray, new TypeInfo<List<List<Long>>>(){});
        assertThat(mappedLongListList).isEqualTo(Arrays.asList(
            Arrays.asList(1L, 2L, 3L),
            Arrays.asList(1L, 2L, 3L),
            Arrays.asList(1L, 2L, 3L)));
    }

    @Test
    void mapJson_mapTypes() {
        // String key Map
        JsonObject jsonStringKeyObject = new JsonObject();
        jsonStringKeyObject.put("1", new JsonLong(1L));
        jsonStringKeyObject.put("2", new JsonLong(2L));
        jsonStringKeyObject.put("3", new JsonLong(3L));

        Map<String, Long> mappedJsonObject = JsonMapper.mapJson(jsonStringKeyObject, new TypeInfo<Map<String, Long>>(){});
        assertThat(mappedJsonObject).isEqualTo(Maps.of(
            "1", 1L,
            "2", 2L,
            "3", 3L
        ));

        // Enum key Map
        JsonObject jsonEnumKeyObject = new JsonObject();
        jsonEnumKeyObject.put(TestEnum.TEST1, new JsonLong(1L));
        jsonEnumKeyObject.put(TestEnum.TEST2, new JsonLong(2L));
        jsonEnumKeyObject.put(TestEnum.TEST3, new JsonLong(3L));

        Map<TestEnum, Long> mappedJsonEnumKeyObject = JsonMapper.mapJson(jsonEnumKeyObject, new TypeInfo<Map<TestEnum, Long>>(){});
        assertThat(mappedJsonEnumKeyObject).isEqualTo(Maps.of(
            TestEnum.TEST1, 1L,
            TestEnum.TEST2, 2L,
            TestEnum.TEST3, 3L
        ));

        // Map in Map
        JsonObject jsonObjectObject = new JsonObject();
        jsonObjectObject.put("1", jsonStringKeyObject);
        jsonObjectObject.put("2", jsonStringKeyObject);
        jsonObjectObject.put("3", jsonStringKeyObject);

        Map<String, Map<String, Long>> mappedJsonObjectObject = JsonMapper.mapJson(
            jsonObjectObject,
            new TypeInfo<Map<String, Map<String, Long>>>(){});

        assertThat(mappedJsonObjectObject).isEqualTo(Maps.of(
            "1", Maps.of(
                "1", 1L,
                "2", 2L,
                "3", 3L
            ),
            "2", Maps.of(
                "1", 1L,
                "2", 2L,
                "3", 3L
            ),
            "3", Maps.of(
                "1", 1L,
                "2", 2L,
                "3", 3L
            )
        ));
    }

    @Test
    void mapJson_objectTypes() {
        // Simple
        JsonObject jsonObjectSimple = new JsonObject();
        jsonObjectSimple.put("TEST_LONG", new JsonLong(1L));
        jsonObjectSimple.put("TEST_DOUBLE", new JsonDouble(1.1D));
        jsonObjectSimple.put("TEST_STRING", new JsonString("test"));
        jsonObjectSimple.put("TEST_BOOLEAN", new JsonBoolean(false));
        jsonObjectSimple.put("TEST_NULL", new JsonNull());

        SimplePojo simplePojo = new SimplePojo();
        simplePojo.setTestLong(1L);
        simplePojo.setTestDouble(1.1D);
        simplePojo.setTestString("test");
        simplePojo.setTestBoolean(false);
        simplePojo.setTestNull(null);

        SimplePojo mappedSimplePojo = JsonMapper.mapJson(jsonObjectSimple, SimplePojo.class);
        assertThat(mappedSimplePojo).isEqualTo(simplePojo);

        // Complex
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectSimple);
        jsonArray.add(jsonObjectSimple);
        jsonArray.add(jsonObjectSimple);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("1", jsonObjectSimple);
        jsonObject.put("2", jsonObjectSimple);
        jsonObject.put("3", jsonObjectSimple);

        JsonObject jsonObjectComplex = new JsonObject();
        jsonObjectComplex.put("TEST_SIMPLE_POJO", jsonObjectSimple);
        jsonObjectComplex.put("TEST_SIMPLE_POJO_LIST", jsonArray);
        jsonObjectComplex.put("TEST_SIMPLE_POJO_MAP", jsonObject);

        ComplexPojo complexPojo = new ComplexPojo();
        complexPojo.setTestSimplePojo(simplePojo);
        complexPojo.setTestSimplePojoList(Arrays.asList(simplePojo, simplePojo, simplePojo));
        complexPojo.setTestSimplePojoMap(Maps.of("1", simplePojo, "2", simplePojo, "3", simplePojo));

        ComplexPojo mappedComplexPojo = JsonMapper.mapJson(jsonObjectComplex, ComplexPojo.class);
        assertThat(mappedComplexPojo).isEqualTo(complexPojo);
    }

    @Test
    void mapObject_simpleTypes() {
        // String
        JsonElement jsonElementString = JsonMapper.mapObject("TEST1");
        assertThat(jsonElementString).isInstanceOf(JsonString.class);
        JsonString jsonString = (JsonString) jsonElementString;
        assertThat(jsonString.getValue()).isEqualTo("TEST1");

        // Enum
        jsonElementString = JsonMapper.mapObject(TestEnum.TEST1);
        assertThat(jsonElementString).isInstanceOf(JsonString.class);
        JsonString jsonEnumString = (JsonString) jsonElementString;
        assertThat(jsonEnumString.getValue()).isEqualTo("TEST1");

        // Long < Long
        JsonElement jsonElementLong = JsonMapper.mapObject(1L);
        assertThat(jsonElementLong).isInstanceOf(JsonLong.class);
        JsonLong jsonLong = (JsonLong) jsonElementLong;
        assertThat(jsonLong.getValue()).isEqualTo(1L);

        // Long < Integer
        jsonElementLong = JsonMapper.mapObject(1);
        assertThat(jsonElementLong).isInstanceOf(JsonLong.class);
        jsonLong = (JsonLong) jsonElementLong;
        assertThat(jsonLong.getValue()).isEqualTo(1L);

        // Long < Byte
        jsonElementLong = JsonMapper.mapObject(Integer.valueOf(1).byteValue());
        assertThat(jsonElementLong).isInstanceOf(JsonLong.class);
        jsonLong = (JsonLong) jsonElementLong;
        assertThat(jsonLong.getValue()).isEqualTo(1L);

        // Long < Short
        jsonElementLong = JsonMapper.mapObject(Integer.valueOf(1).shortValue());
        assertThat(jsonElementLong).isInstanceOf(JsonLong.class);
        jsonLong = (JsonLong) jsonElementLong;
        assertThat(jsonLong.getValue()).isEqualTo(1L);

        // Long < BigInteger
        jsonElementLong = JsonMapper.mapObject(BigInteger.valueOf(1L));
        assertThat(jsonElementLong).isInstanceOf(JsonLong.class);
        jsonLong = (JsonLong) jsonElementLong;
        assertThat(jsonLong.getValue()).isEqualTo(1L);

        // Double < Double
        JsonElement jsonElementDouble = JsonMapper.mapObject(1.1d);
        assertThat(jsonElementDouble).isInstanceOf(JsonDouble.class);
        JsonDouble jsonDouble = (JsonDouble) jsonElementDouble;
        assertThat(jsonDouble.getValue()).isEqualTo(1.1d);

        // Double < Float
        jsonElementDouble = JsonMapper.mapObject(1.1f);
        assertThat(jsonElementDouble).isInstanceOf(JsonDouble.class);
        jsonDouble = (JsonDouble) jsonElementDouble;
        assertThat(jsonDouble.getValue()).isEqualTo(Double.valueOf(1.1d).floatValue());

        // Double < BigDecimal
        jsonElementDouble = JsonMapper.mapObject(BigDecimal.valueOf(1.1d));
        assertThat(jsonElementDouble).isInstanceOf(JsonDouble.class);
        jsonDouble = (JsonDouble) jsonElementDouble;
        assertThat(jsonDouble.getValue()).isEqualTo(1.1d);

        // null
        JsonElement jsonElementNull = JsonMapper.mapObject(null);
        assertThat(jsonElementNull).isInstanceOf(JsonNull.class);

        // Boolean
        JsonElement jsonElementBoolean = JsonMapper.mapObject(true);
        assertThat(jsonElementBoolean).isInstanceOf(JsonBoolean.class);
        JsonBoolean jsonBoolean = (JsonBoolean) jsonElementBoolean;
        assertThat(jsonBoolean.getValue()).isTrue();
    }

    @Test
    void mapObject_listTypes() {
        // List
        JsonElement jsonElementArray = JsonMapper.mapObject(Arrays.asList(1, 2, 3), new TypeInfo<List<Integer>>(){});
        assertThat(jsonElementArray).isInstanceOf(JsonArray.class);
        JsonArray jsonArray = (JsonArray) jsonElementArray;

        JsonArray jsonArrayExpected = new JsonArray();
        jsonArrayExpected.add(new JsonLong(1L));
        jsonArrayExpected.add(new JsonLong(2L));
        jsonArrayExpected.add(new JsonLong(3L));

        assertThat(jsonArray).isEqualTo(jsonArrayExpected);

        // List in List
        JsonElement jsonElementArrayArray = JsonMapper.mapObject(Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(1, 2, 3),
            Arrays.asList(1, 2, 3)),
            new TypeInfo<List<List<Integer>>>(){}
        );
        assertThat(jsonElementArrayArray).isInstanceOf(JsonArray.class);
        JsonArray jsonArrayArray = (JsonArray) jsonElementArrayArray;

        JsonArray jsonArrayArrayExpected = new JsonArray();
        jsonArrayArrayExpected.add(jsonArrayExpected);
        jsonArrayArrayExpected.add(jsonArrayExpected);
        jsonArrayArrayExpected.add(jsonArrayExpected);

        assertThat(jsonArrayArray).isEqualTo(jsonArrayArrayExpected);
    }

    @Test
    void mapObject_mapTypes() {
        // String key Map
        JsonElement jsonElementObject = JsonMapper.mapObject(Maps.of(
                "1", 1,
                "2", 2,
                "3", 3),
            new TypeInfo<Map<String, Integer>>() {
            }
        );
        assertThat(jsonElementObject).isInstanceOf(JsonObject.class);
        JsonObject jsonObject = (JsonObject) jsonElementObject;

        JsonObject jsonObjectExpected = new JsonObject();
        jsonObjectExpected.put("1", new JsonLong(1L));
        jsonObjectExpected.put("2", new JsonLong(2L));
        jsonObjectExpected.put("3", new JsonLong(3L));

        assertThat(jsonObject).isEqualTo(jsonObjectExpected);

        // Enum key Map
        jsonElementObject = JsonMapper.mapObject(Maps.of(
                TestEnum.TEST1, 1,
                TestEnum.TEST2, 2,
                TestEnum.TEST3, 3),
            new TypeInfo<Map<String, Integer>>() {
            }
        );
        assertThat(jsonElementObject).isInstanceOf(JsonObject.class);
        jsonObject = (JsonObject) jsonElementObject;

        JsonObject jsonObjectEnumExpected = new JsonObject();
        jsonObjectEnumExpected.put(TestEnum.TEST1, new JsonLong(1L));
        jsonObjectEnumExpected.put(TestEnum.TEST2, new JsonLong(2L));
        jsonObjectEnumExpected.put(TestEnum.TEST3, new JsonLong(3L));

        assertThat(jsonObject).isEqualTo(jsonObjectEnumExpected);

        // Map in Map
        jsonElementObject = JsonMapper.mapObject(Maps.of(
                "1", Maps.of(
                    "1", 1,
                    "2", 2,
                    "3", 3),
                "2", Maps.of(
                    "1", 1,
                    "2", 2,
                    "3", 3),
                "3", Maps.of(
                    "1", 1,
                    "2", 2,
                    "3", 3)),
            new TypeInfo<Map<String, Map<String, Integer>>>() {
            }
        );
        assertThat(jsonElementObject).isInstanceOf(JsonObject.class);
        jsonObject = (JsonObject) jsonElementObject;

        JsonObject jsonObjectObjectExpected = new JsonObject();
        jsonObjectObjectExpected.put("1", jsonObjectExpected);
        jsonObjectObjectExpected.put("2", jsonObjectExpected);
        jsonObjectObjectExpected.put("3", jsonObjectExpected);

        assertThat(jsonObject).isEqualTo(jsonObjectObjectExpected);
    }

    @Test
    void mapObject_objectTypes() {
        // Simple
        SimplePojo simplePojo = new SimplePojo();
        simplePojo.setTestLong(1L);
        simplePojo.setTestDouble(1.1D);
        simplePojo.setTestString("test");
        simplePojo.setTestBoolean(false);
        simplePojo.setTestNull(null);

        JsonElement jsonElementObject = JsonMapper.mapObject(simplePojo);
        assertThat(jsonElementObject).isInstanceOf(JsonObject.class);
        JsonObject jsonObject = (JsonObject) jsonElementObject;

        JsonObject jsonObjectExpected = new JsonObject();
        jsonObjectExpected.put("TEST_LONG", new JsonLong(1L));
        jsonObjectExpected.put("TEST_DOUBLE", new JsonDouble(1.1D));
        jsonObjectExpected.put("TEST_STRING", new JsonString("test"));
        jsonObjectExpected.put("TEST_BOOLEAN", new JsonBoolean(false));
        jsonObjectExpected.put("TEST_NULL", new JsonNull());

        assertThat(jsonObject).isEqualTo(jsonObjectExpected);

        // Complex
        ComplexPojo complexPojo = new ComplexPojo();
        complexPojo.setTestSimplePojo(simplePojo);
        complexPojo.setTestSimplePojoList(Arrays.asList(simplePojo, simplePojo, simplePojo));
        complexPojo.setTestSimplePojoMap(Maps.of("1", simplePojo, "2", simplePojo, "3", simplePojo));

        jsonElementObject = JsonMapper.mapObject(complexPojo);
        assertThat(jsonElementObject).isInstanceOf(JsonObject.class);
        jsonObject = (JsonObject) jsonElementObject;

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectExpected);
        jsonArray.add(jsonObjectExpected);
        jsonArray.add(jsonObjectExpected);

        JsonObject jsonObjectMap = new JsonObject();
        jsonObjectMap.put("1", jsonObjectExpected);
        jsonObjectMap.put("2", jsonObjectExpected);
        jsonObjectMap.put("3", jsonObjectExpected);

        JsonObject jsonObjectObjectExpected = new JsonObject();
        jsonObjectObjectExpected.put("TEST_SIMPLE_POJO", jsonObjectExpected);
        jsonObjectObjectExpected.put("TEST_SIMPLE_POJO_LIST", jsonArray);
        jsonObjectObjectExpected.put("TEST_SIMPLE_POJO_MAP", jsonObjectMap);

        assertThat(jsonObject).isEqualTo(jsonObjectObjectExpected);
    }

    @Test
    void mapJsonE_object() throws IOException {
        JsonObject simpleJsonObject = new JsonObject();
        simpleJsonObject.put("TEST_LONG", new JsonLong(1L));
        simpleJsonObject.put("TEST_DOUBLE", new JsonDouble(1.1D));
        simpleJsonObject.put("TEST_STRING", new JsonString("test"));
        simpleJsonObject.put("TEST_BOOLEAN", new JsonBoolean(false));
        simpleJsonObject.put("TEST_NULL", new JsonNull());

        JsonObject complexJsonObject = new JsonObject();

        JsonArray jsonObjectArray = new JsonArray();
        jsonObjectArray.add(simpleJsonObject);
        jsonObjectArray.add(simpleJsonObject);
        jsonObjectArray.add(simpleJsonObject);
        complexJsonObject.put("TEST_ARRAY_OBJECT", jsonObjectArray);

        JsonObject jsonObjectObject = new JsonObject();
        jsonObjectObject.put("TEST_OBJECT_1", simpleJsonObject);
        jsonObjectObject.put("TEST_OBJECT_2", simpleJsonObject);
        jsonObjectObject.put("TEST_OBJECT_3", simpleJsonObject);
        complexJsonObject.put("TEST_OBJECT_OBJECT", jsonObjectObject);

        JsonArray jsonLongArray = new JsonArray();
        jsonLongArray.add(new JsonLong(1L));
        jsonLongArray.add(new JsonLong(2L));
        jsonLongArray.add(new JsonLong(3L));
        complexJsonObject.put("TEST_ARRAY_LONG", jsonLongArray);

        JsonArray jsonDoubleArray = new JsonArray();
        jsonDoubleArray.add(new JsonDouble(1.1D));
        jsonDoubleArray.add(new JsonDouble(2.2D));
        jsonDoubleArray.add(new JsonDouble(3.3D));
        complexJsonObject.put("TEST_ARRAY_DOUBLE", jsonDoubleArray);

        JsonArray jsonBooleanArray = new JsonArray();
        jsonBooleanArray.add(new JsonBoolean(true));
        jsonBooleanArray.add(new JsonBoolean(false));
        jsonBooleanArray.add(new JsonBoolean(true));
        complexJsonObject.put("TEST_ARRAY_BOOLEAN", jsonBooleanArray);

        JsonArray jsonStringArray = new JsonArray();
        jsonStringArray.add(new JsonString("test1"));
        jsonStringArray.add(new JsonString("test2"));
        jsonStringArray.add(new JsonString("test3"));
        complexJsonObject.put("TEST_ARRAY_STRING", jsonStringArray);

        JsonArray jsonNullArray = new JsonArray();
        jsonNullArray.add(new JsonNull());
        jsonNullArray.add(new JsonNull());
        jsonNullArray.add(new JsonNull());
        complexJsonObject.put("TEST_ARRAY_NULL", jsonNullArray);

        JsonE jsonE = JsonMapper.mapJson(complexJsonObject);
        String expectedJsonE = new ResourceService().read("json/JsonE.txt").orElse("");

        // compare without whitespaces
        assertThat(jsonE.toString().replaceAll("\\s+",""))
            .isEqualTo(expectedJsonE.replaceAll("\\s+",""));
    }

    @Test
    void mapObject_withJsonPropertyAnnotation() {
        AnnotationPojo annotationPojo = new AnnotationPojo();
        annotationPojo.setTestLong(1L);
        annotationPojo.setTestDouble(1.1D);
        annotationPojo.setTestString("test");
        annotationPojo.setTestBoolean(false);
        annotationPojo.setTestNull(null);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("annotationTestLong", new JsonLong(1L));
        jsonObject.put("annotationTestDouble", new JsonDouble(1.1D));
        jsonObject.put("annotationTestString", new JsonString("test"));
        jsonObject.put("annotationTestBoolean", new JsonBoolean(false));
        jsonObject.put("annotationTestNull", new JsonNull());

        // check map to json
        JsonElement jsonElement = JsonMapper.mapObject(annotationPojo);
        assertThat(jsonElement).isInstanceOf(JsonObject.class);
        JsonObject mappedJsonObject = (JsonObject) jsonElement;
        assertThat(mappedJsonObject).isEqualTo(jsonObject);

        // check map to pojo
        AnnotationPojo mappedAnnotationPojo = JsonMapper.mapJson(jsonObject, AnnotationPojo.class);
        assertThat(mappedAnnotationPojo).isEqualTo(annotationPojo);
    }

    @Data
    public static class SimplePojo {
        private Long testLong;
        private Double testDouble;
        private String testString;
        private Boolean testBoolean;
        private Void testNull;
    }

    @Data
    public static class ComplexPojo {
        private List<SimplePojo> testSimplePojoList;
        private Map<String, SimplePojo> testSimplePojoMap;
        private SimplePojo testSimplePojo;
    }

    public enum TestEnum {
        TEST1,
        TEST2,
        TEST3
    }

    @Data
    public static class AnnotationPojo {
        @JsonProperty(key = "annotationTestLong")
        private Long testLong;
        @JsonProperty(key = "annotationTestDouble")
        private Double testDouble;
        @JsonProperty(key = "annotationTestString")
        private String testString;
        @JsonProperty(key = "annotationTestBoolean")
        private Boolean testBoolean;
        @JsonProperty(key = "annotationTestNull")
        private Void testNull;
    }
}