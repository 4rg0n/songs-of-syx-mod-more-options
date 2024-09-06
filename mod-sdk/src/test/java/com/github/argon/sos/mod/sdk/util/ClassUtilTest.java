package com.github.argon.sos.mod.sdk.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClassUtilTest {

    @Test
    void instanceOf() {
        assertThat(ClassUtil.instanceOf("STRING", String.class)).isTrue();
        assertThat(ClassUtil.instanceOf("STRING", Integer.class)).isFalse();

        assertThat(ClassUtil.instanceOf("STRING", "OTHER_STRING")).isTrue();
        assertThat(ClassUtil.instanceOf("STRING", 1)).isFalse();

        assertThat(ClassUtil.instanceOf(String.class, String.class)).isTrue();
        assertThat(ClassUtil.instanceOf(String.class, Integer.class)).isFalse();
        assertThat(ClassUtil.instanceOf(null, null)).isTrue();
        assertThat(ClassUtil.instanceOf(null, String.class)).isFalse();
        assertThat(ClassUtil.instanceOf(String.class, null)).isFalse();
    }

    @Test
    void sameAs() {
        assertThat(ClassUtil.sameAs(String.class, String.class)).isTrue();
        assertThat(ClassUtil.sameAs(String.class, Integer.class)).isFalse();
    }
}