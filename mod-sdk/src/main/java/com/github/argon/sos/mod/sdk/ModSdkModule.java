package com.github.argon.sos.mod.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.argon.sos.mod.sdk.file.FileService;
import com.github.argon.sos.mod.sdk.file.ResourceService;
import com.github.argon.sos.mod.sdk.game.api.*;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import com.github.argon.sos.mod.sdk.i18n.I18nMessages;
import com.github.argon.sos.mod.sdk.json.*;
import com.github.argon.sos.mod.sdk.json.writer.JacksonWriter;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
import com.github.argon.sos.mod.sdk.metric.MetricCollector;
import com.github.argon.sos.mod.sdk.metric.MetricCsvWriter;
import com.github.argon.sos.mod.sdk.metric.MetricExporter;
import com.github.argon.sos.mod.sdk.metric.MetricScheduler;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.mod.sdk.phase.state.StateManager;
import com.github.argon.sos.mod.sdk.properties.PropertiesStore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModSdkModule {

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameJsonService gameJsonService = buildGameJsonService();
    private static GameJsonService buildGameJsonService() {
        return new GameJsonService(gameJsonStore());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameJsonStore gameJsonStore = buildGameJsonStore();
    private static GameJsonStore buildGameJsonStore() {
        return new GameJsonStore(fileService());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JacksonService jacksonService = buildJacksonService();
    private static JacksonService buildJacksonService() {
        return new JacksonService(new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT),
            new JacksonWriter(),
            fileService());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonEService jsonEService = buildJsonEService();
    private static JsonEService buildJsonEService() {
        return new JsonEService();
    }
    
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JasonService jasonService = buildJasonService();
    private static JasonService buildJasonService() {
        return new JasonService(
            JsonWriters.jsonEPretty(), // will write game json format as default
            fileService());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static PropertiesStore propertiesStore = buildPropertiesStore();
    private static PropertiesStore buildPropertiesStore() {
        return new PropertiesStore(resourceService());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ResourceService resourceService = buildResourceService();
    private static ResourceService buildResourceService() {
        return new ResourceService();
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static FileService fileService = buildFileService();
    private static FileService buildFileService() {
        return new FileService();
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static PhaseManager phaseManager = buildPhaseManager();
    private static PhaseManager buildPhaseManager() {
        return new PhaseManager();
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static StateManager stateManager = buildStateManager();
    private static StateManager buildStateManager() {
        return new StateManager();
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameApiModule gameApis = buildGameApis();
    private static GameApiModule buildGameApis() {
        return new GameApiModule(
            new GameEventsApi(),
            new GameSoundsApi(),
            new GameUiApi(),
            new GameWeatherApi(),
            new GameModApi(),
            new GameRaceApi(),
            new GameSaveApi(),
            new GameLangApi(),
            new GameFactionApi(),
            new GameRoomsApi(),
            new GameStatsApi(metricCollector())
        );
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18nMessages i18nMessages = buildI18nMessages();
    private static I18nMessages buildI18nMessages() {
        return new I18nMessages(gameApis().lang());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18n i18n = buildI18n();
    private static I18n buildI18n() {
        return new I18n(i18nMessages());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static MetricCollector metricCollector = buildMetricCollector();
    private static MetricCollector buildMetricCollector() {
        return new MetricCollector();
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static MetricScheduler metricScheduler = buildMetricScheduler();
    private static MetricScheduler buildMetricScheduler() {
        return new MetricScheduler();
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static MetricExporter metricExporter = buildMetricExporter();
    private static MetricExporter buildMetricExporter() {
        return new MetricExporter(metricCollector(), new MetricCsvWriter());
    }
}
