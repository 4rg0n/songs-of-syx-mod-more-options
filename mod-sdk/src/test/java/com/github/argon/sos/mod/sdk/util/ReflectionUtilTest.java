package com.github.argon.sos.mod.sdk.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionUtilTest {

    @Test
    void getField() {
        assertThat(ReflectionUtil.getField("publicField", TestClass.class)).isPresent();
        assertThat(ReflectionUtil.getField("privateField", TestClass.class)).isEmpty();
        assertThat(ReflectionUtil.getField("notExistingField", TestClass.class)).isEmpty();
    }

    @Test
    void setField() {
        TestClass testClass = new TestClass();

        ReflectionUtil.getDeclaredField("privateField", TestClass.class)
            .ifPresent(field -> ReflectionUtil.setField(field, testClass, "TEST_VALUE"));

        ReflectionUtil.getDeclaredField("publicField", TestClass.class)
            .ifPresent(field -> ReflectionUtil.setField(field, testClass, "TEST_VALUE"));

        assertThat(testClass.getPrivateField()).isEqualTo("TEST_VALUE");
        assertThat(testClass.publicField).isEqualTo("TEST_VALUE");

    }

    @Test
    void getDeclaredField() {
    }

    @Test
    void getDeclaredFields() {
    }

    @Test
    void getDeclaredFieldValue() {
    }

    @Test
    void getDeclaredFieldValues() {
    }

    @Test
    void getDeclaredFieldValuesMap() {
    }

    @Test
    void getAnnotation() {
    }

    @Test
    void invokeMethod() {
    }

    @Test
    void invokeMethodOneArgument() {
    }

    @Test
    void getGenericClass() {
    }

    @Test
    void getGenericClasses() {
    }

    @Test
    void getGenericTypes() {
    }

    @Test
    void hasNoArgsConstructor() {
    }

    @NoArgsConstructor
    @TestAnnotation
    private static class TestClass {

        @Getter
        @TestAnnotation
        private final String privateField = "PRIVATE";
        public final String publicField = "PUBLIC";
        private final List<String> privateList = Lists.of("TEST1", "TEST2");
        private final Map<String, Integer> privateMap = Maps.of("TEST1", 1);

        public TestClass(String nothing) {
        }

        @TestAnnotation
        public boolean doSomething() {
            return true;
        }

        public boolean doSomething(int value) {
            return true;
        }

        public boolean doSomething(int value1, int value2) {
            return true;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
    private @interface TestAnnotation { }
}