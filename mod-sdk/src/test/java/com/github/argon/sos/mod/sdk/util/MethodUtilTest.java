package com.github.argon.sos.mod.sdk.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MethodUtilTest {

    @Test
    void isGetterMethod() throws Exception {
        assertThat(MethodUtil.isGetterMethod(TestClass.class.getMethod("getNumber"))).isTrue();
        assertThat(MethodUtil.isGetterMethod(TestClass.class.getMethod("get"))).isTrue();
        assertThat(MethodUtil.isGetterMethod(TestClass.class.getMethod("is"))).isTrue();
        assertThat(MethodUtil.isGetterMethod(TestClass.class.getMethod("isEnabled"))).isTrue();

        assertThat(MethodUtil.isGetterMethod(TestClass.class.getMethod("doSomething"))).isFalse();
        assertThat(MethodUtil.isGetterMethod(TestClass.class.getMethod("isNumber", int.class))).isFalse();
    }

    @Test
    void isSetterMethod() throws Exception {
        assertThat(MethodUtil.isSetterMethod(TestClass.class.getMethod("setNumber", int.class))).isTrue();
        assertThat(MethodUtil.isSetterMethod(TestClass.class.getMethod("set", int.class))).isTrue();

        assertThat(MethodUtil.isSetterMethod(TestClass.class.getMethod("doSomething"))).isFalse();
        assertThat(MethodUtil.isSetterMethod(TestClass.class.getMethod("setNothing"))).isFalse();
    }

    @Test
    void extractSetterGetterFieldName() throws Exception{
        assertThat(MethodUtil.extractSetterGetterFieldName(TestClass.class.getMethod("setNothing"))).isEqualTo("nothing");
        assertThat(MethodUtil.extractSetterGetterFieldName(TestClass.class.getMethod("getNumber"))).isEqualTo("number");
        assertThat(MethodUtil.extractSetterGetterFieldName(TestClass.class.getMethod("isNumber", int.class))).isEqualTo("number");
        assertThat(MethodUtil.extractSetterGetterFieldName(TestClass.class.getMethod("get"))).isEqualTo("");
        assertThat(MethodUtil.extractSetterGetterFieldName(TestClass.class.getMethod("set", int.class))).isEqualTo("");
        assertThat(MethodUtil.extractSetterGetterFieldName(TestClass.class.getMethod("is"))).isEqualTo("");
    }

    private static class TestClass {

        public boolean isNumber(int number) {
            return true;
        }

        public void setNothing() {}
        public int getNumber() {
            return 1;
        }

        public int get() {
            return 1;
        }

        public boolean isEnabled() {
            return false;
        }

        public boolean is() {
            return false;
        }

        public void setNumber(int number) {}

        public void set(int number) {}


        public void doSomething() {}
    }
}