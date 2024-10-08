package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.mod.sdk.ui.Window;
import com.github.argon.sos.mod.sdk.util.Clipboard;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import com.github.argon.sos.moreoptions.ui.msg.ErrorDialog;

public class ErrorDialogController extends AbstractUiController<ErrorDialog> {
    private final Window<ErrorDialog> errorDialog;
    private final String reportUrl;

    public ErrorDialogController(Window<ErrorDialog> errorDialog, String reportUrl) {
        super(errorDialog.getSection());
        this.errorDialog = errorDialog;
        this.reportUrl = reportUrl;

        errorDialog.getSection().getCloseButton().clickActionSet(this::close);
        errorDialog.getSection().getCopyButton().clickActionSet(this::copy);
        errorDialog.getSection().getReportButton().clickActionSet(this::report);
        errorDialog.getSection().getCleanButton().clickActionSet(this::clean);
    }

    public void clean() {
        try {
            boolean success = configStore.deleteBackupOriginals();

            if (success) {
                messages.notifySuccess("notification.error.clean");
            } else {
                messages.notifyError("notification.error.not.clean");
            }
        } catch (Exception e) {
            messages.errorDialog(e, "notification.error.not.clean");
        }
    }

    public void report() {
        openWebsite(reportUrl);
    }

    public void copy() {
        Throwable exception = errorDialog.getSection().getException();
        String errorMessage = errorDialog.getSection().getErrorMessage();

        try {
            Clipboard.write(errorMessage + "\n\n" + StringUtil.stringify(exception));
            messages.notifySuccess("notification.error.copy");
        } catch (Exception e) {
            messages.errorDialog(e, "notification.error.not.copy");
        }
    }

    public void close() {
        errorDialog.hide();
    }
}
