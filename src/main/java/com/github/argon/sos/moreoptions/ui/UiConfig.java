package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV4Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.FullWindow;
import com.github.argon.sos.moreoptions.game.ui.Modal;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.i18n.I18nMessages;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricCollector;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
import com.github.argon.sos.moreoptions.phase.Phase;
import com.github.argon.sos.moreoptions.phase.PhaseManager;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.phase.UninitializedException;
import com.github.argon.sos.moreoptions.ui.controller.*;
import com.github.argon.sos.moreoptions.ui.tab.advanced.AdvancedTab;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersTab;
import com.github.argon.sos.moreoptions.ui.tab.metrics.MetricsTab;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesTab;
import com.github.argon.sos.moreoptions.ui.tab.weather.WeatherTab;
import lombok.AccessLevel;
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
 * Most UI elements are generated dynamically dictated by the given config {@link MoreOptionsV4Config}.
 * So when a new entry is added, a new UI element like e.g. an additional slider will also be visible.
 * For setting up the UI and adding functionality to buttons.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UiConfig implements Phases {

    @Getter(lazy = true)
    private final static UiConfig instance = new UiConfig(
        GameApis.getInstance(),
        MoreOptionsConfigurator.getInstance(),
        ConfigStore.getInstance(),
        MetricExporter.getInstance(),
        UiFactory.getInstance(),
        Notificator.getInstance(),
        PhaseManager.getInstance()
    );

    private static final I18n i18n = I18n.get(WeatherTab.class);
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
    private final MoreOptionsConfigurator configurator;
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
    private Modal<BackupDialog> backupDialog;

    @Override
    public void initModCreateInstance() {
        initDebugControls();
    }

    /**
     * Prepare ui elements
     */
    @Override
    public void initSettlementUiPresent() {
        MoreOptionsV4Config moreOptionsConfig = configStore.getCurrentConfig();

        if (moreOptionsConfig == null) {
            throw new UninitializedException("Configuration is not initialized.");
        }

        // create More Options ui
        moreOptionsFull = uiFactory.buildMoreOptionsFullScreen(MOD_INFO.name.toString(), moreOptionsConfig);
        initControls(moreOptionsFull);
        injectIntoUITopPanel(moreOptionsFull);

        // create backup configuration dialog if needed
        configStore.getBackup().ifPresent(backupConfig -> {
            backupDialog = new Modal<>(MOD_INFO.name.toString(), new BackupDialog());
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
        IDebugPanel.add(MOD_INFO.name + ":phases:" + Phase.ON_GAME_SAVE_LOADED, () -> PhaseManager.getInstance().onGameSaveReloaded());
        IDebugPanel.add(MOD_INFO.name + ":fullScreen", () -> UiFactory.getInstance().buildMoreOptionsFullScreen("More Options", configStore.getDefaultConfig()).show());
        IDebugPanel.add(MOD_INFO.name + ":metrics:flush", () -> MetricCollector.getInstance().flush());
        IDebugPanel.add(MOD_INFO.name + ":metrics:stop", () -> MetricScheduler.getInstance().stop());
        IDebugPanel.add(MOD_INFO.name + ":metrics:start", () -> MetricScheduler.getInstance().start());
        IDebugPanel.add(MOD_INFO.name + ":i18n:load", I18nMessages.getInstance()::loadWithCurrentGameLocale);
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
        new BoostersPanelController(boostersTab);

        moreOptionsPanel.showAction(boostersTab::refresh);

        // METRICS
        MetricsTab metricsTab = moreOptionsPanel.getMetricsTab();
        new MetricsPanelController(metricsTab);

        // RACES
        RacesTab racesTab = moreOptionsPanel.getRacesTab();
        new RacesPanelController(racesTab);

        // ADVANCED
        AdvancedTab advancedTab = moreOptionsPanel.getAdvancedTab();
        phaseManager.register(Phase.ON_GAME_SAVED, new AdvancedPanelController(advancedTab, moreOptionsPanel));
    }

    public void initBackupControls(
        Modal<BackupDialog> backupDialog,
        FullWindow<MoreOptionsPanel> backupMoreOptionsModal,
        MoreOptionsPanel moreOptionsPanel
    ) {
        initControls(backupMoreOptionsModal);
        new BackupPanelController(backupMoreOptionsModal, backupDialog, moreOptionsPanel);
    }
}
