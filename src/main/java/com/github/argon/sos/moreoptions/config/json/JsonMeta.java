package com.github.argon.sos.moreoptions.config.json;

import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV4Config;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonMeta {
    @Builder.Default
    private int version = MoreOptionsV4Config.VERSION;
    @Builder.Default
    private String logLevel = ConfigDefaults.LOG_LEVEL.getName();
}
