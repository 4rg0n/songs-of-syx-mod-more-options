package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.game.ui.Window;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.mapper.JsonMapper;
import com.github.argon.sos.moreoptions.json.writer.JsonWriters;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesTab;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesSelectionPanel;
import com.github.argon.sos.moreoptions.util.Clipboard;
import snake2d.util.file.FileManager;

import java.nio.file.Path;
import java.util.Objects;

public class RacesPanelController extends AbstractUiController<RacesTab> {
    public RacesPanelController(RacesTab racesTab) {
        super(racesTab);

        racesTab.getFileButton().clickActionSet(this::openCurrentRacesConfigFile);
        racesTab.getFolderButton().clickActionSet(this::openRacesConfigFolder);
        racesTab.getExportButton().clickActionSet(this::exportRacesConfigToClipboard);
        racesTab.getImportButton().clickActionSet(this::importRacesConfigFromClipboard);
        racesTab.getLoadButton().clickActionSet(this::openRacesConfigSelection);
    }

    public void openCurrentRacesConfigFile() {
        Path path = configStore.getRacesConfigPath().orElse(null);
        if (path == null || !path.toFile().exists()) {
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
        if (!ConfigDefaults.RACES_CONFIG_FOLDER_PATH.toFile().exists()) {
            notificator.notifyError(i18n.t("notification.races.folder.not.exists", ConfigDefaults.RACES_CONFIG_FOLDER_PATH));
            return;
        }

        try {
            FileManager.openDesctop(ConfigDefaults.RACES_CONFIG_FOLDER_PATH.toString());
        } catch (Exception e) {
            notificator.notifyError(i18n.t("notification.races.folder.not.open", ConfigDefaults.RACES_CONFIG_FOLDER_PATH), e);
        }
    }

    public void exportRacesConfigToClipboard() {
        try {
            RacesConfig racesConfig = element.getValue();
            JsonElement jsonElement = JsonMapper.mapObject(racesConfig);
            Json json = new Json(jsonElement, JsonWriters.jsonEPretty());

            if (Clipboard.write(json.write())) {
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
                Json json = new Json(s, JsonWriters.jsonEPretty());
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
                RacesConfig racesConfig = configStore.loadRacesConfig(configPath).orElse(null);

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
