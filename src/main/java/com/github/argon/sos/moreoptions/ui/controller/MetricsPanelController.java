package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.ui.panel.metrics.MetricsPanel;
import com.github.argon.sos.moreoptions.util.Clipboard;
import snake2d.util.file.FileManager;

import java.nio.file.Path;

public class MetricsPanelController extends AbstractUiController<MetricsPanel> {
    public MetricsPanelController(MetricsPanel metricsPanel) {
        super(metricsPanel);

        metricsPanel.getExportFolderButton().clickActionSet(this::openMetricsExportFolder);
        metricsPanel.getCopyExportFileButton().clickActionSet(this::copyMetricExportFilename);
    }

    public void openMetricsExportFolder() {
        Path exportFolderPath = element.getExportFolderPath();
        if (!exportFolderPath.toFile().exists()) {
            notificator.notifyError(i18n.t("notification.metrics.folder.not.exists", exportFolderPath));
            return;
        }

        try {
            FileManager.openDesctop(exportFolderPath.toString());
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.metrics.folder.not.open", exportFolderPath), e);
        }
    }

    public void copyMetricExportFilename() {
        Path exportFilePath = element.getExportFilePath();

        try {
            if (exportFilePath != null) {
                boolean written = Clipboard.write(exportFilePath.toString());

                if (written) {
                    notificator.notifySuccess(i18n.t("notification.metrics.file.path.copy", exportFilePath));
                } else {
                    notificator.notifyError(i18n.t("notification.metrics.file.path.not.copy"));
                }
            } else {
                notificator.notifyError(i18n.t("notification.metrics.file.path.not.copy"));
            }
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.metrics.file.path.not.copy"), e);
        }
    }
}
