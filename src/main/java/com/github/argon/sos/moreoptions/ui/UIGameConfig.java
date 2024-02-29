package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.config.ConfigMapper;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.api.UninitializedException;
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
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.DIR;
import snake2d.util.file.FileManager;
import snake2d.util.file.JsonE;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;
import view.interrupter.IDebugPanel;
import view.ui.top.UIPanelTop;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.argon.sos.moreoptions.MoreOptionsScript.MOD_INFO;

/**
 * Most UI elements are generated dynamically dictated by the given config {@link MoreOptionsConfig}.
 * So when a new entry is added, a new UI element like e.g. an additional slider will also be visible.
 * For setting up the UI.
 */
@RequiredArgsConstructor
public class UIGameConfig {

    private final static Logger log = Loggers.getLogger(UIGameConfig.class);

    private final GameApis gameApis;
    private final MoreOptionsConfigurator configurator;
    private final ConfigStore configStore;
    private final MetricExporter metricExporter;
    private final MetricCollector metricCollector;

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
        Modal<BackupDialog> backupModal,
        Modal<MoreOptionsView> backupMoreOptionsModal,
        Modal<MoreOptionsView> moreOptionsModal,
        MoreOptionsConfig backupConfig
    ) {
        // Close: More Options modal with backup config
        backupMoreOptionsModal.getPanel().setCloseAction(() -> {
            configStore.deleteBackupConfig();
            backupMoreOptionsModal.hide();
            backupModal.show();
        });

        // Cancel & Undo
        Button cancelButton = Optional.ofNullable(backupMoreOptionsModal.getSection().getCancelButton())
            .orElseThrow(() -> new UninitializedException("BackupModal is not initialized."));
        cancelButton.clickActionSet(() -> {
            undo(backupMoreOptionsModal.getSection());
            backupMoreOptionsModal.hide();
        });

        // Ok
        Button okButton = Optional.ofNullable(backupMoreOptionsModal.getSection().getOkButton())
            .orElseThrow(() -> new UninitializedException("BackupModal is not initialized."));
        okButton.clickActionSet(() -> {
            MoreOptionsConfig readConfig = backupMoreOptionsModal.getSection().getValue();

            // fallback
            if (readConfig == null) {
                readConfig = configStore.getDefaultConfig();
            }

            moreOptionsModal.getSection().setValue(readConfig);
            configStore.deleteBackupConfig();
            backupMoreOptionsModal.hide();
        });

        // Edit Backup
        backupModal.getSection().getEditButton().clickActionSet(() -> {
            backupMoreOptionsModal.getSection().setValue(backupConfig);
            backupModal.hide();
            backupMoreOptionsModal.show();
        });

        // Close: Backup Modal
        backupModal.getPanel().setCloseAction(() -> {
            configStore.deleteBackupConfig();
            MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
            moreOptionsModal.getSection().setValue(defaultConfig);
            configurator.applyConfig(defaultConfig);
            configStore.setCurrentConfig(defaultConfig);
            configStore.saveConfig(defaultConfig);
            backupModal.hide();
        });

        // Discard Backup
        backupModal.getSection().getDiscardButton().clickActionSet(() -> {
            configStore.deleteBackupConfig();
            MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
            moreOptionsModal.getSection().setValue(defaultConfig);
            configurator.applyConfig(defaultConfig);
            configStore.setCurrentConfig(defaultConfig);
            configStore.saveConfig(defaultConfig);
            backupModal.hide();
        });

        // Apply Backup
        backupModal.getSection().getApplyButton().clickActionSet(() -> {
            configurator.applyConfig(backupConfig);
            moreOptionsModal.getSection().setValue(backupConfig);
            configStore.setCurrentConfig(backupConfig);
            configStore.saveConfig(backupConfig);
            configStore.deleteBackupConfig();
            backupModal.hide();
        });
    }

    public void initActions(Modal<MoreOptionsView> moreOptionsModal) {
        MoreOptionsView moreOptionsView = moreOptionsModal.getSection();

        // Cancel & Undo
        Button cancelButton = moreOptionsView.getCancelButton();
        cancelButton.clickActionSet(() -> {
            undo(moreOptionsView);
            moreOptionsModal.hide();
        });

        // Apply & Save
        Button applyButton = moreOptionsView.getApplyButton();
        applyButton.clickActionSet(() -> {
            applyAndSave(moreOptionsView);
        });

        // Undo changes
        Button undoButton = moreOptionsView.getUndoButton();
        undoButton.clickActionSet(() -> undo(moreOptionsView));

        // reload and apply config from file
        Button reloadButton = moreOptionsView.getReloadButton();
        reloadButton.clickActionSet(() -> {
            MoreOptionsConfig moreOptionsConfig = configStore.loadConfig().orElse(null);

            if (moreOptionsConfig != null) {
                moreOptionsView.setValue(moreOptionsConfig);
                reloadButton.markSuccess(true);
            } else {
                reloadButton.markSuccess(false);
            }
        });

        // copy config from ui into clipboard
        Button shareButton = moreOptionsView.getShareButton();
        shareButton.clickActionSet(() -> {
            MoreOptionsConfig moreOptionsConfig = moreOptionsView.getValue();
            try {
                if (moreOptionsConfig != null) {
                    JsonE jsonE = ConfigMapper.getInstance().mapConfig(moreOptionsConfig);
                    shareButton.markSuccess(Clipboard.write(jsonE.toString()));
                } else {
                    reloadButton.markSuccess(false);
                }
            } catch (Exception e) {
                reloadButton.markSuccess(false);
                log.error("Could not copy config to clipboard.", e);
            }
        });


        // Reset UI to default
        Button resetButton = moreOptionsView.getResetButton();
        resetButton.clickActionSet(() -> {
            MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
            moreOptionsView.setValue(defaultConfig);
        });

        //Ok: Apply & Save & Exit
        Button okButton = moreOptionsView.getOkButton();
        okButton.clickActionSet(() -> {
            applyAndSave(moreOptionsView);
            moreOptionsModal.hide();
        });

        // opens folder with mod configuration
        Button folderButton = moreOptionsView.getFolderButton();
        folderButton.clickActionSet(() -> {
            FileManager.openDesctop(PATHS.local().SETTINGS.get().toString());
        });

        MetricsPanel metricsPanel = moreOptionsView.getMetricsPanel();

        // opens folder with metric export files
        Button exportFolderButton = metricsPanel.getExportFolderButton();
        exportFolderButton.clickActionSet(() -> {
            Path exportFolderPath = metricsPanel.getExportFolderPath();

            if (!exportFolderPath.toFile().exists()) {
                exportFolderButton.markSuccess(false);
                log.info("Can not open metrics export folder: %s. Folder does not exists.", exportFolderPath);
                return;
            }

            FileManager.openDesctop(exportFolderPath.toString());
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
            config.getMetrics().getStats().addAll(availableStats);
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
        moreOptionsView.getConfigStore().getCurrentConfig().ifPresent(moreOptionsView::setValue);
    }
}
