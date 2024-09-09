package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.ui.msg.Messages;
import com.github.argon.sos.moreoptions.ui.tab.metrics.MetricsTab;
import com.github.argon.sos.mod.sdk.util.Clipboard;
import snake2d.util.file.FileManager;

import java.nio.file.Path;

public class MetricsPanelController extends AbstractUiController<MetricsTab> {
    public MetricsPanelController(MetricsTab metricsTab) {
        super(metricsTab);

        metricsTab.getExportFolderButton().clickActionSet(this::openMetricsExportFolder);
        metricsTab.getCopyExportFileButton().clickActionSet(this::copyMetricExportFilename);
    }

    public void openMetricsExportFolder() {
        Path exportFolderPath = element.getExportFolderPath();
        if (!exportFolderPath.toFile().exists()) {
            messages.notifyError("notification.metrics.folder.not.exists", exportFolderPath);
            return;
        }

        try {
            FileManager.openDesctop(exportFolderPath.toString());
        } catch (Exception e) {
            messages.errorDialog(e, "notification.metrics.folder.not.open", exportFolderPath);
        }
    }

    public void copyMetricExportFilename() {
        Path exportFilePath = element.getExportFilePath();

        if (exportFilePath == null) {
            messages.notifyError("notification.metrics.file.path.not.copy");
            return;
        }

        try {
            Clipboard.write(exportFilePath.toString());
            messages.notifySuccess("notification.metrics.file.path.copy", exportFilePath);
        } catch (Exception e) {
            messages.errorDialog(e, "notification.metrics.file.path.not.copy");
        }
    }
}
