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
import com.github.argon.sos.moreoptions.init.InitPhases;
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
import com.github.argon.sos.moreoptions.ui.panel.RacesConfigSelectionPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesPanel;
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
public class UiConfig implements InitPhases {

    @Getter(lazy = true)
    private final static UiConfig instance = new UiConfig(
        GameApis.getInstance(),
        MoreOptionsConfigurator.getInstance(),
        ConfigStore.getInstance(),
        MetricExporter.getInstance(),
        UiFactory.getInstance(),
        Notificator.getInstance()
    );

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
    private Modal<MoreOptions> moreOptionsModal;
    @Getter
    @Nullable
    private Modal<MoreOptions> backupMoreOptionsModal;
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

    public void inject(Modal<MoreOptions> moreOptionsModal) {
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
    public void initDebugActions(Modal<MoreOptions> moreOptionsModal, ConfigStore configStore) {
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

    public void initActions(Modal<MoreOptions> moreOptionsModal) {
        MoreOptions moreOptionsPanel = moreOptionsModal.getSection();
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
                    notificator.notify("New export file: " + exportFile.getFileName());
                }
            }
        });
    }

    public void initActions(Modal<MoreOptions> moreOptionsModal, MoreOptions moreOptionsPanel) {
        // update Notificator queue when More Options Modal is rendered
        moreOptionsModal.renderAction((modal, seconds) -> notificator.update(seconds));
        moreOptionsModal.hideAction(modal -> notificator.close());

        // Cancel & Undo
        moreOptionsPanel.getCancelButton().clickActionSet(() -> {
            try {
                undo(moreOptionsPanel);
            } catch (Exception e) {
                notificator.notifyError("Could not undo changes.", e);
                return;
            }

            moreOptionsModal.hide();
        });

        // Apply & Save
        moreOptionsPanel.getApplyButton().clickActionSet(() -> {
            try {
                if (!applyAndSave(moreOptionsPanel)) {
                    notificator.notifyError("Could not apply config to game.");
                }
            } catch (Exception e) {
                notificator.notifyError("Could not apply config to game.", e);
            }
        });

        // Undo changes
        moreOptionsPanel.getUndoButton().clickActionSet(() -> {
            try {
                undo(moreOptionsPanel);
            } catch (Exception e) {
                notificator.notifyError("Could not undo changes.", e);
            }
        });

        // reload and apply config from file
        moreOptionsPanel.getReloadButton().clickActionSet(() -> {
            MoreOptionsV2Config moreOptionsConfig = configStore.loadConfig().orElse(null);
            if (moreOptionsConfig != null) {
                moreOptionsPanel.setValue(moreOptionsConfig);
                notificator.notifySuccess("Config reloaded into ui.");
            } else {
                notificator.notifyError("Could not reload config from file.");
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
                        notificator.notifySuccess("Config copied to clipboard.");
                    } else {
                        notificator.notifyError("Could not copy config to clipboard.");
                    }
                } else {
                    notificator.notifyError("Could not load config from file.");
                }
            } catch (Exception e) {
                notificator.notifyError("Could not copy config to clipboard.", e);
            }
        });

        // Apply default config to ui
        moreOptionsPanel.getDefaultButton().clickActionSet(() -> {
            MoreOptionsV2Config defaultConfig = configStore.getDefaultConfig();
            try {
                moreOptionsPanel.setValue(defaultConfig);
                notificator.notifySuccess("Default config applied to ui. ");
            } catch (Exception e) {
                notificator.notifyError("Could not apply default config to ui.", e);
            }
        });

        // Apply default config to ui and game & delete config file
        moreOptionsPanel.getResetButton().clickActionSet(() -> {
            // are you sure message
            VIEW.inters().yesNo.activate("This will delete your config file and reset the ui and game to default settings.", () -> {
                MoreOptionsV2Config defaultConfig = configStore.getDefaultConfig();
                try {
                    moreOptionsPanel.setValue(defaultConfig);
                    configStore.deleteConfig();
                    if (applyAndSave(moreOptionsPanel)) {
                        notificator.notifySuccess("Default config applied to ui and file deleted.");
                    } else {
                        notificator.notifyError("Could not apply config.");
                    }

                } catch (Exception e) {
                    notificator.notifyError("Could not reset ui.", e);
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
                notificator.notifyError("Could not open config folder: " + ConfigStore.MORE_OPTIONS_CONFIG_PATH, e);
            }
        });
    }

    public void initActions(MetricsPanel metricsPanel) {
        // opens folder with metric export files
        metricsPanel.getExportFolderButton().clickActionSet(() -> {
            Path exportFolderPath = metricsPanel.getExportFolderPath();
            if (!exportFolderPath.toFile().exists()) {
                notificator.notifyError("Metrics export folder does not exists: " + exportFolderPath);
                return;
            }

            try {
                FileManager.openDesctop(exportFolderPath.toString());
            } catch (Exception e) {
                notificator.notifyError("Could not open metrics export folder: " + exportFolderPath, e);
            }
        });

        // copy export file name into clipboard
        metricsPanel.getCopyExportFileButton().clickActionSet(() -> {
            Path exportFilePath = metricsPanel.getExportFilePath();

            try {
                if (exportFilePath != null) {
                    boolean written = Clipboard.write(exportFilePath.toString());

                    if (written) {
                        notificator.notifySuccess(exportFilePath + " copied to clipboard.");
                    } else {
                        notificator.notifyError("Could not copy export file path to clipboard.");
                    }
                } else {
                    notificator.notifyError("There is no export file path to copy.");
                }
            } catch (Exception e) {
                notificator.notifyError("Could not copy export file path to clipboard.", e);
            }
        });
    }

    public void initActions(RacesPanel racesPanel) {
        // Open current race config file
        racesPanel.getFileButton().clickActionSet(() -> {
            Path path = configStore.racesConfigPath();
            if (!path.toFile().exists()) {
                notificator.notify("Race config file does not exist: " + path);
                return;
            }

            try {
                FileManager.openDesctop(path.toString());
            } catch (Exception e) {
                notificator.notifyError("Could not open races config file: " + path, e);
            }
        });

        // Open folder with race config files
        racesPanel.getFolderButton().clickActionSet(() -> {
            if (!ConfigStore.RACES_CONFIG_PATH.toFile().exists()) {
                notificator.notifyError("Race config folder does not exist: " +  ConfigStore.RACES_CONFIG_PATH);
                return;
            }

            try {
                FileManager.openDesctop(ConfigStore.RACES_CONFIG_PATH.toString());
            } catch (Exception e) {
                notificator.notifyError("Could not open races config folder: " + ConfigStore.RACES_CONFIG_PATH, e);
            }
        });

        // Export current race config from ui into clipboard
        racesPanel.getExportButton().clickActionSet(() -> {
            try {
                MoreOptionsV2Config.RacesConfig racesConfig = racesPanel.getValue();
                JsonElement jsonElement = JsonMapper.mapObject(racesConfig);
                Json json = new Json(jsonElement, JsonWriter.getJsonE());

                if (Clipboard.write(json.toString())) {
                    notificator.notifySuccess("Race config copied to clipboard.");
                } else {
                    notificator.notifyError("Could not copy races config to clipboard.");
                }
            } catch (Exception e) {
                notificator.notifyError("Could not copy races config to clipboard.", e);
            }
        });

        // Import race config from clipboard into ui
        racesPanel.getImportButton().clickActionSet(() -> {
            try {
                Clipboard.read().ifPresent(s -> {
                    Json json = new Json(s, JsonWriter.getJsonE());
                    MoreOptionsV2Config.RacesConfig racesConfig = JsonMapper.mapJson(json.getRoot(), MoreOptionsV2Config.RacesConfig.class);

                    racesPanel.setValue(racesConfig);
                    notificator.notifySuccess("Race config imported from clipboard.");
                });
            } catch (Exception e) {
                notificator.notifyError("Could not import races config to clipboard.");
            }
        });

        // Opens selection of race config files to load
        racesPanel.getLoadButton().clickActionSet(() -> {
            Window<RacesConfigSelectionPanel> racesConfigsSelection = uiFactory.buildRacesConfigSelection("Select a file");
            // load from race config file on doubleclick
            racesConfigsSelection.getSection().getRacesConfigTable().doubleClickAction(row -> {
                try {
                    RacesConfigSelectionPanel.Entry entry = row.getValue();
                    Path configPath = entry.getConfigPath();
                    MoreOptionsV2Config.RacesConfig racesConfig = configStore.loadRaceConfig(configPath).orElse(null);

                    if (racesConfig != null) {
                        racesPanel.setValue(racesConfig);
                        notificator.notifySuccess("Races config loaded and applied to ui.");
                        racesConfigsSelection.hide();
                    } else {
                        notificator.notifyError("Could not load races config.");
                    }
                } catch (Exception e) {
                    notificator.notifyError("Could not load races config.", e);
                }
            });

            racesConfigsSelection.show();
        });


    }

    public void initBackupActions(
        Modal<BackupDialog> backupDialog,
        Modal<MoreOptions> backupMoreOptionsModal,
        Modal<MoreOptions> moreOptionsModal,
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

        MoreOptions moreOptionsPanel = backupMoreOptionsModal.getSection();

        // Cancel & Undo
        Button cancelButton = moreOptionsPanel.getCancelButton();
        cancelButton.clickActionSet(() -> {
            try {
                undo(moreOptionsPanel);
            } catch (Exception e) {
                notificator.notifyError("Could not undo changes.", e);
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
                notificator.notifyError("Could not apply backup config to " + MOD_INFO.name + " ui.", e);
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

    private @Nullable MoreOptionsV2Config apply(MoreOptions moreOptionsPanel) {
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
                    notificator.notify("Starting metric collection and export");
                } else {
                    notificator.notify("Stopping metric collection and export");
                }
            }

            configurator.applyConfig(config);
            configStore.setCurrentConfig(config);
            return config;
        }

        return null;
    }

    private boolean applyAndSave(MoreOptions moreOptionsPanel) {
        MoreOptionsV2Config appliedConfig = apply(moreOptionsPanel);

        if (appliedConfig != null) {
            return configStore.saveConfig(appliedConfig);
        }

        return false;
    }

    private void undo(MoreOptions moreOptionsPanel) {
        MoreOptionsV2Config currentConfig = moreOptionsPanel.getConfigStore().getCurrentConfig();
        moreOptionsPanel.setValue(currentConfig);
    }
}
