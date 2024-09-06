package com.github.argon.sos.mod.sdk.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectionUtilTest {

    @Test
    void getField() {
        assertThat(ReflectionUtil.getField("publicField", TestClass.class)).isPresent();
        assertThat(ReflectionUtil.getField("privateField", TestClass.class)).isEmpty();
        assertThat(ReflectionUtil.getField("notExistingField", TestClass.class)).isEmpty();
    }

    @Test
    void setField() throws Exception {
        TestClass testClass = new TestClass();
        ReflectionUtil.setField("privateField", testClass, "TEST_VALUE");
        ReflectionUtil.setField("publicField", testClass, "TEST_VALUE");

        assertThatThrownBy(() -> ReflectionUtil.setField("privateFinalField", testClass, "TEST_VALUE"))
            .isInstanceOf(IllegalAccessException.class);
        assertThatThrownBy(() -> ReflectionUtil.setField("doesNotExist", testClass, "TEST_VALUE"))
            .isInstanceOf(NoSuchFieldException.class);

        assertThat(testClass.publicField).isEqualTo("TEST_VALUE");
        assertThat(testClass.getPrivateField()).isEqualTo("TEST_VALUE");
    }

    @Test
    void getDeclaredField() {
        assertThat(ReflectionUtil.getDeclaredField("publicField", new TestClass())).isPresent();
        assertThat(ReflectionUtil.getDeclaredField("publicField", TestClass.class)).isPresent();
        assertThat(ReflectionUtil.getDeclaredField("doesNotExist", TestClass.class)).isEmpty();
        assertThat(ReflectionUtil.getDeclaredField("doesNotExist", new TestClass())).isEmpty();
    }

    @Test
    void getDeclaredFields() {
        List<Field> declaredFields = ReflectionUtil.getDeclaredFields(String.class, TestClass.class);

        assertThat(declaredFields).hasSize(6);
        assertThat(declaredFields)
            .allMatch(field -> Lists.of("publicField", "privateField", "privateFinalField", "publicStaticField", "privateStaticField", "privateFinalStaticField")
                .contains(field.getName()));
    }

    @Test
    void getDeclaredFieldValue() {
        assertThat(ReflectionUtil.getDeclaredFieldValue("doesNotExist", TestClass.class)).isEmpty();
        assertThat(ReflectionUtil.getDeclaredFieldValue("publicField", TestClass.class)).isEmpty();
        assertThat(ReflectionUtil.getDeclaredFieldValue("publicStaticField", TestClass.class)).hasValue("PUBLIC_STATIC");
        assertThat(ReflectionUtil.getDeclaredFieldValue("privateStaticField", TestClass.class)).hasValue("PRIVATE_STATIC");
        assertThat(ReflectionUtil.getDeclaredFieldValue("privateFinalStaticField", TestClass.class)).hasValue("PRIVATE_FINAL_STATIC");

        assertThat(ReflectionUtil.getDeclaredFieldValue("doesNotExist", new TestClass())).isEmpty();
        assertThat(ReflectionUtil.getDeclaredFieldValue("publicField", new TestClass())).hasValue("PUBLIC");
        assertThat(ReflectionUtil.getDeclaredFieldValue("privateField", new TestClass())).hasValue("PRIVATE");
        assertThat(ReflectionUtil.getDeclaredFieldValue("privateFinalField", new TestClass())).hasValue("PRIVATE_FINAL");
    }

    @Test
    void getDeclaredFieldValues() {
        List<String> declaredFieldValues = ReflectionUtil.getDeclaredFieldValues(String.class, new TestClass());
        assertThat(declaredFieldValues)
            .containsExactlyInAnyOrder("PUBLIC_STATIC", "PRIVATE_STATIC", "PRIVATE_FINAL_STATIC", "PUBLIC", "PRIVATE", "PRIVATE_FINAL");
    }

    @Test
    void getDeclaredFieldValuesMap() {
        Map<Field, String> declaredFieldValuesMap = ReflectionUtil.getDeclaredFieldValuesMap(String.class, new TestClass());

        assertThat(declaredFieldValuesMap.keySet())
            .allMatch(field -> Sets.of("publicField", "privateField", "privateFinalField", "publicStaticField", "privateStaticField", "privateFinalStaticField")
                .contains(field.getName()));
        assertThat(declaredFieldValuesMap.values())
            .containsExactlyInAnyOrder("PUBLIC_STATIC", "PRIVATE_STATIC", "PRIVATE_FINAL_STATIC", "PUBLIC", "PRIVATE", "PRIVATE_FINAL");
    }

    @Test
    void getAnnotation() throws Exception {
        assertThat(ReflectionUtil.getAnnotation(TestClass.class.getDeclaredField("privateField"), TestAnnotation.class))
            .get().isInstanceOf(TestAnnotation.class);
        assertThat(ReflectionUtil.getAnnotation(TestClass.class, TestAnnotation.class))
            .get().isInstanceOf(TestAnnotation.class);
        assertThat(ReflectionUtil.getAnnotation(TestClass.class.getDeclaredMethod("doSomething"), TestAnnotation.class))
            .get().isInstanceOf(TestAnnotation.class);
        assertThat(ReflectionUtil.getAnnotation(TestClass.class.getDeclaredMethod("doSomething" , Integer.class), TestAnnotation.class))
            .isEmpty();
    }

    @Test
    void getMethod() {
        assertThat(ReflectionUtil.getMethod(TestClass.class, "doesNotExist")).isEmpty();
        assertThat(ReflectionUtil.getMethod(TestClass.class, "doSomething")).isPresent();
    }

    @Test
    void invokeMethod() throws Exception {
        assertThat(ReflectionUtil.invokeMethod(TestClass.class.getDeclaredMethod("doSomething"), new TestClass()))
            .isEqualTo(true);
        assertThat(ReflectionUtil.invokeMethod("doSomething", new TestClass()))
            .isEqualTo(true);
        assertThat(ReflectionUtil.invokeMethod("doSomething", new TestClass(),1, 1))
            .isEqualTo(true);
    }

    @Test
    void invokeMethodOneArgument() throws Exception {
        assertThat(ReflectionUtil.invokeMethodOneArgument(TestClass.class.getDeclaredMethod("doSomething", Integer.class), new TestClass(), 1))
            .isEqualTo(true);
        assertThat(ReflectionUtil.invokeMethodOneArgument("doSomething", new TestClass(), 1))
            .isEqualTo(true);
    }

    @Test
    void getGenericClass() throws Exception {
        assertThat(ReflectionUtil.getGenericClass(TestClass.class.getDeclaredField("privateList"))).hasValue(String.class);
        assertThat(ReflectionUtil.getGenericClass(TestClass.class.getDeclaredField("privateMap"))).isEmpty();
        assertThat(ReflectionUtil.getGenericClass(TestClass.class.getDeclaredField("privateStaticField"))).isEmpty();
    }

    @Test
    void getGenericClasses() throws Exception {
        assertThat(ReflectionUtil.getGenericClasses(TestClass.class.getDeclaredField("privateList")))
            .containsExactlyInAnyOrder(String.class);
        assertThat(ReflectionUtil.getGenericClasses(TestClass.class.getDeclaredField("privateMap")))
            .contains(String.class, Integer.class);
        assertThat(ReflectionUtil.getGenericClasses(TestClass.class.getDeclaredField("privateStaticField")))
            .isEmpty();
    }

    @Test
    void getGenericTypes() throws Exception {
        assertThat(ReflectionUtil.getGenericTypes(TestClass.class.getDeclaredField("privateList")))
            .containsExactlyInAnyOrder(String.class);
        assertThat(ReflectionUtil.getGenericTypes(TestClass.class.getDeclaredField("privateMap")))
            .contains(String.class, Integer.class);
        assertThat(ReflectionUtil.getGenericTypes(TestClass.class.getDeclaredField("privateStaticField")))
            .isEmpty();
    }

    @Test
    void hasNoArgsConstructor() {
        assertThat(ReflectionUtil.hasNoArgsConstructor(TestClass.class)).isTrue();
        assertThat(ReflectionUtil.hasNoArgsConstructor(OtherTestClass.class)).isFalse();
    }

    private static class OtherTestClass {
        public OtherTestClass(String nothing) {
        }
    }

    @NoArgsConstructor
    @TestAnnotation
    private static class TestClass {

        private static String privateStaticField = "PRIVATE_STATIC";
        public static String publicStaticField = "PUBLIC_STATIC";
        private final static String privateFinalStaticField = "PRIVATE_FINAL_STATIC";

        @Getter
        @TestAnnotation
        private String privateField = "PRIVATE";
        @Getter
        private final String privateFinalField = "PRIVATE_FINAL";
        public String publicField = "PUBLIC";
        private final List<String> privateList = Lists.of("TEST1", "TEST2");
        private final Map<String, Integer> privateMap = Maps.of("TEST1", 1);

        @TestAnnotation
        public boolean doSomething() {
            return true;
        }

        public boolean doSomething(Integer value) {
            return true;
        }

        public boolean doSomething(Integer value1, Integer value2) {
            return true;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
    private @interface TestAnnotation { }
}