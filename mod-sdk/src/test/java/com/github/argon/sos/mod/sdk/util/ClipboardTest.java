package com.github.argon.sos.mod.sdk.util;

import com.github.argon.sos.mod.sdk.testing.ModSdkExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModSdkExtension.class)
class ClipboardTest {

    /**
     * This thing may not run in a build environment
     */
    @Test
    void writeAndRead() {
        Clipboard.write("TEST");
        assertThat(Clipboard.read()).hasValue("TEST");

        Clipboard.write(null);
        assertThat(Clipboard.read()).hasValue("TEST");
    }
}