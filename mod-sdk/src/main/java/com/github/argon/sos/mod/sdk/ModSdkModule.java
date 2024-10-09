package com.github.argon.sos.mod.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.mod.sdk.data.save.GameSaver;
import com.github.argon.sos.mod.sdk.data.save.GameSavers;
import com.github.argon.sos.mod.sdk.file.FileService;
import com.github.argon.sos.mod.sdk.file.IOService;
import com.github.argon.sos.mod.sdk.file.ResourceService;
import com.github.argon.sos.mod.sdk.game.api.*;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import com.github.argon.sos.mod.sdk.i18n.I18nMessageBundle;
import com.github.argon.sos.mod.sdk.json.*;
import com.github.argon.sos.mod.sdk.json.store.JsonStore;
import com.github.argon.sos.mod.sdk.json.store.JsonStoreManager;
import com.github.argon.sos.mod.sdk.json.store.filepath.*;
import com.github.argon.sos.mod.sdk.json.writer.JacksonWriter;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriter;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.writer.FileLogWriter;
import com.github.argon.sos.mod.sdk.metric.MetricCollector;
import com.github.argon.sos.mod.sdk.metric.MetricCsvWriter;
import com.github.argon.sos.mod.sdk.metric.MetricExporter;
import com.github.argon.sos.mod.sdk.metric.MetricScheduler;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.mod.sdk.phase.state.StateManager;
import com.github.argon.sos.mod.sdk.properties.PropertiesService;
import com.github.argon.sos.mod.sdk.properties.PropertiesStore;
import com.github.argon.sos.mod.sdk.ui.Notificator;
import com.github.argon.sos.mod.sdk.ui.controller.Controllers;
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
     * See: {@link Controllers}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static Controllers controllers = Factory.newControllers();

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
     * For managing JSON config files
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
        JsonWriters.jsonEPretty(), // will write game json format as default
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
     * For saving data related to save games
     * See: {@link GameSaver}
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameSaver gameSaver = gameSaver("mods");

    public static GameSaver gameSaver(String folderName) {
        Path path = PATHS.local().SAVE.get().resolve(folderName);
        GameSaver saver = GameSavers.getSaver(path);
        phaseManager().register(Phase.ON_GAME_SAVE_LOADED, saver);

        return saver;
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static SimpleFilePathGenerator simpleFilePathGenerator = Factory.newSimpleFilePathGenerator();

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static SaveFilePathGenerator saveFilePathGenerator = Factory.newSaveFilePathGenerator(gameApis().save());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonStoreManager jsonStoreManager = Factory.newJsonStoreManager(
        simpleFilePathGenerator(),
        Factory.newJsonStore(jacksonService())
    );


    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonStoreManager jsonSaveStoreManager = Factory.newJsonStoreManager(
        saveFilePathGenerator(),
        Factory.newJsonStore(jacksonService())
    );

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Factory {

        public static Controllers newControllers() {
            return new Controllers();
        }
        public static GameJsonService newGameJsonService(GameJsonStore gameJsonStore) {
            return new GameJsonService(gameJsonStore);
        }

        public static GameJsonStore newGameJsonStore(IOService ioService) {
            return new GameJsonStore(ioService);
        }

        public static JsonConfigStore newJsonConfigStore(GameSaveApi saveApi, JsonService jsonService, IOService ioService) {
            return new JsonConfigStore(saveApi, jsonService, ioService);
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
                new GameStatsApi(metricCollector),
                new GameAnimalsApi()
            );
        }

        public static I18nMessageBundle newI18nMessages(GameLangApi langApi) {
            return new I18nMessageBundle(langApi);
        }

        public static I18n newI18n(I18nMessageBundle i18NMessageBundle) {
            return new I18n(i18NMessageBundle);
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

        public static FileLogWriter newFileLogWriter(Path logFile) {
            return new FileLogWriter(Logger.PREFIX_MOD, Logger.LOG_MSG_FORMAT, logFile);
        }

        public static GameSaver newGameSaver(Path path) {
            return new GameSaver(path, jsonSaveStoreManager());
        }

        public static JsonStoreManager newJsonStoreManager(
            FilePathGenerator filePathGenerator,
            JsonStore jsonStore
        ) {
            return new JsonStoreManager(filePathGenerator, jsonStore);
        }

        public static JsonStore newJsonStore(JsonService jsonService) {
            return new JsonStore(jsonService);
        }

        public static SimpleFilePathGenerator newSimpleFilePathGenerator() {
            return new SimpleFilePathGenerator();
        }

        public static SaveFilePathGenerator newSaveFilePathGenerator(GameSaveApi saveApi) {
            return new SaveFilePathGenerator(saveApi);
        }
    }
}
