package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.moreoptions.config.domain.*;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersTab;
import com.github.argon.sos.moreoptions.ui.tab.events.EventsTab;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesTab;
import game.faction.Faction;
import init.paths.ModInfo;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MoreOptionsModel {

    @NonNull
    private MoreOptionsV5Config config;
    @NonNull
    private MoreOptionsV5Config defaultConfig;

    @NonNull
    private Advanced advanced;
    @NonNull
    private Boosters boosters;
    @NonNull
    private Events events;
    @NonNull
    private Metrics metrics;
    @NonNull
    private Races races;
    @NonNull
    private Sounds sounds;
    @NonNull
    private Weather weather;
    @Nullable
    private ModInfo modInfo;

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Advanced {
        @Nullable
        private String saveStamp;
        @NonNull
        private Integer worldSeed;
        @NonNull
        private Level logLevel;
        @NonNull
        private Path logFilePath;
        private boolean logToFile;
        @NonNull
        private Level defaultLogLevel;
        @NonNull
        private ConfigMeta defaultConfig;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Boosters {
        @NonNull
        private BoostersConfig config;
        @NonNull
        private BoostersConfig defaultConfig;
        @NonNull
        private Map<Faction, List<BoostersTab.Entry>> entries;
        @NonNull
        private Map<String, BoostersConfig.BoostersPreset> presets;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Events {
        @NonNull
        private EventsConfig config;
        @NonNull
        private EventsConfig defaultConfig;

        @NonNull
        private Map<String, EventsTab.GeneralEvent> generalEvents;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Metrics {
        @NonNull
        private Path exportFolder;
        @NonNull
        private Path exportFile;
        @NonNull
        private Set<String> availableStats;
        @NonNull
        private MetricsConfig defaultConfig;
        @NonNull
        private MetricsConfig config;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Races {
        @NonNull
        private Map<String, List<RacesTab.Entry>> entries;
        @NonNull
        private RacesConfig defaultConfig;
        @NonNull
        private RacesConfig config;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Sounds {
        @NonNull
        private SoundsConfig defaultConfig;
        @NonNull
        private SoundsConfig config;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Weather {
        @NonNull
        private WeatherConfig defaultConfig;
        @NonNull
        private WeatherConfig config;
    }
}
