package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.Notificator;
import com.github.argon.sos.moreoptions.config.ConfigMapper;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.Modal;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricCollector;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.MetricsPanel;
import com.github.argon.sos.moreoptions.util.Clipboard;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import init.paths.ModInfo;
import init.paths.PATHS;
import init.sprite.SPRITES;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.factory.Mappers;
import snake2d.util.datatypes.DIR;
import snake2d.util.file.FileManager;
import snake2d.util.file.JsonE;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;
import view.interrupter.IDebugPanel;
import view.ui.top.UIPanelTop;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.argon.sos.moreoptions.MoreOptionsScript.MOD_INFO;

/**
 * Most UI elements are generated dynamically dictated by the given config {@link MoreOptionsConfig}.
 * So when a new entry is added, a new UI element like e.g. an additional slider will also be visible.
 * For setting up the UI.
 */
@RequiredArgsConstructor
public class UiGameConfig {

    @Getter(lazy = true)
    private final static UiGameConfig instance = new UiGameConfig(
        GameApis.getInstance(),
        MoreOptionsConfigurator.getInstance(),
        ConfigStore.getInstance(),
        MetricExporter.getInstance(),
        MetricCollector.getInstance(),
        Notificator.getInstance(),
        Mappers.getMapper(ConfigMapper.class)
    );

    private final static Logger log = Loggers.getLogger(UiGameConfig.class);

    private final GameApis gameApis;
    private final MoreOptionsConfigurator configurator;
    private final ConfigStore configStore;
    private final MetricExporter metricExporter;
    private final MetricCollector metricCollector;
    private final Notificator notificator;
    private final ConfigMapper configMapper;

    public void inject(Modal<MoreOptionsView> moreOptionsModal) {
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
    public void initDebugActions(Modal<MoreOptionsView> moreOptionsModal, ConfigStore configStore) {
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

    public void initBackupActions(
        Modal<BackupDialog> backupDialog,
        Modal<MoreOptionsView> backupMoreOptionsModal,
        Modal<MoreOptionsView> moreOptionsModal,
        MoreOptionsConfig backupConfig
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

        MoreOptionsView moreOptionsView = backupMoreOptionsModal.getSection();

        // Cancel & Undo
        Button cancelButton = moreOptionsView.getCancelButton();
        cancelButton.clickActionSet(() -> {
            try {
                undo(moreOptionsView);
            } catch (Exception e) {
                notificator.notifyError("Could not undo changes.", e);
                return;
            }

            backupMoreOptionsModal.hide();
        });

        // Ok
        Button okButton = moreOptionsView.getOkButton();
        okButton.clickActionSet(() -> {
            MoreOptionsConfig readConfig = moreOptionsView.getValue();

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
                moreOptionsView.setValue(backupConfig);
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
                MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
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
                MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
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

    public void initActions(Modal<MoreOptionsView> moreOptionsModal) {
        MoreOptionsView moreOptionsView = moreOptionsModal.getSection();

        // update Notificator queue when More Options Modal is rendered
        moreOptionsModal.onRender((modal, seconds) -> notificator.update(seconds));
        moreOptionsModal.onHide(modal -> notificator.close());

        // Cancel & Undo
        Button cancelButton = moreOptionsView.getCancelButton();
        cancelButton.clickActionSet(() -> {
            try {
                undo(moreOptionsView);
            } catch (Exception e) {
                notificator.notifyError("Could not undo changes.", e);
                return;
            }

            moreOptionsModal.hide();
        });

        // Apply & Save
        Button applyButton = moreOptionsView.getApplyButton();
        applyButton.clickActionSet(() -> {
            try {
                applyAndSave(moreOptionsView);
            } catch (Exception e) {
                notificator.notifyError("Could not apply config to game.", e);
            }
        });

        // Undo changes
        Button undoButton = moreOptionsView.getUndoButton();
        undoButton.clickActionSet(() -> {
            try {
                undo(moreOptionsView);
            } catch (Exception e) {
                notificator.notifyError("Could not undo changes.", e);
            }
        });

        // reload and apply config from file
        Button reloadButton = moreOptionsView.getReloadButton();
        reloadButton.clickActionSet(() -> {
            MoreOptionsConfig moreOptionsConfig = configStore.loadConfig().orElse(null);
            if (moreOptionsConfig != null) {
                moreOptionsView.setValue(moreOptionsConfig);
                notificator.notifySuccess("Config reloaded into ui.");
            } else {
                notificator.notifyError("Could not reload config from file.");
            }
        });

        // copy config from ui into clipboard
        Button shareButton = moreOptionsView.getShareButton();
        shareButton.clickActionSet(() -> {
            MoreOptionsConfig moreOptionsConfig = moreOptionsView.getValue();
            try {
                if (moreOptionsConfig != null) {
                    JsonE jsonE = configMapper.mapConfig(moreOptionsConfig);
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


        // Reset UI to default
        Button resetButton = moreOptionsView.getResetButton();
        resetButton.clickActionSet(() -> {
            MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
            try {
                moreOptionsView.setValue(defaultConfig);
                notificator.notifySuccess("Default config applied to ui. ");
            } catch (Exception e) {
                notificator.notifyError("Could not apply default config to ui.", e);
            }
        });

        //Ok: Apply & Save & Exit
        Button okButton = moreOptionsView.getOkButton();
        okButton.clickActionSet(() -> {
            try {
                applyAndSave(moreOptionsView);
            } catch (Exception e) {
                notificator.notifyError("Could not apply config to game.", e);
                return;
            }

            moreOptionsModal.hide();
        });

        // opens folder with mod configuration
        Button folderButton = moreOptionsView.getFolderButton();
        folderButton.clickActionSet(() -> {
            try {
                FileManager.openDesctop(PATHS.local().SETTINGS.get().toString());
            } catch (Exception e) {
                notificator.notifyError("Could not open config folder.", e);
            }
        });

        MetricsPanel metricsPanel = moreOptionsView.getMetricsPanel();

        // opens folder with metric export files
        Button exportFolderButton = metricsPanel.getExportFolderButton();
        exportFolderButton.clickActionSet(() -> {
            Path exportFolderPath = metricsPanel.getExportFolderPath();

            if (!exportFolderPath.toFile().exists()) {
                notificator.notifyError("Metrics export folder does not exists.");
                return;
            }

            try {
                FileManager.openDesctop(exportFolderPath.toString());
            } catch (Exception e) {
                notificator.notifyError("Could not open metrics export folder.", e);
            }
        });

        // after config is applied to game
        configurator.onAfterApplyAction(moreOptionsConfig -> {
            metricsPanel.refresh(metricExporter.getExportFile());
        });
    }


    /**
     * Generates UI with available config.
     * Adds config modal buttons functionality.
     *
     * @param config used to generate the UI
     */
    public Modal<MoreOptionsView> buildModal(String title, MoreOptionsConfig config) {
        log.debug("Initialize %s ui", title);

        List<BoostersPanel.Entry> boosterEntries = config.getBoosters().entrySet().stream()
            .map(entry -> BoostersPanel.Entry.builder()
                .key(entry.getKey())
                .range(entry.getValue())
                .boosters(gameApis.booster().get(entry.getKey()))
                .cat(gameApis.booster().getCat(entry.getKey()))
                .build())
            .collect(Collectors.toList());

        List<String> availableStats = gameApis.stats().getAvailableStatKeys();
        ModInfo modInfo = gameApis.mod().getCurrentMod().orElse(null);
        Path exportFolder = MetricExporter.EXPORT_FOLDER;
        Path exportFile = metricExporter.getExportFile();

        // todo this is hacky: make sure when no stats are configured, use availableStats
        if (config.getMetrics().getStats().isEmpty()) {
            config.getMetrics().setStats(availableStats);
        }

        Modal<MoreOptionsView> moreOptionsModal = new Modal<>(title,
            new MoreOptionsView(config, configStore, boosterEntries, availableStats, exportFolder, exportFile, modInfo));
        moreOptionsModal.center();

        initActions(moreOptionsModal);
        return moreOptionsModal;
    }

    private @Nullable MoreOptionsConfig apply(MoreOptionsView moreOptionsView) {
        // only save when changes were made
        if (moreOptionsView.isDirty()) {
            MoreOptionsConfig config = moreOptionsView.getValue();

            if (config == null) {
                log.warn("Could read config from modal. Got null");
                return null;
            }

            // notify when metric collection status changes
            MoreOptionsConfig currentConfig = configStore.getCurrentConfig();
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

    private boolean applyAndSave(MoreOptionsView moreOptionsView) {
        MoreOptionsConfig appliedConfig = apply(moreOptionsView);

        if (appliedConfig != null) {
            return configStore.saveConfig(appliedConfig);
        }

        return false;
    }

    private void undo(MoreOptionsView moreOptionsView) {
        MoreOptionsConfig currentConfig = moreOptionsView.getConfigStore().getCurrentConfig();
        moreOptionsView.setValue(currentConfig);
    }
}
