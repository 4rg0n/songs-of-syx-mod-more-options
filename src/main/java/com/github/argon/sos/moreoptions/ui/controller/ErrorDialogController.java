package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.game.ui.Window;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.ErrorDialog;
import com.github.argon.sos.moreoptions.util.StringUtil;

public class ErrorDialogController extends AbstractUiController<ErrorDialog> {
    private final static Logger log = Loggers.getLogger(ErrorDialogController.class);
    private final Window<ErrorDialog> errorDialog;
    private final String reportUrl;

    public ErrorDialogController(Window<ErrorDialog> errorDialog, String reportUrl) {
        super(errorDialog.getSection());
        this.errorDialog = errorDialog;
        this.reportUrl = reportUrl;

        // update Notificator queue when Error Dialog Modal is rendered
        errorDialog.renderAction(notificator::update);
        errorDialog.hideAction(notificator::close);

        errorDialog.getSection().getCloseButton().clickActionSet(this::close);
        errorDialog.getSection().getCopyButton().clickActionSet(this::copy);
        errorDialog.getSection().getReportButton().clickActionSet(this::report);
    }

    public void report() {
        openWebsite(reportUrl);
    }

    public void copy() {
        Throwable exception = errorDialog.getSection().getException();

        copyToClipboard(
            StringUtil.stringify(exception),
            i18n.t("notification.error.copy"),
            i18n.t("notification.error.not.copy")
        );
    }

    public void close() {
        errorDialog.hide();
    }
}
