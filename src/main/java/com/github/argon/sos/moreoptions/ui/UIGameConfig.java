package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.MoreOptionsConfigurator;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.Modal;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import init.paths.ModInfo;
import init.paths.PATHS;
import init.sprite.SPRITES;
import lombok.RequiredArgsConstructor;
import snake2d.util.datatypes.DIR;
import snake2d.util.file.FileManager;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;
import view.interrupter.IDebugPanel;
import view.ui.UIPanelTop;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.argon.sos.moreoptions.MoreOptionsScript.MOD_INFO;

/**
 * Most UI elements are generated dynamically dictated by the given config {@link MoreOptionsConfig}.
 * So when a new entry is added, a new UI element like e.g. an additional slider will also be visible.
 */
@RequiredArgsConstructor
public class UIGameConfig {

    private final static Logger log = Loggers.getLogger(UIGameConfig.class);

    private final ModInfo modInfo;

    private final GameApis gameApis;

    private final MoreOptionsConfigurator configurator;

    private final ConfigStore configStore;


    public void inject(Modal<MoreOptionsModal> moreOptionsModal) {
        log.debug("Injecting button into game ui");
        GButt.ButtPanel settlementButton = new GButt.ButtPanel(SPRITES.icons().s.cog) {
            @Override
            protected void clickA() {
                moreOptionsModal.show();
            }
        };

        settlementButton.hoverInfoSet(MOD_INFO.name);
        settlementButton.setDim(32, UIPanelTop.HEIGHT);

        // inject button for opening modal into game UI
        gameApis.uiApi().findUIElementInWorldView(UIPanelTop.class)
            .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
            .ifPresent(o -> {
                log.debug("Injecting into UIPanelTop#right in settlement view");
                GuiSection right = (GuiSection) o;
                right.addRelBody(8, DIR.W, settlementButton);
            });

        gameApis.uiApi().findUIElementInSettlementView(UIPanelTop.class)
            .flatMap(uiPanelTop -> ReflectionUtil.getDeclaredField("right", uiPanelTop))
            .ifPresent(o -> {
                log.debug("Injecting into UIPanelTop#right in world view");
                GuiSection right = (GuiSection) o;
                right.addRelBody(8, DIR.W, settlementButton);
            });

        // todo: add panel button to world ui
    }

    /**
     * Debug commands are executable via the in game debug panel
     */
    public void initDebug(Modal<MoreOptionsModal> moreOptionsModal, ConfigStore configStore) {
        log.debug("Initialize %s Debug Commands", MOD_INFO.name);
        IDebugPanel.add(MOD_INFO.name + ":show", moreOptionsModal::show);
        IDebugPanel.add(MOD_INFO.name + ":createBackup", configStore::createBackupConfig);
        IDebugPanel.add(MOD_INFO.name + ":log.stats", () -> {
            log.info("Events Status:\n%s", gameApis.eventsApi().readEventsEnabledStatus()
                .entrySet().stream().map(entry -> entry.getKey() + " enabled: " + entry.getValue() + "\n")
                .collect(Collectors.joining()));
        });
    }

    public void initForBackup(Modal<BackupModal> backupModal, Modal<MoreOptionsModal> backupMoreOptionsModal, Modal<MoreOptionsModal> moreOptionsModal, MoreOptionsConfig config) {
        init(backupMoreOptionsModal, config);

        // Close: More Options modal with backup config
        backupMoreOptionsModal.getPanel().setCloseAction(() -> {
            configStore.deleteBackupConfig();
            backupMoreOptionsModal.hide();
            backupModal.show();
        });

        // Cancel & Undo
        backupMoreOptionsModal.getSection().getCancelButton().clickActionSet(() -> {
            undo(backupMoreOptionsModal.getSection());
            configStore.deleteBackupConfig();
            backupMoreOptionsModal.hide();
        });

        // Ok
        backupMoreOptionsModal.getSection().getOkButton().clickActionSet(() -> {
            applyAndSave(backupMoreOptionsModal.getSection());
            moreOptionsModal.getSection().applyConfig(backupMoreOptionsModal.getSection().getConfig());
            configStore.deleteBackupConfig();
            backupMoreOptionsModal.hide();
        });

        // Edit Backup
        backupModal.getSection().getEditButton().clickActionSet(() -> {
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


        List<BoostersPanel.Entry> boosterEntries = config.getBoosters().entrySet().stream().map(entry ->
            BoostersPanel.Entry.builder()
                .key(entry.getKey())
                .range(entry.getValue())
                .enemy(gameApis.boosterApi().isEnemyBooster(entry.getKey()))
                .player(gameApis.boosterApi().isPlayerBooster(entry.getKey()))
                .build()
        ).collect(Collectors.toList());

        moreOptionsModal.getSection().init(config, boosterEntries);
        moreOptionsModal.center();

        // Cancel & Undo
        moreOptionsModal.getSection().getCancelButton().clickActionSet(() -> {
            undo(moreOptionsModal.getSection());
            moreOptionsModal.hide();
        });

        // Apply & Save
        moreOptionsModal.getSection().getApplyButton().clickActionSet(() -> {
            applyAndSave(moreOptionsModal.getSection());
        });

        // Reset UI to default
        moreOptionsModal.getSection().getResetButton().clickActionSet(() -> {
            MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
            moreOptionsModal.getSection().applyConfig(defaultConfig);
        });

        // Undo changes
        moreOptionsModal.getSection().getUndoButton().clickActionSet(() -> undo(moreOptionsModal.getSection()));

        //Ok: Apply & Save & Exit
        moreOptionsModal.getSection().getOkButton().clickActionSet(() -> {
            applyAndSave(moreOptionsModal.getSection());
            moreOptionsModal.hide();
        });

        // opens folder with mod configuration
        moreOptionsModal.getSection().getFolderButton().clickActionSet(() -> {
            FileManager.openDesctop(PATHS.local().SETTINGS.get().toString());
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
