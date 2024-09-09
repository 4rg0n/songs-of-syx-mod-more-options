package com.github.argon.sos.moreoptions;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.i18n.I18n;
import com.github.argon.sos.mod.sdk.i18n.I18nMessages;
import com.github.argon.sos.mod.sdk.properties.PropertiesStore;
import com.github.argon.sos.moreoptions.booster.BoosterService;
import com.github.argon.sos.moreoptions.config.*;
import com.github.argon.sos.moreoptions.game.api.GameApiModule;
import com.github.argon.sos.moreoptions.metric.MetricCollector;
import com.github.argon.sos.moreoptions.metric.MetricCsvWriter;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
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
        return new UiMapper(ModSdkModule.gameApis(), GameApiModule.boosters());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static UiConfig uiConfig = buildUiConfig();
    private static UiConfig buildUiConfig() {
        return new UiConfig(
            ModSdkModule.gameApis(),
            moreOptionsConfigurator(),
            configStore(),
            metricExporter(),
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
    private final static ConfigService configService = buildConfigService();
    private static ConfigService buildConfigService() {
        return new ConfigService(configFactory(), ModSdkModule.jasonService());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigDefaults configDefaults = buildConfigDefaults();
    private static ConfigDefaults buildConfigDefaults() {
        return new ConfigDefaults(
            ModSdkModule.gameApis(),
            GameApiModule.boosters(),
            GameApiModule.stats());
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
            GameApiModule.boosters(),
            metricCollector(),
            metricExporter(),
            metricScheduler());
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
            GameApiModule.stats(),
            configStore(),
            propertiesStore(),
            metricExporter(),
            uiMapper());
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
