package com.github.argon.sos.moreoptions.config.domain;

import com.github.argon.sos.mod.sdk.data.ByteUnit;
import com.github.argon.sos.mod.sdk.game.jvm.JvmArgs;
import com.github.argon.sos.mod.sdk.util.ArgsUtil;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmConfig {
    @Builder.Default
    private ByteUnit maxMemoryMb = ConfigDefaults.MAX_MEMORY_MB;
    @Builder.Default
    private ByteUnit minMemoryMb = ConfigDefaults.MIN_MEMORY_MB;
    @Nullable
    private JvmArgs jvmArgs;

    public static JvmConfig from(JvmArgs jvmArgs) {
        JvmConfigBuilder jvmConfigBuilder = JvmConfig.builder();

        // min memory
        List<JvmArgs.JvmArg> xmsArgs = jvmArgs.getByValueContains("-Xms");
        if (!xmsArgs.isEmpty()) {
            JvmArgs.JvmArg xmsArg = xmsArgs.getLast();
            ArgsUtil.extractByteUnit(xmsArg.getValue()).ifPresent(byteUnit -> {
                jvmConfigBuilder.minMemoryMb(byteUnit.to(ByteUnit.Unit.MEGABYTE));
            });
        }

        // max memory
        List<JvmArgs.JvmArg> xmxArgs = jvmArgs.getByValueContains("-Xmx");
        if (!xmxArgs.isEmpty()) {
            JvmArgs.JvmArg xmxArg = xmxArgs.getLast();
            ArgsUtil.extractByteUnit(xmxArg.getValue()).ifPresent(byteUnit -> {
                jvmConfigBuilder.maxMemoryMb(byteUnit.to(ByteUnit.Unit.MEGABYTE));
            });
        }

        return jvmConfigBuilder
            .jvmArgs(jvmArgs)
            .build();
    }
}