package com.github.argon.sos.mod.sdk.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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