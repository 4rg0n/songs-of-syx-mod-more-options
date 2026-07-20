package com.github.argon.sos.mod.sdk.game.jvm;

import com.github.argon.sos.mod.testing.ModSdkExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModSdkExtension.class)
class JvmArgsTest {

    @Test
    void fromString() {
        String jvmArgsContent = """
            -Xms2G -Xmx5G
            -XX:+UseGCLogFileRotation
            -XX:NumberOfGCLogFiles=2
            """;

        JvmArgs expected = new JvmArgs(List.of(
            new JvmArgs.JvmArg(null, "-Xms2G"),
            new JvmArgs.JvmArg(null, "-Xmx5G"),
            new JvmArgs.JvmArg(null, "-XX:+UseGCLogFileRotation"),
            new JvmArgs.JvmArg("-XX:NumberOfGCLogFiles", "2")
        ));

        JvmArgs jvmArgs = JvmArgs.fromString(jvmArgsContent);
        assertThat(jvmArgs.getJvmArgs()).isEqualTo(expected.getJvmArgs());
    }
}