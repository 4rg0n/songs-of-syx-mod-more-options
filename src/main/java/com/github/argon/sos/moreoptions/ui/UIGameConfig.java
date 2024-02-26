package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
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
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import init.paths.PATHS;
import init.sprite.SPRITES;
import lombok.RequiredArgsConstructor;
import snake2d.util.datatypes.DIR;
import snake2d.util.file.FileManager;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;
import view.interrupter.IDebugPanel;
import view.ui.top.UIPanelTop;

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


    public void inject(Modal<MoreOptionsModal> moreOptionsModal) {
        log.debug("Injecting button into game ui");
        GButt.ButtPanel moreOptionsButton = new GButt.ButtPanel(SPRITES.icons().s.cog) {
            @Override
            protected void clickA() {
                moreOptionsModal.show();
            }
        };

        moreOptionsButton.hoverInfoSet(MOD_INFO.name);
        moreOptionsButton.setDim(32, UIPanelTop.HEIGHT);

        // inject button for opening modal into game UI
        gameApis.uiApi().findUIElementInWorldView(UIPanelTop.class)
            .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
            .ifPresent(o -> {
                log.debug("Injecting button into UIPanelTop#right in settlement view");
                GuiSection right = (GuiSection) o;
                right.addRelBody(8, DIR.W, moreOptionsButton);
            });

        gameApis.uiApi().findUIElementInSettlementView(UIPanelTop.class)
            .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
            .ifPresent(o -> {
                log.debug("Injecting button into UIPanelTop#right in world view");
                GuiSection right = (GuiSection) o;
                right.addRelBody(8, DIR.W, moreOptionsButton);
            });
    }

    /**
     * Debug commands are executable via the in game debug panel
     */
    public void initDebug(Modal<MoreOptionsModal> moreOptionsModal, ConfigStore configStore) {
        log.debug("Initialize %s Debug Commands", MOD_INFO.name);
        IDebugPanel.add(MOD_INFO.name + ":show", moreOptionsModal::show);
        IDebugPanel.add(MOD_INFO.name + ":metrics:buffer", () -> MetricCollector.getInstance().buffer());
        IDebugPanel.add(MOD_INFO.name + ":metrics:flush", () -> MetricCollector.getInstance().flush());
        IDebugPanel.add(MOD_INFO.name + ":metrics:export", () -> MetricExporter.getInstance().export());
        IDebugPanel.add(MOD_INFO.name + ":metrics:start", () -> MetricScheduler.getInstance().start());
        IDebugPanel.add(MOD_INFO.name + ":metrics:stop", () -> MetricScheduler.getInstance().stop());
        IDebugPanel.add(MOD_INFO.name + ":createBackup", configStore::createBackupConfig);
        IDebugPanel.add(MOD_INFO.name + ":log.stats", () -> {
            log.info("Events Status:\n%s", gameApis.eventsApi().readEventsEnabledStatus()
                .entrySet().stream().map(entry -> entry.getKey() + " enabled: " + entry.getValue() + "\n")
                .collect(Collectors.joining()));
        });
    }

    public void initForBackup(
        Modal<BackupModal> backupModal,
        Modal<MoreOptionsModal> backupMoreOptionsModal,
        Modal<MoreOptionsModal> moreOptionsModal,
        MoreOptionsConfig config
    ) {
        init(backupMoreOptionsModal, config);

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
            configStore.deleteBackupConfig();
            backupMoreOptionsModal.hide();
        });

        // Ok
        Button okButton = Optional.ofNullable(backupMoreOptionsModal.getSection().getOkButton())
            .orElseThrow(() -> new UninitializedException("BackupModal is not initialized."));
        okButton.clickActionSet(() -> {
            applyAndSave(backupMoreOptionsModal.getSection());
            moreOptionsModal.getSection().applyConfig(backupMoreOptionsModal.getSection().getConfig());
            configStore.deleteBackupConfig();
            backupMoreOptionsModal.hide();
        });

        // Edit Backup
        backupModal.getSection().getEditButton().clickActionSet(() -> {
            backupMoreOptionsModal.getSection().applyConfig(config);
            backupModal.hide();
            backupMoreOptionsModal.show();
        });

        // Close: Backup Modal
        backupModal.getPanel().setCloseAction(() -> {
            configStore.deleteBackupConfig();
            MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
            moreOptionsModal.getSection().applyConfig(defaultConfig);
            configurator.applyConfig(defaultConfig);
            configStore.setCurrentConfig(defaultConfig);
            configStore.saveConfig(defaultConfig);
            backupModal.hide();
        });

        // Discard Backup
        backupModal.getSection().getDiscardButton().clickActionSet(() -> {
            configStore.deleteBackupConfig();
            MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
            moreOptionsModal.getSection().applyConfig(defaultConfig);
            configurator.applyConfig(defaultConfig);
            configStore.setCurrentConfig(defaultConfig);
            configStore.saveConfig(defaultConfig);
            backupModal.hide();
        });

        // Apply Backup
        backupModal.getSection().getApplyButton().clickActionSet(() -> {
            configurator.applyConfig(config);
            moreOptionsModal.getSection().applyConfig(config);
            configStore.setCurrentConfig(config);
            configStore.saveConfig(config);
            configStore.deleteBackupConfig();
            backupModal.hide();
        });
    }


    /**
     * Generates UI with available config.
     * Adds config modal buttons functionality.
     *
     * @param config used to generate the UI
     */
    public void init(Modal<MoreOptionsModal> moreOptionsModal, MoreOptionsConfig config) {
        log.debug("Initialize %s UI", MOD_INFO.name);

        List<BoostersPanel.Entry> boosterEntries = config.getBoosters().entrySet().stream()
            .map(entry -> BoostersPanel.Entry.builder()
                .key(entry.getKey())
                .range(entry.getValue())
                .cat(gameApis.boosterApi().getCat(entry.getKey()))
                .build())
            .collect(Collectors.toList());

        moreOptionsModal.getSection().init(config, boosterEntries);
        moreOptionsModal.center();

        // Cancel & Undo
        Button cancelButton = Optional.ofNullable(moreOptionsModal.getSection().getCancelButton())
            .orElseThrow(() -> new UninitializedException("MoreOptionsModal is not initialized."));
        cancelButton.clickActionSet(() -> {
            undo(moreOptionsModal.getSection());
            moreOptionsModal.hide();
        });

        // Apply & Save
        Button applyButton = Optional.ofNullable(moreOptionsModal.getSection().getApplyButton())
            .orElseThrow(() -> new UninitializedException("MoreOptionsModal is not initialized."));
        applyButton.clickActionSet(() -> {
            applyAndSave(moreOptionsModal.getSection());
        });

        // Reset UI to default
        Button resetButton = Optional.ofNullable(moreOptionsModal.getSection().getResetButton())
            .orElseThrow(() -> new UninitializedException("MoreOptionsModal is not initialized."));
        resetButton.clickActionSet(() -> {
            MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
            moreOptionsModal.getSection().applyConfig(defaultConfig);
        });

        // Undo changes
        Button undoButton = Optional.ofNullable(moreOptionsModal.getSection().getUndoButton())
            .orElseThrow(() -> new UninitializedException("MoreOptionsModal is not initialized."));
        undoButton.clickActionSet(() -> undo(moreOptionsModal.getSection()));

        //Ok: Apply & Save & Exit
        Button okButton = Optional.ofNullable(moreOptionsModal.getSection().getOkButton())
            .orElseThrow(() -> new UninitializedException("MoreOptionsModal is not initialized."));
        okButton.clickActionSet(() -> {
            applyAndSave(moreOptionsModal.getSection());
            moreOptionsModal.hide();
        });

        // opens folder with mod configuration
        Button folderButton = Optional.ofNullable(moreOptionsModal.getSection().getFolderButton())
            .orElseThrow(() -> new UninitializedException("MoreOptionsModal is not initialized."));
        folderButton.clickActionSet(() -> {
            FileManager.openDesctop(PATHS.local().SETTINGS.get().toString());
        });

        moreOptionsModal.onShow(Modal::refresh);

        MetricsPanel metricsPanel = Optional.ofNullable(moreOptionsModal.getSection().getMetricsPanel())
            .orElseThrow(() -> new UninitializedException("MoreOptionsModal is not initialized."));
        moreOptionsModal.getSection().getMetricsPanel().onRefresh(panel -> {
            MoreOptionsConfig moreOptionsConfig = configStore.getCurrentConfig()
                .orElse(configStore.getDefaultConfig());
            String exportFilePath = metricExporter.getExportFilePath().toString();
            List<String> keyList = metricCollector.getKeyList();
            List<String> stats = moreOptionsConfig.getMetrics().getStats();

            log.debug("Refreshing metrics panel");
            log.trace("Refresh values:\n  exportFilePath: %s\n  keyList: %s\n  stats: %s", exportFilePath, keyList, stats);

            panel.refresh(
                exportFilePath,
                keyList,
                stats);
        });
    }

    private void applyAndSave(MoreOptionsModal moreOptionsModal) {
        // only save when changes were made
        if (moreOptionsModal.isDirty()) {
            MoreOptionsConfig config = moreOptionsModal.getConfig();
            configurator.applyConfig(config);
            configStore.setCurrentConfig(config);
            configStore.saveConfig(config);
        }
    }

    private void undo(MoreOptionsModal moreOptionsModal) {
        moreOptionsModal.getConfigStore().getCurrentConfig().ifPresent(moreOptionsModal::applyConfig);
    }
}
