package com.github.argon.sos.moreoptions.config.domain;

import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMeta {
    @Builder.Default
    private int version = MoreOptionsV4Config.VERSION;
    @Builder.Default
    private Level logLevel = Loggers.LOG_LEVEL_DEFAULT;
}
