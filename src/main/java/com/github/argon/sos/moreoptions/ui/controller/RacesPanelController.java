package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.game.ui.Window;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.JsonMapper;
import com.github.argon.sos.moreoptions.json.JsonWriter;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.ui.panel.races.RacesPanel;
import com.github.argon.sos.moreoptions.ui.panel.races.RacesSelectionPanel;
import com.github.argon.sos.moreoptions.util.Clipboard;
import snake2d.util.file.FileManager;

import java.nio.file.Path;
import java.util.Objects;

public class RacesPanelController extends AbstractUiController<RacesPanel> {
    public RacesPanelController(RacesPanel racesPanel) {
        super(racesPanel);

        racesPanel.getFileButton().clickActionSet(this::openCurrentRacesConfigFile);
        racesPanel.getFolderButton().clickActionSet(this::openRacesConfigFolder);
        racesPanel.getExportButton().clickActionSet(this::exportRacesConfigToClipboard);
        racesPanel.getImportButton().clickActionSet(this::importRacesConfigFromClipboard);
        racesPanel.getLoadButton().clickActionSet(this::openRacesConfigSelection);
    }

    public void openCurrentRacesConfigFile() {
        Path path = configStore.racesConfigPath();
        if (!path.toFile().exists()) {
            notificator.notify(i18n.t("notification.races.file.not.exists", path));
            return;
        }

        try {
            FileManager.openDesctop(path.toString());
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.races.file.not.open", path), e);
        }
    }

    public void openRacesConfigFolder() {
        if (!ConfigStore.RACES_CONFIG_PATH.toFile().exists()) {
            notificator.notifyError(i18n.t("notification.races.folder.not.exists", ConfigStore.RACES_CONFIG_PATH));
            return;
        }

        try {
            FileManager.openDesctop(ConfigStore.RACES_CONFIG_PATH.toString());
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.races.folder.not.open", ConfigStore.RACES_CONFIG_PATH), e);
        }
    }

    public void exportRacesConfigToClipboard() {
        try {
            RacesConfig racesConfig = element.getValue();
            JsonElement jsonElement = JsonMapper.mapObject(racesConfig);
            Json json = new Json(jsonElement, JsonWriter.getJsonE());

            if (Clipboard.write(json.toString())) {
                notificator.notifySuccess(i18n.t("notification.races.config.copy"));
            } else {
                notificator.notifyError(i18n.t("notification.races.config.not.copy"));
            }
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.races.config.not.copy"), e);
        }
    }

    public void importRacesConfigFromClipboard() {
        try {
            Clipboard.read().ifPresent(s -> {
                Json json = new Json(s, JsonWriter.getJsonE());
                RacesConfig racesConfig = JsonMapper.mapJson(json.getRoot(), RacesConfig.class);

                element.setValue(racesConfig);
                notificator.notifySuccess(i18n.t("notification.races.config.import"));
            });
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.races.config.not.import"));
        }
    }

    public void openRacesConfigSelection() {
        Window<RacesSelectionPanel> racesConfigsSelection = uiFactory.buildRacesConfigSelection("Select a file");
        // load from race config file on doubleclick
        racesConfigsSelection.getSection().getRacesConfigTable().doubleClickAction(row -> {
            try {
                Objects.requireNonNull(row);
                RacesSelectionPanel.Entry entry = row.getValue();
                Path configPath = entry.getConfigPath();
                RacesConfig racesConfig = configStore.loadRaceConfig(configPath).orElse(null);

                if (racesConfig != null) {
                    element.setValue(racesConfig);
                    notificator.notifySuccess(i18n.t("notification.races.config.load"));
                    racesConfigsSelection.hide();
                } else {
                    notificator.notifyError(i18n.t("notification.races.config.not.load"));
                }
            } catch (Exception e) {
                notificator.notifyError(i18n.t("notification.races.config.not.load"), e);
            }
        });

        racesConfigsSelection.show();
    }
}
