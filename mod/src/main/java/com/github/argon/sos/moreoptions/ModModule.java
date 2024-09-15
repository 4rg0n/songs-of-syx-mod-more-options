package com.github.argon.sos.moreoptions;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.config.ConfigVersionHandlers;
import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.mod.sdk.file.IOService;
import com.github.argon.sos.mod.sdk.game.api.GameFactionApi;
import com.github.argon.sos.mod.sdk.game.api.GameLangApi;
import com.github.argon.sos.mod.sdk.game.api.GameSaveApi;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import com.github.argon.sos.mod.sdk.i18n.I18nMessageBundle;
import com.github.argon.sos.mod.sdk.json.JacksonService;
import com.github.argon.sos.mod.sdk.json.JsonGameService;
import com.github.argon.sos.mod.sdk.metric.MetricCollector;
import com.github.argon.sos.mod.sdk.metric.MetricExporter;
import com.github.argon.sos.mod.sdk.metric.MetricScheduler;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.mod.sdk.phase.state.StateManager;
import com.github.argon.sos.mod.sdk.properties.PropertiesStore;
import com.github.argon.sos.mod.sdk.ui.Notificator;
import com.github.argon.sos.moreoptions.booster.BoosterService;
import com.github.argon.sos.moreoptions.config.*;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.json.JsonConfigStoreFactory;
import com.github.argon.sos.moreoptions.config.json.v2.JsonConfigV2Handler;
import com.github.argon.sos.moreoptions.config.json.v3.JsonConfigV3Handler;
import com.github.argon.sos.moreoptions.config.json.v4.JsonConfigV4Handler;
import com.github.argon.sos.moreoptions.config.json.v5.JsonConfigV5Handler;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.api.GameBoosterApi;
import com.github.argon.sos.moreoptions.ui.UiConfig;
import com.github.argon.sos.moreoptions.ui.UiFactory;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import com.github.argon.sos.moreoptions.ui.msg.Messages;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModModule {

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameApis gameApis = Factory.newGameApis(boosterService());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18nMessageBundle i18nMessages = Factory.newI18nMessages("mo-i18n", ModSdkModule.gameApis().lang());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18n i18n = Factory.newI18n(i18nMessages());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static UiMapper uiMapper = Factory.newUiMapper(ModSdkModule.gameApis(), gameApis().boosters());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static UiConfig uiConfig = Factory.newUiConfig(
        ModSdkModule.gameApis(),
        configurator(),
        configStore(),
        ModSdkModule.metricExporter(),
        uiFactory(),
        ModSdkModule.notificator(),
        ModSdkModule.phaseManager());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonConfigStoreFactory jsonConfigFactory = Factory.newConfigFactory(
        ModSdkModule.gameApis().save(),
        ModSdkModule.jacksonService(),
        ModSdkModule.jsonGameService(),
        ModSdkModule.fileService(),
        JsonConfigStoreFactory.PathsConfig.builder()
            .configFile(ConfigDefaults.CONFIG_FILE_PATH)
            .raceConfigFolder(ConfigDefaults.RACES_CONFIG_FOLDER_PATH)
            .boosterConfigFolder(ConfigDefaults.BOOSTERS_CONFIG_FOLDER_PATH)
            .build());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonConfigStore jsonConfigStore = Factory.newJsonConfigStore();


    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigVersionHandlers<MoreOptionsV5Config> configVersionHandlers = Factory.newConfigVersionHandlers(
        jsonConfigStore(),
        jsonConfigFactory());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigService configService = Factory.newConfigService(
        jsonConfigStore(),
        configVersionHandlers(),
        ModSdkModule.jsonGameService());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigDefaults configDefaults = Factory.newConfigDefaults(
        ModSdkModule.gameApis(),
        gameApis().boosters());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigStore configStore = Factory.newConfigStore(
        configService(),
        configDefaults(),
        ModSdkModule.stateManager());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static Configurator configurator = Factory.newConfigurator(
        ModSdkModule.gameApis(),
        gameApis().boosters(),
        ModSdkModule.metricCollector(),
        ModSdkModule.metricExporter(),
        ModSdkModule.metricScheduler());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigApplier configApplier = Factory.newConfigApplier(configStore(), configurator());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static UiFactory uiFactory = Factory.newUiFactory(
        ModSdkModule.gameApis(),
        configStore(),
        ModSdkModule.propertiesStore(),
        ModSdkModule.metricExporter(),
        uiMapper());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static Messages messages = Factory.newMessages(ModSdkModule.notificator(), uiFactory());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static BoosterService boosterService = Factory.newBoosterService(ModSdkModule.gameApis().faction());

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Factory {

        public static I18nMessageBundle newI18nMessages(String bundleName, GameLangApi langApi) {
            return new I18nMessageBundle(bundleName, langApi);
        }

        public static I18n newI18n(I18nMessageBundle i18NMessageBundle) {
            return new I18n(i18NMessageBundle);
        }

        public static GameApis newGameApis(BoosterService boosterService) {
            return new GameApis(new GameBoosterApi(boosterService));
        }

        public static UiMapper newUiMapper(com.github.argon.sos.mod.sdk.game.api.GameApis gameApis, GameBoosterApi boosters) {
            return new UiMapper(gameApis, boosters);
        }

        public static UiConfig newUiConfig(
            com.github.argon.sos.mod.sdk.game.api.GameApis gameApis,
            Configurator configurator,
            ConfigStore configStore,
            MetricExporter metricExporter,
            UiFactory uiFactory,
            Notificator notificator,
            PhaseManager phaseManager
        ) {
            return new UiConfig(
                gameApis,
                configurator,
                configStore,
                metricExporter,
                uiFactory,
                notificator,
                phaseManager);
        }

        public static JsonConfigStoreFactory newConfigFactory(
            GameSaveApi saveApi,
            JacksonService jacksonService,
            JsonGameService jsonGameService,
            IOService ioService,
            JsonConfigStoreFactory.PathsConfig pathsConfig
        ) {
            return new JsonConfigStoreFactory(
                saveApi,
                jacksonService,
                jsonGameService,
                ioService,
                pathsConfig);
        }

        public static JsonConfigStore newJsonConfigStore() {
            return jsonConfigFactory().newJsonConfigStoreV5();
        }

        public static ConfigVersionHandlers<MoreOptionsV5Config> newConfigVersionHandlers(
            JsonConfigStore latesJsonConfigStore,
            JsonConfigStoreFactory jsonConfigStoreFactory
        ) {
            return new ConfigVersionHandlers<MoreOptionsV5Config>()
                .register(5, new JsonConfigV5Handler(latesJsonConfigStore))
                .register(4, new JsonConfigV4Handler(jsonConfigStoreFactory.newJsonConfigStoreV4()))
                .register(3, new JsonConfigV3Handler(jsonConfigStoreFactory.newJsonConfigStoreV3()))
                .register(2, new JsonConfigV2Handler(jsonConfigStoreFactory.newJsonConfigStoreV2()));
        }

        public static ConfigService newConfigService(
            JsonConfigStore jsonConfigStore,
            ConfigVersionHandlers<MoreOptionsV5Config> versionHandlers,
            JsonGameService jsonGameService
        ) {
            return new ConfigService(
                jsonConfigStore,
                versionHandlers,
                jsonGameService);
        }

        public static ConfigDefaults newConfigDefaults(
            com.github.argon.sos.mod.sdk.game.api.GameApis gameApis,
            GameBoosterApi boosters
        ) {
            return new ConfigDefaults(
                gameApis,
                boosters);
        }

        public static ConfigStore newConfigStore(
            ConfigService configService,
            ConfigDefaults configDefaults,
            StateManager stateManager
        ) {
            return new ConfigStore(configService, configDefaults, stateManager);
        }

        public static Configurator newConfigurator(
            com.github.argon.sos.mod.sdk.game.api.GameApis gameApis,
            GameBoosterApi boosters,
            MetricCollector metricCollector,
            MetricExporter metricExporter,
            MetricScheduler metricScheduler
        ) {
            return new Configurator(
                gameApis,
                boosters,
                metricCollector,
                metricExporter,
                metricScheduler);
        }

        public static ConfigApplier newConfigApplier(ConfigStore configStore, Configurator configurator) {
            return new ConfigApplier(configStore, configurator);
        }

        public static UiFactory newUiFactory(
            com.github.argon.sos.mod.sdk.game.api.GameApis gameApis,
            ConfigStore configStore,
            PropertiesStore propertiesStore,
            MetricExporter metricExporter,
            UiMapper uiMapper
        ) {
            return new UiFactory(
                gameApis,
                configStore,
                propertiesStore,
                metricExporter,
                uiMapper);
        }

        public static Messages newMessages(Notificator notificator, UiFactory uiFactory) {
            return new Messages(notificator, uiFactory);
        }

        public static BoosterService newBoosterService(GameFactionApi gameFactionApi) {
            return new BoosterService(gameFactionApi);
        }
    }
}
