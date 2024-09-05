package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.game.ui.FullWindow;
import com.github.argon.sos.moreoptions.game.ui.Window;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.BackupDialog;
import com.github.argon.sos.moreoptions.ui.MoreOptionsPanel;
import com.github.argon.sos.moreoptions.ui.msg.Message;
import org.jetbrains.annotations.Nullable;

import static com.github.argon.sos.moreoptions.MoreOptionsScript.MOD_INFO;

public class BackupPanelController extends AbstractUiController<MoreOptionsPanel> {
    private final static Logger log = Loggers.getLogger(BackupPanelController.class);

    @Nullable
    private final MoreOptionsV5Config backupConfig;
    private final Window<BackupDialog> backupDialog;
    private final MoreOptionsPanel backupMoreOptionsPanel;
    private final MoreOptionsPanel moreOptionsPanel;
    private final FullWindow<MoreOptionsPanel> backupMoreOptionsWindow;

    public BackupPanelController(
        FullWindow<MoreOptionsPanel> backupMoreOptionsWindow,
        Window<BackupDialog> backupDialog,
        MoreOptionsPanel moreOptionsPanel
    ) {
        super(backupMoreOptionsWindow.getSection());
        this.backupDialog = backupDialog;
        this.moreOptionsPanel = moreOptionsPanel;
        this.backupMoreOptionsPanel = backupMoreOptionsWindow.getSection();
        this.backupMoreOptionsWindow = backupMoreOptionsWindow;
        this.backupConfig = configStore.getBackup().orElse(null);

        backupMoreOptionsWindow.hideAction(this::closeWindow);
        backupMoreOptionsPanel.getCancelButton().clickActionSet(this::cancelAndUndo);
        backupMoreOptionsPanel.getOkButton().clickActionSet(this::applyAndSaveAndExit);

        backupDialog.getSection().getEditButton().clickActionSet(this::editBackup);
        backupDialog.getPanel().setCloseAction(this::closeDialog);
        backupDialog.getSection().getDiscardButton().clickActionSet(this::closeDialog);
        backupDialog.getSection().getApplyButton().clickActionSet(this::applyBackup);
    }

    public void closeWindow() {
        try {
            configStore.deleteBackups();
        } catch (Exception e) {
            Message.errorDialog(e, "notification.backup.config.not.delete");
        }

        backupDialog.show();
    }

    public void closeDialog() {
        try {
            configStore.deleteBackups();
            MoreOptionsV5Config defaultConfig = configStore.getDefaultConfig();
            moreOptionsPanel.setValue(defaultConfig);
            configApplier.applyToGameAndSave(defaultConfig);
        } catch (Exception e) {
            Message.errorDialog(e, "notification.backup.config.not.applyDefault");
        }

        backupDialog.hide();
    }
    public void cancelAndUndo() {
        try {
            undo(backupMoreOptionsPanel);
        } catch (Exception e) {
            Message.errorDialog(e, "notification.config.not.undo");
            return;
        }

        backupMoreOptionsWindow.hide();
    }

    public void applyAndSaveAndExit() {
        MoreOptionsV5Config readConfig = backupMoreOptionsPanel.getValue();

        // fallback
        if (readConfig == null) {
            readConfig = configStore.getDefaultConfig();
        }

        try {
            moreOptionsPanel.setValue(readConfig);
            configStore.deleteBackups();
        } catch (Exception e) {
            Message.errorDialog(e, "notification.backup.config.not.apply", MOD_INFO.name);
            return;
        }

        backupMoreOptionsWindow.hide();
    }

    public void editBackup() {
        backupDialog.hide();

        try {
            backupMoreOptionsPanel.setValue(backupConfig);
        } catch (Exception e) {
            Message.errorDialog(e, "notification.backup.config.not.edit", MOD_INFO.name);
            return;
        }

        backupMoreOptionsWindow.show();
    }

    public void applyBackup() {
        try {
            if (backupConfig == null) {
                backupDialog.hide();
                return;
            }

            configApplier.applyToGameAndSave(backupConfig);
            moreOptionsPanel.setValue(backupConfig);
            configStore.deleteBackups();
        } catch (Exception e) {
            Message.errorDialog(e, "notification.backup.config.not.apply", MOD_INFO.name);
        }

        backupDialog.hide();
    }
}
