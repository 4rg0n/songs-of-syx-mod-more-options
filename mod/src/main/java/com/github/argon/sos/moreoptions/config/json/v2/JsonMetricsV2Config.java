package com.github.argon.sos.moreoptions.config.json.v2;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonMetricsV2Config {
    @Builder.Default
    private boolean enabled = false;
    @Builder.Default
    private Range collectionRateSeconds = ConfigDefaults.metricCollectionRate();
    @Builder.Default
    private Range exportRateMinutes = ConfigDefaults.metricExportRate();
    @Builder.Default
    private Set<String> stats = new HashSet<>();
}
