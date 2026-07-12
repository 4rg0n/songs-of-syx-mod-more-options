package com.github.argon.sos.mod.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.mod.sdk.file.FileService;
import com.github.argon.sos.mod.sdk.file.IOService;
import com.github.argon.sos.mod.sdk.file.ResourceService;
import com.github.argon.sos.mod.sdk.game.api.*;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import com.github.argon.sos.mod.sdk.i18n.I18nMessageBundle;
import com.github.argon.sos.mod.sdk.json.*;
import com.github.argon.sos.mod.sdk.json.writer.JacksonWriter;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriter;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.writer.FileLogWriter;
import com.github.argon.sos.mod.sdk.metric.MetricCollector;
import com.github.argon.sos.mod.sdk.metric.MetricCsvWriter;
import com.github.argon.sos.mod.sdk.metric.MetricExporter;
import com.github.argon.sos.mod.sdk.metric.MetricScheduler;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.mod.sdk.phase.state.StateManager;
import com.github.argon.sos.mod.sdk.properties.PropertiesService;
import com.github.argon.sos.mod.sdk.properties.PropertiesStore;
import com.github.argon.sos.mod.sdk.ui.notification.Notificator;
import com.github.argon.sos.mod.sdk.ui.controller.UiControllers;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.nio.file.Path;

/**
 * Main access point for different features of the mod sdk.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModSdkModule {

    /**
     * For managing controllers
     * See: {@link UiControllers}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static UiControllers uiControllers = Factory.newControllers();

    /**
     * For reading GAME JSON config
     * See: {@link GameJsonService}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameJsonService gameJsonService = Factory.newGameJsonService(gameJsonStore());

    /**
     * For caching and accessing GAME JSON config
     * See: {@link GameJsonStore}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameJsonStore gameJsonStore = Factory.newGameJsonStore(fileService());

    /**
     * For managing json config files
     * See: {@link JsonConfigStore}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonConfigStore jsonConfigStore = Factory.newJsonConfigStore(
        gameApis().save(),
        jacksonService(),
        fileService());

    /**
     * For parsing Java properties
     * See: {@link JavaPropsMapper}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JavaPropsMapper jacksonPropertiesMapper = Factory.newJavaPropsMapper();

    /**
     * For parsing JSON
     * See: {@link JavaPropsMapper}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ObjectMapper jacksonJsonMapper = Factory.newJacksonObjectMapper();

    /**
     * For reading and writing JSON
     * See: {@link JacksonService}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JacksonService jacksonService = Factory.newJacksonService(
        jacksonJsonMapper(),
        fileService());

    /**
     * For reading and writing GAME JSON with the vanilla parser
     * See: {@link JsonEService}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonEService jsonEService = Factory.newJsonEService();

    /**
     * For reading and writing GAME JSON with a custom parser
     * See: {@link JsonGameService}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonGameService jsonGameService = Factory.newJsonGameService(
        JsonWriters.gameJsonUnquotedPretty(), // will write game json format as default
        fileService());

    /**
     * For reading Java properties
     * See: {@link PropertiesService}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static PropertiesService propertiesService = Factory.newPropertiesService(
        resourceService(),
        jacksonPropertiesMapper());

    /**
     * For caching and accessing Java properties
     * See: {@link PropertiesStore}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static PropertiesStore propertiesStore = Factory.newPropertiesStore(propertiesService());

    /**
     * For reading files from within a *.jar file
     * See: {@link ResourceService}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ResourceService resourceService = Factory.newResourceService();

    /**
     * For reading, writing and deleting files
     * See: {@link FileService}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static FileService fileService = Factory.newFileService();

    /**
     * For executing code in classes when the game is in a certain phase e.g. "on game save"
     * See: {@link PhaseManager}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static PhaseManager phaseManager = Factory.newPhaseManager();

    /**
     * Holds certain information about the current game e.g., whether if it's a new game
     * See: {@link StateManager}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static StateManager stateManager = Factory.newStateManager();

    /**
     * Provides access to game functionality and data
     * See: {@link GameApis}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameApis gameApis = Factory.newGameApis(metricCollector());

    /**
     * Holds and loads texts for translations
     * See: {@link I18nMessageBundle}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18nMessageBundle i18nMessages = Factory.newI18nMessages(gameApis().lang());

    /**
     * Holds and creates translators
     * See: {@link I18nMessageBundle}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18n i18n = Factory.newI18n(i18nMessages());

    /**
     * For collecting game stats as metrics
     * See: {@link MetricCollector}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static MetricCollector metricCollector = Factory.newMetricCollector();

    /**
     * For scheduling metric collections and exports
     * See: {@link MetricScheduler}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static MetricScheduler metricScheduler = Factory.newMetricScheduler();

    /**
     * For writing metrics into a CSV file
     * See: {@link MetricExporter}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static MetricExporter metricExporter = Factory.newMetricExporter(
        PATHS.local().PROFILE.get().resolve("/Metric Exports"),
        metricCollector());

    /**
     * For showing a small message box in the right-hand corner
     * See: {@link Notificator}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static Notificator notificator = Factory.newNotificator(ModSdkModule.gameApis().ui());

    /**
     * Creates the actual instances of the components managed by {@link ModSdkModule}.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Factory {

        /**
         * Creates the default {@link UiControllers}.
         *
         * @return new controllers instance
         */
        public static UiControllers newControllers() {
            return new UiControllers();
        }

        /**
         * Creates a new {@link GameJsonService} for the given {@link GameJsonStore}.
         *
         * @param gameJsonStore to use for reading/writing game json configs
         * @return new game json service
         */
        public static GameJsonService newGameJsonService(GameJsonStore gameJsonStore) {
            return new GameJsonService(gameJsonStore);
        }

        /**
         * Creates a new {@link GameJsonStore} using the given {@link IOService}.
         *
         * @param ioService to use for reading/writing files
         * @return new game json store
         */
        public static GameJsonStore newGameJsonStore(IOService ioService) {
            return new GameJsonStore(ioService);
        }

        /**
         * Creates a new {@link JsonConfigStore} wired up with the given save API and services.
         *
         * @param saveApi to use for locating save folders
         * @param jsonService to use for reading/writing json
         * @param ioService to use for reading/writing files
         * @return new json config store
         */
        public static JsonConfigStore newJsonConfigStore(GameSaveApi saveApi, JsonService jsonService, IOService ioService) {
            return new JsonConfigStore(saveApi, jsonService, ioService);
        }

        /**
         * Creates a new {@link JavaPropsMapper} for parsing Java properties.
         *
         * @return new java props mapper
         */
        public static JavaPropsMapper newJavaPropsMapper() {
            return new JavaPropsMapper();
        }

        /**
         * Creates a new {@link ObjectMapper} configured for pretty printed JSON.
         *
         * @return new object mapper
         */
        public static ObjectMapper newJacksonObjectMapper() {
            return new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        }

        /**
         * Creates a new {@link JacksonService} for reading and writing JSON.
         *
         * @param objectMapper to use for mapping json
         * @param ioService to use for reading/writing files
         * @return new jackson service
         */
        public static JacksonService newJacksonService(
            ObjectMapper objectMapper,
            IOService ioService
        ) {
            return new JacksonService(
                objectMapper,
                new JacksonWriter(),
                ioService);
        }

        /**
         * Creates a new {@link JsonEService} for reading and writing GAME JSON with the vanilla parser.
         *
         * @return new json e service
         */
        public static JsonEService newJsonEService() {
            return new JsonEService();
        }

        /**
         * Creates a new {@link JsonGameService} using the given writer and {@link IOService}.
         *
         * @param jsonWriter to use for writing json
         * @param ioService to use for reading/writing files
         * @return new json game service
         */
        public static JsonGameService newJsonGameService(JsonWriter jsonWriter, IOService ioService) {
            return new JsonGameService(
                jsonWriter,
                ioService);
        }

        /**
         * Creates a new {@link PropertiesService} for reading Java properties.
         *
         * @param ioService to use for reading files
         * @param javaPropsMapper to use for mapping properties
         * @return new properties service
         */
        public static PropertiesService newPropertiesService(IOService ioService, JavaPropsMapper javaPropsMapper) {
            return new PropertiesService(ioService, javaPropsMapper);
        }

        /**
         * Creates a new {@link PropertiesStore} for caching and accessing Java properties.
         *
         * @param propertiesService to use for reading properties
         * @return new properties store
         */
        public static PropertiesStore newPropertiesStore(PropertiesService propertiesService) {
            return new PropertiesStore(propertiesService);
        }

        /**
         * Creates a new {@link ResourceService} for reading files from within a *.jar file.
         *
         * @return new resource service
         */
        public static ResourceService newResourceService() {
            return new ResourceService();
        }

        /**
         * Creates a new {@link FileService} for reading, writing and deleting files.
         *
         * @return new file service
         */
        public static FileService newFileService() {
            return new FileService();
        }

        /**
         * Creates a new {@link PhaseManager} for executing code on certain game phases.
         *
         * @return new phase manager
         */
        public static PhaseManager newPhaseManager() {
            return new PhaseManager();
        }

        /**
         * Creates a new {@link GameApis} wired up with all game API implementations and the given {@link MetricCollector}.
         *
         * @param metricCollector to collect game stats with
         * @return new game apis
         */
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
                new GameStatsApi(metricCollector),
                new GameAnimalsApi()
            );
        }

        /**
         * Creates a new {@link I18nMessageBundle} loading its texts through the given {@link GameLangApi}.
         *
         * @param langApi to load the language files with
         * @return new i18n message bundle
         */
        public static I18nMessageBundle newI18nMessages(GameLangApi langApi) {
            return new I18nMessageBundle(langApi);
        }

        /**
         * Creates a new {@link I18n} using the given {@link I18nMessageBundle}.
         *
         * @param i18NMessageBundle to translate messages with
         * @return new i18n instance
         */
        public static I18n newI18n(I18nMessageBundle i18NMessageBundle) {
            return new I18n(i18NMessageBundle);
        }

        /**
         * Creates a new {@link MetricCollector} for collecting game stats as metrics.
         *
         * @return new metric collector
         */
        public static MetricCollector newMetricCollector() {
            return new MetricCollector();
        }

        /**
         * Creates a new {@link MetricScheduler} for scheduling metric collections and exports.
         *
         * @return new metric scheduler
         */
        public static MetricScheduler newMetricScheduler() {
            return new MetricScheduler();
        }

        /**
         * Creates a new {@link MetricExporter} writing metrics into the given export folder.
         *
         * @param exportFolder to write metric exports into
         * @param metricCollector to export the collected metrics of
         * @return new metric exporter
         */
        public static MetricExporter newMetricExporter(Path exportFolder, MetricCollector metricCollector) {
            return new MetricExporter(exportFolder, metricCollector, new MetricCsvWriter());
        }

        /**
         * Creates a new {@link Notificator} using the given {@link GameUiApi}.
         *
         * @param ui to display notifications with
         * @return new notificator
         */
        public static Notificator newNotificator(GameUiApi ui) {
            return new Notificator(ui);
        }

        /**
         * Creates a new {@link StateManager} for holding information about the current game.
         *
         * @return new state manager
         */
        public static StateManager newStateManager() {
            return new StateManager();
        }

        /**
         * Creates a new {@link FileLogWriter} writing log messages into the given file.
         *
         * @param logFile to write log messages into
         * @return new file log writer
         */
        public static FileLogWriter newFileLogWriter(Path logFile) {
            return new FileLogWriter(Logger.PREFIX_MOD, Logger.LOG_MSG_FORMAT, logFile);
        }
    }
}
