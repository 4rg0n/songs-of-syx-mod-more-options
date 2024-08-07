package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV4Config;
import com.github.argon.sos.moreoptions.game.ui.FullWindow;
import com.github.argon.sos.moreoptions.ui.MoreOptionsPanel;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import com.github.argon.sos.moreoptions.util.Clipboard;

public class MoreOptionsPanelController extends AbstractUiController<MoreOptionsPanel> {

    private final FullWindow<MoreOptionsPanel> moreOptionsWindow;

    public MoreOptionsPanelController(MoreOptionsPanel moreOptionsPanel, FullWindow<MoreOptionsPanel> moreOptionsWindow) {
        super(moreOptionsPanel);
        this.moreOptionsWindow = moreOptionsWindow;

        // update Notificator queue when More Options Modal is rendered
        moreOptionsWindow.renderAction(notificator::update);
        moreOptionsWindow.hideAction(notificator::close);

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
            notificator.notifyError(i18n.t("notification.config.not.undo"), e);
            return;
        }

        moreOptionsWindow.hide();
    }

    public void applyAndSave() {
        try {
            if (!applyAndSave(element)) {
                notificator.notifyError(i18n.t("notification.config.not.apply"));
            }
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.config.not.apply"), e);
        }
    }

    public void undo() {
        try {
            undo(element);
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.config.not.undo"), e);
        }
    }

    public void reloadAndApply() {
        MoreOptionsV4Config moreOptionsConfig = configStore.reloadConfig().orElse(null);
        if (moreOptionsConfig != null) {
            element.setValue(moreOptionsConfig);
            notificator.notifySuccess(i18n.t("notification.config.reload"));
        } else {
            notificator.notifyError(i18n.t("notification.config.not.reload"));
        }
    }

    public void copyMoreOptionsConfigToClipboard() {
        MoreOptionsV4Config moreOptionsConfig = element.getValue();
        try {
            if (moreOptionsConfig != null) {
                boolean written = Clipboard.write(moreOptionsConfig.toJson());

                if (written) {
                    notificator.notifySuccess(i18n.t("notification.config.copy"));
                } else {
                    notificator.notifyError(i18n.t("notification.config.not.copy"));
                }
            } else {
                notificator.notifyError(i18n.t("notification.config.not.copy"));
            }
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.config.not.copy"), e);
        }
    }

    public void resetTabToDefaultConfig() {
        try {
            AbstractConfigTab<?, ?> abstractConfigTab = element.getTabulator().getActiveTab();
            if (abstractConfigTab != null) {
                abstractConfigTab.resetToDefault();
                notificator.notifySuccess(i18n.t("notification.config.default.tab.apply", abstractConfigTab.getTitle()));
            } else {
                notificator.notifyError(i18n.t("notification.config.default.tab.not.apply"));
            }
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.config.default.tab.not.apply"), e);
        }
    }

    public void resetEverythingToDefaultConfig() {
        try {
            element.setValue(configStore.getDefaultConfig());
            notificator.notifySuccess(i18n.t("notification.config.default.all.apply"));
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.config.default.all.not.apply"), e);
        }
    }

    public void applyAndSaveAndExit() {
        applyAndSave();
        moreOptionsWindow.hide();
    }
}
