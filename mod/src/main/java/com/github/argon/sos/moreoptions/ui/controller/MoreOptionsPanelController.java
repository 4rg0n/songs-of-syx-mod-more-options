package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.mod.sdk.ui.FullWindow;
import com.github.argon.sos.mod.sdk.util.Clipboard;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.ui.MoreOptionsPanel;
import com.github.argon.sos.moreoptions.ui.msg.Messages;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;

public class MoreOptionsPanelController extends AbstractUiController<MoreOptionsPanel> {

    private final FullWindow<MoreOptionsPanel> moreOptionsWindow;

    public MoreOptionsPanelController(MoreOptionsPanel moreOptionsPanel, FullWindow<MoreOptionsPanel> moreOptionsWindow) {
        super(moreOptionsPanel);
        this.moreOptionsWindow = moreOptionsWindow;

        moreOptionsPanel.getCancelButton().clickActionSet(this::cancelAndUndo);
        moreOptionsPanel.getApplyButton().clickActionSet(this::applyAndSave);
        moreOptionsPanel.getUndoButton().clickActionSet(this::undo);
        moreOptionsPanel.getReloadButton().clickActionSet(this::reloadAndApply);
        moreOptionsPanel.getShareButton().clickActionSet(this::copyMoreOptionsConfigToClipboard);
        moreOptionsPanel.getDefaultTabButton().clickActionSet(this::resetTabToDefaultConfig);
        moreOptionsPanel.getDefaultAllButton().clickActionSet(this::resetEverythingToDefaultConfig);
        moreOptionsPanel.getOkButton().clickActionSet(this::applyAndSaveAndExit);
    }

    public void cancelAndUndo() {
        try {
            undo(element);
        } catch (Exception e) {
            messages.errorDialog(e, "notification.config.not.undo");
            return;
        }

        moreOptionsWindow.hide();
    }

    public void applyAndSave() {
        try {
            if (!applyAndSave(element)) {
                messages.notifyError("notification.config.not.apply");
            }
        } catch (Exception e) {
            messages.errorDialog(e, "notification.config.not.apply");
        }
    }

    public void undo() {
        try {
            undo(element);
        } catch (Exception e) {
            messages.errorDialog(e, "notification.config.not.undo");
        }
    }

    public void reloadAndApply() {
        MoreOptionsV5Config moreOptionsConfig = configStore.reloadConfig().orElse(null);
        if (moreOptionsConfig != null) {
            element.setValue(moreOptionsConfig);
            messages.notifySuccess(i18n.t("notification.config.reload"));
        } else {
            messages.notifyError("notification.config.not.reload");
        }
    }

    public void copyMoreOptionsConfigToClipboard() {
        MoreOptionsV5Config moreOptionsConfig = element.getValue();

        if (moreOptionsConfig == null) {
            messages.notifyError("notification.config.not.copy");
            return;
        }

        try {
            Clipboard.write(moreOptionsConfig.toJson());
            messages.notifySuccess("notification.config.copy");
        } catch (Exception e) {
            messages.errorDialog(e, "notification.config.not.copy");
        }
    }

    public void resetTabToDefaultConfig() {
        try {
            AbstractConfigTab<?, ?> abstractConfigTab = element.getTabulator().getActiveTab();
            if (abstractConfigTab != null) {
                abstractConfigTab.resetToDefault();
                messages.notifySuccess("notification.config.default.tab.apply", abstractConfigTab.getTitle());
            } else {
                messages.notifyError("notification.config.default.tab.not.apply");
            }
        } catch (Exception e) {
            messages.errorDialog(e, "notification.config.default.tab.not.apply");
        }
    }

    public void resetEverythingToDefaultConfig() {
        try {
            element.setValue(configStore.getDefaultConfig());
            messages.notifySuccess("notification.config.default.all.apply");
        } catch (Exception e) {
            messages.errorDialog(e, "notification.config.default.all.not.apply");
        }
    }

    public void applyAndSaveAndExit() {
        applyAndSave();
        moreOptionsWindow.hide();
    }
}
