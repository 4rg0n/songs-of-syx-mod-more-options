package com.github.argon.sos.moreoptions.config.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonMeta {
    @Builder.Default
    private int version = MoreOptionsV5Config.VERSION;
    @Builder.Default
    private String logLevel = ConfigDefaults.LOG_LEVEL.getName();
    @Builder.Default
    private boolean logToFile = false;
}
