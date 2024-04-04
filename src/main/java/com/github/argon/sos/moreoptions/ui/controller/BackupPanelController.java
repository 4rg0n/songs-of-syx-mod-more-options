package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV4Config;
import com.github.argon.sos.moreoptions.game.ui.FullWindow;
import com.github.argon.sos.moreoptions.game.ui.Modal;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.BackupDialog;
import com.github.argon.sos.moreoptions.ui.MoreOptionsPanel;
import org.jetbrains.annotations.Nullable;

import static com.github.argon.sos.moreoptions.MoreOptionsScript.MOD_INFO;

public class BackupPanelController extends AbstractUiController<MoreOptionsPanel> {
    private final static Logger log = Loggers.getLogger(BackupPanelController.class);

    @Nullable
    private final MoreOptionsV4Config backupConfig;
    private final Modal<BackupDialog> backupDialog;
    private final MoreOptionsPanel backupMoreOptionsPanel;
    private final MoreOptionsPanel moreOptionsPanel;
    private final FullWindow<MoreOptionsPanel> backupMoreOptionsWindow;

    public BackupPanelController(
        FullWindow<MoreOptionsPanel> backupMoreOptionsWindow,
        Modal<BackupDialog> backupDialog,
        MoreOptionsPanel moreOptionsPanel
    ) {
        super(backupMoreOptionsWindow.getSection());
        this.backupDialog = backupDialog;
        this.moreOptionsPanel = moreOptionsPanel;
        this.backupMoreOptionsPanel = backupMoreOptionsWindow.getSection();
        this.backupMoreOptionsWindow = backupMoreOptionsWindow;
        this.backupConfig = configStore.getBackup().orElse(null);

        backupMoreOptionsWindow.hideAction(panel -> this.closeWindow());
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
            log.error("Could not delete backup config", e);
        }

        backupDialog.show();
    }

    public void closeDialog() {
        try {
            configStore.deleteBackups();
            MoreOptionsV4Config defaultConfig = configStore.getDefaultConfig();
            moreOptionsPanel.setValue(defaultConfig);
            configApplier.applyToGameAndSave(defaultConfig);
        } catch (Exception e) {
            log.error("Could not apply default config via backup dialog", e);
        }

        backupDialog.hide();
    }
    public void cancelAndUndo() {
        try {
            undo(backupMoreOptionsPanel);
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.config.not.undo"), e);
            return;
        }

        backupMoreOptionsWindow.hide();
    }

    public void applyAndSaveAndExit() {
        MoreOptionsV4Config readConfig = backupMoreOptionsPanel.getValue();

        // fallback
        if (readConfig == null) {
            readConfig = configStore.getDefaultConfig();
        }

        try {
            moreOptionsPanel.setValue(readConfig);
            configStore.deleteBackups();
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.backup.config.not.apply", MOD_INFO.name), e);
            return;
        }

        backupMoreOptionsWindow.hide();
    }

    public void editBackup() {
        backupDialog.hide();

        try {
            backupMoreOptionsPanel.setValue(backupConfig);
        } catch (Exception e) {
            log.error("Could not apply backup config for editing", e);
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
            log.error("Could not apply backup config", e);
        }

        backupDialog.hide();
    }
}
