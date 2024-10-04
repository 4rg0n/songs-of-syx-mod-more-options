package com.github.argon.sos.moreoptions.config.domain;

import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMeta {
    @Builder.Default
    private int version = MoreOptionsV5Config.VERSION;
    @Builder.Default
    private Level logLevel = Loggers.LOG_LEVEL_DEFAULT;
    @Builder.Default
    private boolean logToFile = false;
}
