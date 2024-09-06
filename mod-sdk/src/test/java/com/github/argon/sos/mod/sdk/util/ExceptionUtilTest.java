package com.github.argon.sos.mod.sdk.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionUtilTest {

    @Test
    void contains() {
        RuntimeException needle = new RuntimeException("TEST1");
        RuntimeException haystack = new RuntimeException("TEST2", needle);

        assertThat(ExceptionUtil.contains(haystack, needle)).isTrue();
        assertThat(ExceptionUtil.contains(new RuntimeException("TEST3"), needle)).isFalse();
    }

    @Test
    void extractThrowableLast() {
        RuntimeException expected = new RuntimeException("TEST1");
        Object[] argsValid = new Object[]{"TEST", 1, expected};
        Object[] argsInvalid = new Object[]{"TEST", 1, 2.0D};

        assertThat(ExceptionUtil.extractThrowableLast(argsValid)).isEqualTo(expected);
        assertThat(ExceptionUtil.extractThrowableLast(argsInvalid)).isNull();
    }
}