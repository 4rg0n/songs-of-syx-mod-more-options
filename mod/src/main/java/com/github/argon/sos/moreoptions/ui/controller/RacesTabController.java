package com.github.argon.sos.moreoptions.ui.controller;

import com.github.argon.sos.mod.sdk.ui.Window;
import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.JsonMapper;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
import com.github.argon.sos.mod.sdk.util.Clipboard;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesSelectionPanel;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesTab;
import snake2d.util.file.FileManager;

import java.nio.file.Path;
import java.util.Objects;

public class RacesTabController extends AbstractUiController<RacesTab> {
    public RacesTabController(RacesTab racesTab) {
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
            messages.notify("notification.races.file.not.exists", path);
            return;
        }

        try {
            FileManager.openDesctop(path.toString());
        } catch (Exception e) {
            messages.errorDialog(e, "notification.races.file.not.open", path);
        }
    }

    public void openRacesConfigFolder() {
        if (!ConfigDefaults.RACES_CONFIG_FOLDER_PATH.toFile().exists()) {
            messages.notifyError("notification.races.folder.not.exists", ConfigDefaults.RACES_CONFIG_FOLDER_PATH);
            return;
        }

        try {
            FileManager.openDesctop(ConfigDefaults.RACES_CONFIG_FOLDER_PATH.toString());
        } catch (Exception e) {
            messages.errorDialog(e, "notification.races.folder.not.open", ConfigDefaults.RACES_CONFIG_FOLDER_PATH);
        }
    }

    public void exportRacesConfigToClipboard() {
        try {
            RacesConfig racesConfig = getElement().getValue();
            JsonElement jsonElement = JsonMapper.mapObject(racesConfig);
            Json json = new Json(jsonElement, JsonWriters.jsonEPretty());

            Clipboard.write(json.write());
            messages.notifySuccess("notification.races.config.copy");
        } catch (Exception e) {
            messages.errorDialog(e, "notification.races.config.not.copy");
        }
    }

    public void importRacesConfigFromClipboard() {
        try {
            Clipboard.read().ifPresent(s -> {
                Json json = new Json(s, JsonWriters.jsonEPretty());
                RacesConfig racesConfig = JsonMapper.mapJson(json.getRoot(), RacesConfig.class);

                getElement().setValue(racesConfig);
                messages.notifySuccess("notification.races.config.import");
            });
        } catch (Exception e) {
            messages.errorDialog(e, "notification.races.config.not.import");
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
                    getElement().setValue(racesConfig);
                    messages.notifySuccess("notification.races.config.load");
                    racesConfigsSelection.hide();
                } else {
                    messages.notifyError("notification.races.config.not.load");
                }
            } catch (Exception e) {
                messages.errorDialog(e, "notification.races.config.not.load");
            }
        });

        racesConfigsSelection.show();
    }
}
