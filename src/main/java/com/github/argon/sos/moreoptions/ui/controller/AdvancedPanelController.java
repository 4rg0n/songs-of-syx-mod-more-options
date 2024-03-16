package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV3Config;
import com.github.argon.sos.moreoptions.ui.MoreOptionsPanel;
import com.github.argon.sos.moreoptions.ui.panel.advanced.AdvancedPanel;
import init.paths.PATHS;
import snake2d.Errors;
import snake2d.util.file.FileManager;

public class AdvancedPanelController extends AbstractUiController<AdvancedPanel> {

    private final MoreOptionsPanel moreOptionsPanel;

    public AdvancedPanelController(AdvancedPanel advancedPanel, MoreOptionsPanel moreOptionsPanel) {
        super(advancedPanel);
        this.moreOptionsPanel = moreOptionsPanel;

        advancedPanel.getDumpLogsButton().clickActionSet(this::dumpLogs);
        advancedPanel.getGameLogsFolderButton().clickActionSet(this::openGameLogsFolder);
        advancedPanel.getResetButton().clickActionSet(this::resetModConfig);
        advancedPanel.getFolderButton().clickActionSet(this::openModConfigFolder);
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
            FileManager.openDesctop(ConfigStore.MORE_OPTIONS_CONFIG_PATH.get().toString());
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.config.folder.not.open", ConfigStore.MORE_OPTIONS_CONFIG_PATH), e);
        }
    }

    public void resetModConfig() {
        // are you sure message
        gameApis.ui().inters().yesNo.activate(i18n.t("MoreOptionsPanel.text.yesNo.reset"), () -> {
            MoreOptionsV3Config defaultConfig = configStore.getDefaultConfig();
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
    }
}
