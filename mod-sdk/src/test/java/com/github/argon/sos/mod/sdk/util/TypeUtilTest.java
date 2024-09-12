package com.github.argon.sos.mod.sdk.util;

import com.github.argon.sos.mod.sdk.testing.ModSdkExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(ModSdkExtension.class)
class TypeUtilTest {

    @Test
    void getRawType() throws Exception {
        Assertions.assertThat(TypeUtil.getRawType(String.class)).isEqualTo(String.class);
        Assertions.assertThat(TypeUtil.getRawType(TestClass.class.getDeclaredField("list").getGenericType()))
            .isEqualTo(List.class);
    }

    @Test
    void isAssignableFrom() throws Exception {
        Assertions.assertThat(TypeUtil.isAssignableFrom(TestClass.class.getDeclaredField("list").getType(), List.class)).isTrue();
        Assertions.assertThat(TypeUtil.isAssignableFrom(TestClass.class.getDeclaredField("list").getType(), String.class)).isFalse();
    }

    @Test
    void sameAs() throws Exception {
        Assertions.assertThat(TypeUtil.sameAs(
                TestClass.class.getDeclaredField("list").getType(),
                TestClass.class.getDeclaredField("string").getType()))
            .isFalse();

        Assertions.assertThat(TypeUtil.sameAs(
                TestClass.class.getDeclaredField("list").getType(),
                TestClass.class.getDeclaredField("anotherList").getType()))
            .isTrue();

        Assertions.assertThat(TypeUtil.sameAs(
                TestClass.class.getDeclaredField("list").getType(),
                TestClass.class.getDeclaredField("yetAnotherList").getType()))
            .isTrue();
    }

    private static class TestClass {
        public List<String> list;

        public String string;

        public List<String> anotherList;

        public List<Integer> yetAnotherList;
    }
}