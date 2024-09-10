package com.github.argon.sos.moreoptions;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.config.ConfigVersionHandlers;
import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import com.github.argon.sos.mod.sdk.i18n.I18nMessages;
import com.github.argon.sos.mod.sdk.properties.PropertiesStore;
import com.github.argon.sos.moreoptions.booster.BoosterService;
import com.github.argon.sos.moreoptions.config.*;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
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
import com.github.argon.sos.moreoptions.ui.msg.Notificator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModModule {

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameApis gameApis = buildGameApis();
    private static GameApis buildGameApis() {
        return new GameApis(
            new GameBoosterApi(booster()));
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static PropertiesStore propertiesStore = buildPropertiesStore();
    private static PropertiesStore buildPropertiesStore() {
        return new PropertiesStore(ModSdkModule.resourceService(), "mo-mod.properties");
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18nMessages i18nMessages = buildI18nMessages();
    private static I18nMessages buildI18nMessages() {
        return new I18nMessages("mo-messages", ModSdkModule.gameApis().lang());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18n i18n = buildI18n();
    private static I18n buildI18n() {
        return new I18n(i18nMessages());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static UiMapper uiMapper = buildUiMapper();
    private static UiMapper buildUiMapper() {
        return new UiMapper(ModSdkModule.gameApis(), gameApis().boosters());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static UiConfig uiConfig = buildUiConfig();
    private static UiConfig buildUiConfig() {
        return new UiConfig(
            ModSdkModule.gameApis(),
            moreOptionsConfigurator(),
            configStore(),
            ModSdkModule.metricExporter(),
            uiFactory(),
            notificator(),
            ModSdkModule.phaseManager()
        );
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigFactory configFactory = buildConfigFactory();
    private static ConfigFactory buildConfigFactory() {
        return new ConfigFactory(
            ModSdkModule.gameApis().save(),
            ModSdkModule.jacksonService(),
            ModSdkModule.jasonService(),
            ModSdkModule.fileService()
        );
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonConfigStore jsonConfigStore = buildJsonConfigStore();
    private static JsonConfigStore buildJsonConfigStore() {
        return configFactory().newJsonConfigStoreV5();
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigVersionHandlers<MoreOptionsV5Config> configVersionHandlers = buildConfigVersionHandlers();
    private static ConfigVersionHandlers<MoreOptionsV5Config> buildConfigVersionHandlers() {
        return new ConfigVersionHandlers<MoreOptionsV5Config>()
            .register(5, new JsonConfigV5Handler(jsonConfigStore()))
            .register(4, new JsonConfigV4Handler(configFactory().newJsonConfigStoreV4()))
            .register(3, new JsonConfigV3Handler(configFactory().newJsonConfigStoreV3()))
            .register(2, new JsonConfigV2Handler(configFactory().newJsonConfigStoreV2()));
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigService configService = buildConfigService();
    private static ConfigService buildConfigService() {
        return new ConfigService(
            jsonConfigStore(),
            configVersionHandlers(),
            ModSdkModule.jasonService()
        );
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigDefaults configDefaults = buildConfigDefaults();
    private static ConfigDefaults buildConfigDefaults() {
        return new ConfigDefaults(
            ModSdkModule.gameApis(),
            gameApis().boosters());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigStore configStore = buildConfigStore();
    private static ConfigStore buildConfigStore() {
        return new ConfigStore(configService(), configDefaults(), ModSdkModule.stateManager());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static MoreOptionsConfigurator moreOptionsConfigurator = buildMoreOptionsConfigurator();
    private static MoreOptionsConfigurator buildMoreOptionsConfigurator() {
        return new MoreOptionsConfigurator(
            ModSdkModule.gameApis(),
            gameApis().boosters(),
            ModSdkModule.metricCollector(),
            ModSdkModule.metricExporter(),
            ModSdkModule.metricScheduler());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigApplier configApplier = buildConfigApplier();
    private static ConfigApplier buildConfigApplier() {
        return new ConfigApplier(configStore(), moreOptionsConfigurator());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static UiFactory uiFactory = buildUiFactory();
    private static UiFactory buildUiFactory() {
        return new UiFactory(
            ModSdkModule.gameApis(),
            configStore(),
            propertiesStore(),
            ModSdkModule.metricExporter(),
            uiMapper());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static Notificator notificator = buildNotificator();
    private static Notificator buildNotificator() {
        return new Notificator(ModSdkModule.gameApis().ui());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static Messages messages = buildMessages();
    private static Messages buildMessages() {
        return new Messages(notificator(), uiFactory());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static BoosterService booster = buildBoosterService();
    private static BoosterService buildBoosterService() {
        return new BoosterService(ModSdkModule.gameApis().faction());
    }
}
