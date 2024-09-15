package com.github.argon.sos.moreoptions.config.domain;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MetricsConfig {
    @Builder.Default
    private boolean enabled = false;
    @Builder.Default
    private Range collectionRateSeconds = ConfigDefaults.metricCollectionRate();
    @Builder.Default
    private Range exportRateMinutes = ConfigDefaults.metricExportRate();
    @Builder.Default
    private Map<String, Boolean> stats = new HashMap<>();
}
