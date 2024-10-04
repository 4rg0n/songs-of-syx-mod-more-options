package com.github.argon.sos.mod.sdk.util;

import com.github.argon.sos.mod.testing.ModSdkExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModSdkExtension.class)
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