package com.github.argon.sos.mod.sdk.util;

import com.github.argon.sos.mod.testing.ModSdkExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModSdkExtension.class)
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
    void shortenClassName() {
        Assertions.assertThat(ClassUtil.shortenClassName(String.class)).isEqualTo("j.l.String");
    }

    @Test
    void getInnerName() {
        Assertions.assertThat(ClassUtil.getInnerName(Map.Entry.class)).isEqualTo("Map.Entry");
    }

    @Test
    void shortenPackageName() {
        Assertions.assertThat(ClassUtil.shortenPackageName("test.foo.bar")).isEqualTo("t.f.b");
        Assertions.assertThat(ClassUtil.shortenPackageName("test")).isEqualTo("t");
        Assertions.assertThat(ClassUtil.shortenPackageName("")).isEqualTo("");
        Assertions.assertThat(ClassUtil.shortenPackageName("t")).isEqualTo("t");
        Assertions.assertThat(ClassUtil.shortenPackageName(".")).isEqualTo("");
        Assertions.assertThat(ClassUtil.shortenPackageName("test.")).isEqualTo("t");
        Assertions.assertThat(ClassUtil.shortenPackageName(".test")).isEqualTo("t");
    }

    @Test
    void sameAs() {
        assertThat(ClassUtil.sameAs(String.class, String.class)).isTrue();
        assertThat(ClassUtil.sameAs(String.class, Integer.class)).isFalse();
    }
}