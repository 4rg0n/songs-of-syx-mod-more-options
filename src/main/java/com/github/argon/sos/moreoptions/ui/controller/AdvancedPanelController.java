package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.ui.MoreOptionsPanel;
import com.github.argon.sos.moreoptions.ui.UiFactory;
import com.github.argon.sos.moreoptions.ui.msg.Message;
import com.github.argon.sos.moreoptions.ui.tab.advanced.AdvancedTab;
import com.github.argon.sos.moreoptions.util.Clipboard;
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
        advancedTab.getUiShowRoomButton().clickActionSet(this::openUiShowRoom);
    }

    @Override
    public void onGameSaved(Path saveFilePath) {
        element.refresh(gameApis.save().getSaveStamp());
    }

    public void dumpLogs() {
        try {
            Errors.forceDump(i18n.t("MoreOptionsPanel.text.logs.dump"));
            Message.notifySuccess("notification.logs.dump");
        } catch (Exception e) {
            Message.errorDialog(e, "notification.logs.not.dump");
        }
    }

    public void openGameLogsFolder() {
        try {
            FileManager.openDesctop(PATHS.local().LOGS.get().toString());
        } catch (Exception e) {
            Message.errorDialog(e, "notification.logs.folder.not.open");
        }
    }

    public void openModConfigFolder() {
        try {
            FileManager.openDesctop(ConfigDefaults.CONFIGE_PATH.toString());
        } catch (Exception e) {
            Message.errorDialog(e, "notification.config.folder.not.open", ConfigDefaults.CONFIGE_PATH);
        }
    }

    public void resetModConfig() {
        // are you sure message
        gameApis.ui().inters().yesNo.activate(i18n.t("MoreOptionsPanel.text.yesNo.reset"), () -> {
            MoreOptionsV5Config defaultConfig = configStore.getDefaultConfig();
            try {
                moreOptionsPanel.setValue(defaultConfig);
                configStore.clear();
                if (applyAndSave(moreOptionsPanel)) {
                    Message.notifySuccess("notification.config.reset");
                } else {
                    Message.notifyError("notification.config.not.reset");
                }

            } catch (Exception e) {
                Message.errorDialog(e, "notification.config.not.reset");
            }
        }, () -> {}, true);
    }

    public void copySaveStamp() {
        String saveStamp = element.getSaveStamp();
        try {
            Clipboard.write(saveStamp);
            Message.notifySuccess("notification.advanced.saveStamp.copy", saveStamp);
        } catch (Exception e) {
            Message.errorDialog(e, "notification.advanced.saveStamp.not.copy");
        }
    }

    public void copyWorldSeed() {
        int worldSeed = element.getWorldSeed();
        try {
            Clipboard.write(String.valueOf(worldSeed));
            Message.notifySuccess("notification.advanced.worldSeed.copy", worldSeed);
        } catch (Exception e) {
            Message.errorDialog(e, "notification.advanced.worldSeed.not.copy");
        }
    }

    public void openUiShowRoom() {
        UiFactory.buildUiShowRoom().show();
    }
}
