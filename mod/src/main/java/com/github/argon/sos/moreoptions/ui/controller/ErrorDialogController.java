package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.mod.sdk.game.ui.Window;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Clipboard;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import com.github.argon.sos.moreoptions.ui.msg.ErrorDialog;
import com.github.argon.sos.moreoptions.ui.msg.Message;

public class ErrorDialogController extends AbstractUiController<ErrorDialog> {
    private final static Logger log = Loggers.getLogger(ErrorDialogController.class);
    private final Window<ErrorDialog> errorDialog;
    private final String reportUrl;

    public ErrorDialogController(Window<ErrorDialog> errorDialog, String reportUrl) {
        super(errorDialog.getSection());
        this.errorDialog = errorDialog;
        this.reportUrl = reportUrl;

        errorDialog.getSection().getCloseButton().clickActionSet(this::close);
        errorDialog.getSection().getCopyButton().clickActionSet(this::copy);
        errorDialog.getSection().getReportButton().clickActionSet(this::report);
    }

    public void report() {
        openWebsite(reportUrl);
    }

    public void copy() {
        Throwable exception = errorDialog.getSection().getException();

        try {
            Clipboard.write(StringUtil.stringify(exception));
            Message.notifySuccess("notification.error.copy");
        } catch (Exception e) {
            Message.errorDialog(e, "notification.error.not.copy");
        }
    }

    public void close() {
        errorDialog.hide();
    }
}
