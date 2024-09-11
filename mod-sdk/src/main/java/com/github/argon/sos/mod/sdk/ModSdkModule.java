package com.github.argon.sos.mod.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.github.argon.sos.mod.sdk.file.FileService;
import com.github.argon.sos.mod.sdk.file.IOService;
import com.github.argon.sos.mod.sdk.file.ResourceService;
import com.github.argon.sos.mod.sdk.game.api.*;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import com.github.argon.sos.mod.sdk.i18n.I18nMessages;
import com.github.argon.sos.mod.sdk.json.*;
import com.github.argon.sos.mod.sdk.json.writer.JacksonWriter;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriter;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
import com.github.argon.sos.mod.sdk.metric.MetricCollector;
import com.github.argon.sos.mod.sdk.metric.MetricCsvWriter;
import com.github.argon.sos.mod.sdk.metric.MetricExporter;
import com.github.argon.sos.mod.sdk.metric.MetricScheduler;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.mod.sdk.phase.state.StateManager;
import com.github.argon.sos.mod.sdk.properties.PropertiesService;
import com.github.argon.sos.mod.sdk.properties.PropertiesStore;
import com.github.argon.sos.mod.sdk.ui.Notificator;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.nio.file.Path;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModSdkModule {

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameJsonService gameJsonService = Factory.newGameJsonService(gameJsonStore());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameJsonStore gameJsonStore = Factory.newGameJsonStore(fileService());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JavaPropsMapper jacksonPropertiesMapper = Factory.newJavaPropsMapper();

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ObjectMapper jacksonJsonMapper = Factory.newJacksonObjectMapper();

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JacksonService jacksonService = Factory.newJacksonService(
        jacksonJsonMapper(),
        fileService());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonEService jsonEService = Factory.newJsonEService();

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonGameService jsonGameService = Factory.newJsonGameService(
        JsonWriters.jsonEPretty(), // will write game json format as default
        fileService());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static PropertiesService propertiesService = Factory.newPropertiesService(
        resourceService(),
        jacksonPropertiesMapper());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static PropertiesStore propertiesStore = Factory.newPropertiesStore(propertiesService());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ResourceService resourceService = Factory.newResourceService();

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static FileService fileService = Factory.newFileService();

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static PhaseManager phaseManager = Factory.newPhaseManager();

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static StateManager stateManager = Factory.newStateManager();

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameApis gameApis = Factory.newGameApis(metricCollector());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18nMessages i18nMessages = Factory.newI18nMessages(gameApis().lang());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18n i18n = Factory.newI18n(i18nMessages());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static MetricCollector metricCollector = Factory.newMetricCollector();

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static MetricScheduler metricScheduler = Factory.newMetricScheduler();

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static MetricExporter metricExporter = Factory.newMetricExporter(
        PATHS.local().PROFILE.get().resolve("/Metric Exports"),
        metricCollector());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static Notificator notificator = Factory.newNotificator(ModSdkModule.gameApis().ui());

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Factory {
        public static GameJsonService newGameJsonService(GameJsonStore gameJsonStore) {
            return new GameJsonService(gameJsonStore);
        }

        public static GameJsonStore newGameJsonStore(IOService ioService) {
            return new GameJsonStore(ioService);
        }

        public static JavaPropsMapper newJavaPropsMapper() {
            return new JavaPropsMapper();
        }

        public static ObjectMapper newJacksonObjectMapper() {
            return new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        }

        public static JacksonService newJacksonService(
            ObjectMapper objectMapper,
            IOService ioService
        ) {
            return new JacksonService(
                objectMapper,
                new JacksonWriter(),
                ioService);
        }

        public static JsonEService newJsonEService() {
            return new JsonEService();
        }

        public static JsonGameService newJsonGameService(JsonWriter jsonWriter, IOService ioService) {
            return new JsonGameService(
                jsonWriter,
                ioService);
        }

        public static PropertiesService newPropertiesService(IOService ioService, JavaPropsMapper javaPropsMapper) {
            return new PropertiesService(ioService, javaPropsMapper);
        }

        public static PropertiesStore newPropertiesStore(PropertiesService propertiesService) {
            return new PropertiesStore(propertiesService);
        }

        public static ResourceService newResourceService() {
            return new ResourceService();
        }

        public static FileService newFileService() {
            return new FileService();
        }

        public static PhaseManager newPhaseManager() {
            return new PhaseManager();
        }

        public static GameApis newGameApis(MetricCollector metricCollector) {
            return new GameApis(
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
                new GameStatsApi(metricCollector)
            );
        }

        public static I18nMessages newI18nMessages(GameLangApi langApi) {
            return new I18nMessages(langApi);
        }

        public static I18n newI18n(I18nMessages i18nMessages) {
            return new I18n(i18nMessages);
        }

        public static MetricCollector newMetricCollector() {
            return new MetricCollector();
        }

        public static MetricScheduler newMetricScheduler() {
            return new MetricScheduler();
        }

        public static MetricExporter newMetricExporter(Path exportFolder, MetricCollector metricCollector) {
            return new MetricExporter(exportFolder, metricCollector, new MetricCsvWriter());
        }

        public static Notificator newNotificator(GameUiApi ui) {
            return new Notificator(ui);
        }

        public static StateManager newStateManager() {
            return new StateManager();
        }
    }
}
