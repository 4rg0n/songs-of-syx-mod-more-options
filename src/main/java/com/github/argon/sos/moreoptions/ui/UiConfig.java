package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.Notificator;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.JsonConfigMapper;
import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.Modal;
import com.github.argon.sos.moreoptions.game.ui.Window;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.JsonMapper;
import com.github.argon.sos.moreoptions.json.JsonWriter;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricCollector;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
import com.github.argon.sos.moreoptions.ui.panel.MetricsPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesSelectionPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesPanel;
import com.github.argon.sos.moreoptions.ui.panel.WeatherPanel;
import com.github.argon.sos.moreoptions.util.Clipboard;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import init.sprite.SPRITES;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.DIR;
import snake2d.util.file.FileManager;
import snake2d.util.file.JsonE;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;
import view.interrupter.IDebugPanel;
import view.main.VIEW;
import view.ui.top.UIPanelTop;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.argon.sos.moreoptions.MoreOptionsScript.MOD_INFO;
import static java.time.temporal.ChronoField.*;

/**
 * Most UI elements are generated dynamically dictated by the given config {@link MoreOptionsV2Config}.
 * So when a new entry is added, a new UI element like e.g. an additional slider will also be visible.
 * For setting up the UI.
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
        Notificator.getInstance()
    );

    private static final I18n i18n = I18n.get(WeatherPanel.class);
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

    @Getter
    @Nullable
    private Modal<MoreOptionsPanel> moreOptionsModal;
    @Getter
    @Nullable
    private Modal<MoreOptionsPanel> backupMoreOptionsModal;
    @Getter
    @Nullable
    private Modal<BackupDialog> backupDialog;

    @Override
    public void initGameUiPresent() {
        MoreOptionsV2Config moreOptionsConfig = configStore.getCurrentConfig();

        // create More Options ui
        moreOptionsModal = uiFactory.buildMoreOptionsModal(MOD_INFO.name.toString(), moreOptionsConfig);
        initActions(moreOptionsModal);
        initDebugActions(moreOptionsModal, configStore);
        inject(moreOptionsModal);
        Optional<MoreOptionsV2Config> backupConfig = configStore.getBackupConfig();

        // create backup configuration dialog if needed
        if (backupConfig.isPresent()) {
            backupDialog = new Modal<>(MOD_INFO.name.toString(), new BackupDialog());
            backupMoreOptionsModal = uiFactory.buildMoreOptionsModal(
                MOD_INFO.name + " Backup",
                backupConfig.get());

            configStore.setCurrentConfig(backupConfig.get());
            initActions(backupMoreOptionsModal);
            initBackupActions(backupDialog, backupMoreOptionsModal, moreOptionsModal, backupConfig.get());
        }
    }

    public void inject(Modal<MoreOptionsPanel> moreOptionsModal) {
        log.debug("Injecting button into game ui");
        GButt.ButtPanel moreOptionsButton = new GButt.ButtPanel(SPRITES.icons().s.cog) {
            @Override
            protected void clickA() {
                moreOptionsModal.show();
            }
        };

        moreOptionsButton.hoverInfoSet(MOD_INFO.name);
        moreOptionsButton.setDim(32, UIPanelTop.HEIGHT);

        // inject button into world view
        try {
            Object object = gameApis.ui().findUIElementInWorldView(UIPanelTop.class)
                .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
                .orElse(null);

            if (object == null) {
                throw new IllegalStateException("Could not find UI to inject button into.");
            }

            log.debug("Injecting button into UIPanelTop#right in world view");
            GuiSection right = (GuiSection) object;
            right.addRelBody(8, DIR.W, moreOptionsButton);
        } catch (Exception e) {
            log.error("Could not inject %s UI button into world view :(", MOD_INFO.name, e);
        }

        // inject button into settlement view
        try {
            Object object = gameApis.ui().findUIElementInSettlementView(UIPanelTop.class)
                .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
                .orElse(null);

            if (object == null) {
                throw new IllegalStateException("Could not find UI to inject button into.");
            }

            log.debug("Injecting button into UIPanelTop#right in settlement view");
            GuiSection right = (GuiSection) object;
            right.addRelBody(8, DIR.W, moreOptionsButton);
        } catch (Exception e) {
            log.error("Could not inject %s UI button into settlement view :(", MOD_INFO.name, e);
        }
    }

    /**
     * Debug commands are executable via the in game debug panel
     */
    public void initDebugActions(Modal<MoreOptionsPanel> moreOptionsModal, ConfigStore configStore) {
        log.debug("Initialize %s Debug Commands", MOD_INFO.name);
        IDebugPanel.add(MOD_INFO.name + ":show", moreOptionsModal::show);
        IDebugPanel.add(MOD_INFO.name + ":metrics:flush", () -> MetricCollector.getInstance().flush());
        IDebugPanel.add(MOD_INFO.name + ":metrics:stop", () -> MetricScheduler.getInstance().stop());
        IDebugPanel.add(MOD_INFO.name + ":createBackup", configStore::createBackupConfig);
        IDebugPanel.add(MOD_INFO.name + ":log.stats", () -> {
            log.info("Events Status: %s", gameApis.events().readEventsEnabledStatus()
                .entrySet().stream().map(entry -> entry.getKey() + " enabled: " + entry.getValue() + "\n")
                .collect(Collectors.joining()));
        });
    }

    public void initActions(Modal<MoreOptionsPanel> moreOptionsModal) {
        MoreOptionsPanel moreOptionsPanel = moreOptionsModal.getSection();
        initActions(moreOptionsModal, moreOptionsPanel);

        // METRICS
        MetricsPanel metricsPanel = moreOptionsPanel.getMetricsPanel();
        initActions(metricsPanel);

        // RACES
        RacesPanel racesPanel = moreOptionsPanel.getRacesPanel();
        initActions(racesPanel);

        // after config is applied to game
        configurator.onAfterApplyAction(moreOptionsConfig -> {
            Path exportFile = metricExporter.getExportFile();
            Path currentExportFile = metricsPanel.getExportFilePath();

            // export file changed?
            if (!exportFile.equals(currentExportFile)) {
                metricsPanel.refresh(exportFile);

                // don't notify for initial files
                if (currentExportFile != null) {
                    notificator.notify(i18n.t("notification.metrics.file.new", exportFile.getFileName()));
                }
            }
        });
    }

    public void initActions(Modal<MoreOptionsPanel> moreOptionsModal, MoreOptionsPanel moreOptionsPanel) {
        // update Notificator queue when More Options Modal is rendered
        moreOptionsModal.renderAction((modal, seconds) -> notificator.update(seconds));
        moreOptionsModal.hideAction(modal -> notificator.close());

        // Cancel & Undo
        moreOptionsPanel.getCancelButton().clickActionSet(() -> {
            try {
                undo(moreOptionsPanel);
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.config.not.undo"), e);
                return;
            }

            moreOptionsModal.hide();
        });

        // Apply & Save
        moreOptionsPanel.getApplyButton().clickActionSet(() -> {
            try {
                if (!applyAndSave(moreOptionsPanel)) {
                    notificator.notifyError(i18n.t("notification.config.not.apply"));
                }
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.config.not.apply"), e);
            }
        });

        // Undo changes
        moreOptionsPanel.getUndoButton().clickActionSet(() -> {
            try {
                undo(moreOptionsPanel);
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.config.not.undo"), e);
            }
        });

        // reload and apply config from file
        moreOptionsPanel.getReloadButton().clickActionSet(() -> {
            MoreOptionsV2Config moreOptionsConfig = configStore.loadConfig().orElse(null);
            if (moreOptionsConfig != null) {
                moreOptionsPanel.setValue(moreOptionsConfig);
                notificator.notifySuccess(i18n.t("notification.config.reload"));
            } else {
                notificator.notifyError(i18n.t("notification.config.not.reload"));
            }
        });

        // copy config from ui into clipboard
        moreOptionsPanel.getShareButton().clickActionSet(() -> {
            MoreOptionsV2Config moreOptionsConfig = moreOptionsPanel.getValue();
            try {
                if (moreOptionsConfig != null) {
                    JsonE jsonE = JsonConfigMapper.mapConfig(moreOptionsConfig);
                    boolean written = Clipboard.write(jsonE.toString());

                    if (written) {
                        notificator.notifySuccess(i18n.t("notification.config.copy"));
                    } else {
                        notificator.notifyError(i18n.t("notification.config.not.copy"));
                    }
                } else {
                    notificator.notifyError(i18n.t("notification.config.not.copy"));
                }
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.config.not.copy"), e);
            }
        });

        // Apply default config to ui
        moreOptionsPanel.getDefaultButton().clickActionSet(() -> {
            MoreOptionsV2Config defaultConfig = configStore.getDefaultConfig();
            try {
                moreOptionsPanel.setValue(defaultConfig);
                notificator.notifySuccess(i18n.t("notification.config.default.apply"));
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.config.default.not.apply"), e);
            }
        });

        // Apply default config to ui and game & delete config file
        moreOptionsPanel.getResetButton().clickActionSet(() -> {
            // are you sure message
            VIEW.inters().yesNo.activate(i18n.t("MoreOptionsPanel.text.yesNo.reset"), () -> {
                MoreOptionsV2Config defaultConfig = configStore.getDefaultConfig();
                try {
                    moreOptionsPanel.setValue(defaultConfig);
                    configStore.deleteConfig();
                    if (applyAndSave(moreOptionsPanel)) {
                        notificator.notifySuccess(i18n.t("notification.config.reset"));
                    } else {
                        notificator.notifyError(i18n.t("notification.config.not.reset"));
                    }

                } catch (Exception e) {
                    notificator.notifyError(i18n.t("notification.config.not.reset"), e);
                }
            }, () -> {}, true);
        });

        //Ok: Apply & Save & Exit
        moreOptionsPanel.getOkButton().clickActionSet(() -> {
            try {
                applyAndSave(moreOptionsPanel);
            } catch (Exception e) {
               log.error("Could not apply config to game.", e);
            }

            moreOptionsModal.hide();
        });

        // opens folder with mod configuration
        moreOptionsPanel.getFolderButton().clickActionSet(() -> {
            try {
                FileManager.openDesctop(ConfigStore.MORE_OPTIONS_CONFIG_PATH.get().toString());
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.config.folder.not.open", ConfigStore.MORE_OPTIONS_CONFIG_PATH), e);
            }
        });
    }

    public void initActions(MetricsPanel metricsPanel) {
        // opens folder with metric export files
        metricsPanel.getExportFolderButton().clickActionSet(() -> {
            Path exportFolderPath = metricsPanel.getExportFolderPath();
            if (!exportFolderPath.toFile().exists()) {
                notificator.notifyError(i18n.t("notification.metrics.folder.not.exists", exportFolderPath));
                return;
            }

            try {
                FileManager.openDesctop(exportFolderPath.toString());
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.metrics.folder.not.open", exportFolderPath), e);
            }
        });

        // copy export file name into clipboard
        metricsPanel.getCopyExportFileButton().clickActionSet(() -> {
            Path exportFilePath = metricsPanel.getExportFilePath();

            try {
                if (exportFilePath != null) {
                    boolean written = Clipboard.write(exportFilePath.toString());

                    if (written) {
                        notificator.notifySuccess(i18n.t("notification.metrics.file.path.copy", exportFilePath));
                    } else {
                        notificator.notifyError(i18n.t("notification.metrics.file.path.not.copy"));
                    }
                } else {
                    notificator.notifyError(i18n.t("notification.metrics.file.path.not.copy"));
                }
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.metrics.file.path.not.copy"), e);
            }
        });
    }

    public void initActions(RacesPanel racesPanel) {
        // Open current race config file
        racesPanel.getFileButton().clickActionSet(() -> {
            Path path = configStore.racesConfigPath();
            if (!path.toFile().exists()) {
                notificator.notify(i18n.t("notification.races.file.not.exists", path));
                return;
            }

            try {
                FileManager.openDesctop(path.toString());
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.races.file.not.open", path), e);
            }
        });

        // Open folder with race config files
        racesPanel.getFolderButton().clickActionSet(() -> {
            if (!ConfigStore.RACES_CONFIG_PATH.toFile().exists()) {
                notificator.notifyError(i18n.t("notification.races.folder.not.exists", ConfigStore.RACES_CONFIG_PATH));
                return;
            }

            try {
                FileManager.openDesctop(ConfigStore.RACES_CONFIG_PATH.toString());
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.races.folder.not.open", ConfigStore.RACES_CONFIG_PATH), e);
            }
        });

        // Export current race config from ui into clipboard
        racesPanel.getExportButton().clickActionSet(() -> {
            try {
                MoreOptionsV2Config.RacesConfig racesConfig = racesPanel.getValue();
                JsonElement jsonElement = JsonMapper.mapObject(racesConfig);
                Json json = new Json(jsonElement, JsonWriter.getJsonE());

                if (Clipboard.write(json.toString())) {
                    notificator.notifySuccess(i18n.t("notification.races.config.copy"));
                } else {
                    notificator.notifyError(i18n.t("notification.races.config.not.copy"));
                }
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.races.config.not.copy"), e);
            }
        });

        // Import race config from clipboard into ui
        racesPanel.getImportButton().clickActionSet(() -> {
            try {
                Clipboard.read().ifPresent(s -> {
                    Json json = new Json(s, JsonWriter.getJsonE());
                    MoreOptionsV2Config.RacesConfig racesConfig = JsonMapper.mapJson(json.getRoot(), MoreOptionsV2Config.RacesConfig.class);

                    racesPanel.setValue(racesConfig);
                    notificator.notifySuccess(i18n.t("notification.races.config.import"));
                });
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.races.config.not.import"));
            }
        });

        // Opens selection of race config files to load
        racesPanel.getLoadButton().clickActionSet(() -> {
            Window<RacesSelectionPanel> racesConfigsSelection = uiFactory.buildRacesConfigSelection("Select a file");
            // load from race config file on doubleclick
            racesConfigsSelection.getSection().getRacesConfigTable().doubleClickAction(row -> {
                try {
                    RacesSelectionPanel.Entry entry = row.getValue();
                    Path configPath = entry.getConfigPath();
                    MoreOptionsV2Config.RacesConfig racesConfig = configStore.loadRaceConfig(configPath).orElse(null);

                    if (racesConfig != null) {
                        racesPanel.setValue(racesConfig);
                        notificator.notifySuccess(i18n.t("notification.races.config.load"));
                        racesConfigsSelection.hide();
                    } else {
                        notificator.notifyError(i18n.t("notification.races.config.not.load"));
                    }
                } catch (Exception e) {
                    notificator.notifyError(i18n.t("notification.races.config.not.load"), e);
                }
            });

            racesConfigsSelection.show();
        });


    }

    public void initBackupActions(
        Modal<BackupDialog> backupDialog,
        Modal<MoreOptionsPanel> backupMoreOptionsModal,
        Modal<MoreOptionsPanel> moreOptionsModal,
        MoreOptionsV2Config backupConfig
    ) {
        // Close: More Options modal with backup config
        backupMoreOptionsModal.getPanel().setCloseAction(() -> {
            try {
                configStore.deleteBackupConfig();
            } catch (Exception e) {
                log.error("Could not delete backup config", e);
            }

            backupMoreOptionsModal.hide();
            backupDialog.show();
        });

        MoreOptionsPanel moreOptionsPanel = backupMoreOptionsModal.getSection();

        // Cancel & Undo
        Button cancelButton = moreOptionsPanel.getCancelButton();
        cancelButton.clickActionSet(() -> {
            try {
                undo(moreOptionsPanel);
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.config.not.undo"), e);
                return;
            }

            backupMoreOptionsModal.hide();
        });

        // Ok
        Button okButton = moreOptionsPanel.getOkButton();
        okButton.clickActionSet(() -> {
            MoreOptionsV2Config readConfig = moreOptionsPanel.getValue();

            // fallback
            if (readConfig == null) {
                readConfig = configStore.getDefaultConfig();
            }

            try {
                moreOptionsModal.getSection().setValue(readConfig);
                configStore.deleteBackupConfig();
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.backup.config.not.apply", MOD_INFO.name), e);
                return;
            }

            backupMoreOptionsModal.hide();
        });

        // Edit Backup
        backupDialog.getSection().getEditButton().clickActionSet(() -> {
            backupDialog.hide();

            try {
                moreOptionsPanel.setValue(backupConfig);
            } catch (Exception e) {
                log.error("Could not apply backup config for editing", e);
                return;
            }

            backupMoreOptionsModal.show();
        });

        // Close: Backup Dialog
        backupDialog.getPanel().setCloseAction(() -> {
            try {
                configStore.deleteBackupConfig();
                MoreOptionsV2Config defaultConfig = configStore.getDefaultConfig();
                moreOptionsModal.getSection().setValue(defaultConfig);
                configurator.applyConfig(defaultConfig);
                configStore.setCurrentConfig(defaultConfig);
                configStore.saveConfig(defaultConfig);
            } catch (Exception e) {
                log.error("Could not apply default config via backup dialog", e);
            }

            backupDialog.hide();
        });

        // Discard Backup
        backupDialog.getSection().getDiscardButton().clickActionSet(() -> {
            try {
                configStore.deleteBackupConfig();
                MoreOptionsV2Config defaultConfig = configStore.getDefaultConfig();
                moreOptionsModal.getSection().setValue(defaultConfig);
                configurator.applyConfig(defaultConfig);
                configStore.setCurrentConfig(defaultConfig);
                configStore.saveConfig(defaultConfig);
            } catch (Exception e) {
                log.error("Could not discard backup config", e);
            }

            backupDialog.hide();
        });

        // Apply Backup
        backupDialog.getSection().getApplyButton().clickActionSet(() -> {
            try {
                configurator.applyConfig(backupConfig);
                moreOptionsModal.getSection().setValue(backupConfig);
                configStore.setCurrentConfig(backupConfig);
                configStore.saveConfig(backupConfig);
                configStore.deleteBackupConfig();
            } catch (Exception e) {
                log.error("Could not apply backup config", e);
            }

            backupDialog.hide();
        });
    }

    private @Nullable MoreOptionsV2Config apply(MoreOptionsPanel moreOptionsPanel) {
        // only save when changes were made
        if (moreOptionsPanel.isDirty()) {
            MoreOptionsV2Config config = moreOptionsPanel.getValue();

            if (config == null) {
                log.warn("Could read config from modal. Got null");
                return null;
            }

            // notify when metric collection status changes
            MoreOptionsV2Config currentConfig = configStore.getCurrentConfig();
            if (currentConfig.getMetrics().isEnabled() != config.getMetrics().isEnabled()) {
                if (config.getMetrics().isEnabled()) {
                    notificator.notify(i18n.t("notification.metrics.start"));
                } else {
                    notificator.notify(i18n.t("notification.metrics.stop"));
                }
            }

            configurator.applyConfig(config);
            configStore.setCurrentConfig(config);
            return config;
        }

        return null;
    }

    private boolean applyAndSave(MoreOptionsPanel moreOptionsPanel) {
        MoreOptionsV2Config appliedConfig = apply(moreOptionsPanel);

        if (appliedConfig != null) {
            return configStore.saveConfig(appliedConfig);
        }

        return false;
    }

    private void undo(MoreOptionsPanel moreOptionsPanel) {
        MoreOptionsV2Config currentConfig = configStore.getCurrentConfig();
        moreOptionsPanel.setValue(currentConfig);
    }
}
