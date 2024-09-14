package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.game.api.GameApis;
import com.github.argon.sos.mod.sdk.ui.FullWindow;
import com.github.argon.sos.mod.sdk.ui.Window;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.UninitializedException;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.Configurator;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.mod.sdk.metric.MetricExporter;
import com.github.argon.sos.moreoptions.ui.controller.*;
import com.github.argon.sos.mod.sdk.ui.Notificator;
import com.github.argon.sos.moreoptions.ui.tab.advanced.AdvancedTab;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersTab;
import com.github.argon.sos.moreoptions.ui.tab.metrics.MetricsTab;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesTab;
import com.github.argon.sos.moreoptions.ui.tab.weather.WeatherTab;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import util.gui.misc.GButt;
import view.interrupter.IDebugPanel;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.github.argon.sos.moreoptions.MoreOptionsScript.MOD_INFO;
import static java.time.temporal.ChronoField.*;

/**
 * Most UI elements are generated dynamically dictated by the given config {@link MoreOptionsV5Config}.
 * So when a new entry is added, a new UI element like e.g. an additional slider will also be visible.
 * For setting up the UI and adding functionality to buttons.
 */
@RequiredArgsConstructor
public class UiConfig implements Phases {

    private static final I18nTranslator i18n = ModModule.i18n().get(WeatherTab.class);
    private final static Logger log = Loggers.getLogger(UiConfig.class);

    /**
     * How to display the dates used by the mod.
     * E.g. 2024-12-31 23:59:59
     */
    public static final DateTimeFormatter TIME_FORMAT;
    static {
        TIME_FORMAT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter(Locale.getDefault());
    }

    private final GameApis gameApis;
    private final Configurator configurator;
    private final ConfigStore configStore;
    private final MetricExporter metricExporter;
    private final UiFactory uiFactory;
    private final Notificator notificator;
    private final PhaseManager phaseManager;

    @Getter
    @Nullable
    private FullWindow<MoreOptionsPanel> moreOptionsFull;
    @Getter
    @Nullable
    private FullWindow<MoreOptionsPanel> backupMoreOptionsModal;
    @Getter
    @Nullable
    private Window<BackupDialog> backupDialog;

    @Override
    public void initModCreateInstance() {
        initDebugControls();
    }

    /**
     * Prepare ui elements
     */
    @Override
    public void initSettlementUiPresent() {
        MoreOptionsV5Config moreOptionsConfig = configStore.getCurrentConfig();

        if (moreOptionsConfig == null) {
            throw new UninitializedException("Configuration is not initialized.");
        }

        // create More Options ui
        moreOptionsFull = uiFactory.buildMoreOptionsFullScreen(MOD_INFO.name.toString(), moreOptionsConfig);
        initControls(moreOptionsFull);
        injectIntoUITopPanel(moreOptionsFull);

        // create backup configuration dialog if needed
        configStore.getBackup().ifPresent(backupConfig -> {
            backupDialog = UiFactory.buildBackupDialog();
            backupMoreOptionsModal = uiFactory.buildMoreOptionsFullScreen(
                MOD_INFO.name + " " + i18n.t("MoreOptionsPanel.backup.title.suffix"),
                backupConfig);

            configStore.setCurrentConfig(backupConfig);
            //noinspection DataFlowIssue
            initBackupControls(backupDialog, backupMoreOptionsModal, moreOptionsFull.getSection());
        });

        MetricsTab metricsTab = moreOptionsFull.getSection().getMetricsTab();
        // after config is applied to game
        configurator.onAfterApplyAction(config -> {
            Path exportFile = metricExporter.getExportFile();
            Path currentExportFile = metricsTab.getExportFilePath();

            // export file changed?
            if (!exportFile.equals(currentExportFile)) {
                metricsTab.refresh(exportFile);

                // don't notify for initial files
                if (currentExportFile != null) {
                    notificator.notify(i18n.t("notification.metrics.file.new", exportFile.getFileName()));
                }
            }
        });

        // inject Stats UI
        StatsUi statsUi = StatsUi.getInstance();
        injectStatsUI(statsUi);
    }

    public void injectStatsUI(StatsUi statsUi) {
        try {
            gameApis.ui().injectIntoUITopPanels(statsUi);
        } catch (Exception e) {
            log.error("Could not inject Stats ui ", e);
        }
    }

    public void injectIntoUITopPanel(FullWindow<MoreOptionsPanel> moreOptionsModal) {
        log.debug("Injecting %s buttons into game ui", MOD_INFO.name);
        GButt.ButtPanel moreOptionsButton = UiFactory.buildMoreOptionsButton(moreOptionsModal);

        try {
            gameApis.ui().injectIntoUITopPanels(moreOptionsButton);
        } catch (Exception e) {
            log.error("Could not inject %s buttons into game ui", MOD_INFO.name, e);
        }
    }

    /**
     * Debug commands are executable via the in game debug panel
     */
    public void initDebugControls() {
        log.debug("Initialize %s Debug Commands", MOD_INFO.name);
        IDebugPanel.add(MOD_INFO.name + ":uiShowRoom", () -> UiFactory.buildUiShowRoom().show());
        IDebugPanel.add(MOD_INFO.name + ":errorDialog", () -> ModModule.uiFactory().buildErrorDialog(new RuntimeException("Test Exception Message"), "Test Error Message").show());
        IDebugPanel.add(MOD_INFO.name + ":phases:" + Phase.ON_GAME_SAVE_LOADED, phaseManager::onGameSaveReloaded);
        IDebugPanel.add(MOD_INFO.name + ":fullScreen", () -> ModModule.uiFactory().buildMoreOptionsFullScreen("More Options", configStore.getDefaultConfig()).show());
        IDebugPanel.add(MOD_INFO.name + ":metrics:flush", () -> ModSdkModule.metricCollector().flush());
        IDebugPanel.add(MOD_INFO.name + ":metrics:stop", () -> ModSdkModule.metricScheduler().stop());
        IDebugPanel.add(MOD_INFO.name + ":metrics:start", () -> ModSdkModule.metricScheduler().start());
        IDebugPanel.add(MOD_INFO.name + ":log:stats", () -> {
            log.info("Events Status: %s", gameApis.events().readEventsEnabledStatus()
                .entrySet().stream().map(entry -> entry.getKey() + " enabled: " + entry.getValue() + "\n")
                .collect(Collectors.joining()));
        });
    }

    public void initControls(FullWindow<MoreOptionsPanel> moreOptionsWindow) {
        MoreOptionsPanel moreOptionsPanel = moreOptionsWindow.getSection();
        new MoreOptionsPanelController(moreOptionsPanel, moreOptionsWindow);

        // BOOSTERS
        BoostersTab boostersTab = moreOptionsPanel.getBoostersTab();
        new BoostersTabController(boostersTab);

        moreOptionsPanel.showAction(boostersTab::refresh);

        // METRICS
        MetricsTab metricsTab = moreOptionsPanel.getMetricsTab();
        new MetricsTabController(metricsTab);

        // RACES
        RacesTab racesTab = moreOptionsPanel.getRacesTab();
        new RacesTabController(racesTab);

        // ADVANCED
        AdvancedTab advancedTab = moreOptionsPanel.getAdvancedTab();
        phaseManager.register(Phase.ON_GAME_SAVED, new AdvancedTabController(advancedTab, moreOptionsPanel));
    }

    public void initBackupControls(
        Window<BackupDialog> backupDialog,
        FullWindow<MoreOptionsPanel> backupMoreOptionsModal,
        MoreOptionsPanel moreOptionsPanel
    ) {
        initControls(backupMoreOptionsModal);
        new BackupPanelController(backupMoreOptionsModal, backupDialog, moreOptionsPanel);
    }
}
