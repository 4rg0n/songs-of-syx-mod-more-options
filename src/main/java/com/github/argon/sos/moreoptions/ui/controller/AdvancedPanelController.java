package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV4Config;
import com.github.argon.sos.moreoptions.ui.MoreOptionsPanel;
import com.github.argon.sos.moreoptions.ui.tab.advanced.AdvancedTab;
import init.paths.PATHS;
import snake2d.Errors;
import snake2d.util.file.FileManager;

import java.nio.file.Path;

public class AdvancedPanelController extends AbstractUiController<AdvancedTab> {

    private final MoreOptionsPanel moreOptionsPanel;

    public AdvancedPanelController(AdvancedTab advancedTab, MoreOptionsPanel moreOptionsPanel) {
        super(advancedTab);
        this.moreOptionsPanel = moreOptionsPanel;

        advancedTab.getDumpLogsButton().clickActionSet(this::dumpLogs);
        advancedTab.getGameLogsFolderButton().clickActionSet(this::openGameLogsFolder);
        advancedTab.getResetButton().clickActionSet(this::resetModConfig);
        advancedTab.getFolderButton().clickActionSet(this::openModConfigFolder);
        advancedTab.getCopySaveStampButton().clickActionSet(this::copySaveStamp);
        advancedTab.getCopyWorldSeedButton().clickActionSet(this::copyWorldSeed);
    }

    @Override
    public void onGameSaved(Path saveFilePath) {
        element.refresh(gameApis.save().getSaveStamp());
    }

    public void dumpLogs() {
        try {
            Errors.forceDump(i18n.t("MoreOptionsPanel.text.logs.dump"));
            notificator.notifySuccess(i18n.t("notification.logs.dump"));
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.logs.not.dump"), e);
        }
    }

    public void openGameLogsFolder() {
        try {
            FileManager.openDesctop(PATHS.local().LOGS.get().toString());
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.logs.folder.not.open"), e);
        }
    }

    public void openModConfigFolder() {
        try {
            FileManager.openDesctop(ConfigDefaults.CONFIGE_PATH.toString());
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.config.folder.not.open", ConfigDefaults.CONFIGE_PATH), e);
        }
    }

    public void resetModConfig() {
        // are you sure message
        gameApis.ui().inters().yesNo.activate(i18n.t("MoreOptionsPanel.text.yesNo.reset"), () -> {
            MoreOptionsV4Config defaultConfig = configStore.getDefaultConfig();
            try {
                moreOptionsPanel.setValue(defaultConfig);
                configStore.clear();
                if (applyAndSave(moreOptionsPanel)) {
                    notificator.notifySuccess(i18n.t("notification.config.reset"));
                } else {
                    notificator.notifyError(i18n.t("notification.config.not.reset"));
                }

            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.config.not.reset"), e);
            }
        }, () -> {}, true);
    }

    public void copySaveStamp() {
        String saveStamp = element.getSaveStamp();
        copyToClipboard(
            saveStamp,
            i18n.t("notification.advanced.saveStamp.copy", saveStamp),
            i18n.t("notification.advanced.saveStamp.not.copy")
        );
    }

    public void copyWorldSeed() {
        int worldSeed = element.getWorldSeed();
        copyToClipboard(
            String.valueOf(worldSeed),
            i18n.t("notification.advanced.worldSeed.copy", worldSeed),
            i18n.t("notification.advanced.worldSeed.not.copy")
        );
    }
}
