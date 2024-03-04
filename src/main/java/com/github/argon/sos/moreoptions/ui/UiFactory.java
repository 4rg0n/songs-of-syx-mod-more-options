package com.github.argon.sos.moreoptions.ui;


import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.Modal;
import com.github.argon.sos.moreoptions.game.ui.Window;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesConfigSelectionPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesPanel;
import init.paths.ModInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import util.save.SaveFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UiFactory {

    @Getter(lazy = true)
    private final static UiFactory instance = new UiFactory(
        GameApis.getInstance(),
        ConfigStore.getInstance(),
        MetricExporter.getInstance(),
        UiMapper.getInstance()
    );

    private final static Logger log = Loggers.getLogger(UiFactory.class);

    private final GameApis gameApis;
    private final ConfigStore configStore;
    private final MetricExporter metricExporter;
    private final UiMapper uiMapper;

    /**
     * Generates UI with available config.
     * Adds config modal buttons functionality.
     *
     * @param config used to generate the UI
     */
    public Modal<MoreOptionsPanel> buildMoreOptionsModal(String title, MoreOptionsV2Config config) {
        log.debug("Build %s ui", title);

        List<BoostersPanel.Entry> boosterEntries = uiMapper.mapToBoosterPanelEntries(config.getBoosters());
        Map<String, List<RacesPanel.Entry>> raceEntries = uiMapper.mapToRacePanelEntries(config.getRaces().getLikings());

        List<String> availableStats = gameApis.stats().getAvailableStatKeys();
        ModInfo modInfo = gameApis.mod().getCurrentMod().orElse(null);
        Path exportFolder = MetricExporter.EXPORT_FOLDER;
        Path exportFile = metricExporter.getExportFile();

        Modal<MoreOptionsPanel> moreOptionsModal = new Modal<>(title, new MoreOptionsPanel(
            config,
            configStore,
            boosterEntries,
            raceEntries,
            availableStats,
            exportFolder,
            exportFile,
            modInfo
        ));
        moreOptionsModal.center();

        return moreOptionsModal;
    }

    public Window<RacesConfigSelectionPanel> buildRacesConfigSelection(String title) {
        log.debug("Build %s ui", title);
        RacesConfigSelectionPanel.Entry current = null;

        // prepare entries
        List<RacesConfigSelectionPanel.Entry> racesConfigs = new ArrayList<>();
        List<ConfigStore.RaceConfigMeta> raceConfigMetas = configStore.loadRacesConfigMetas();
        for (ConfigStore.RaceConfigMeta configMeta : raceConfigMetas) {
            SaveFile saveFile = gameApis.save().findByPathContains(configMeta.getConfigPath()).orElse(null);

            RacesConfigSelectionPanel.Entry entry = RacesConfigSelectionPanel.Entry.builder()
                .configPath(configMeta.getConfigPath())
                .creationDate(configMeta.getCreationTime())
                .updateDate(configMeta.getUpdateTime())
                .saveFile(saveFile)
                .build();

            SaveFile currentFile = gameApis.save().getCurrentFile();

            if (saveFile != null && currentFile != null && saveFile.fullName.equals(currentFile.fullName)) {
                current = entry;
            }

            racesConfigs.add(entry);
        }

        Window<RacesConfigSelectionPanel> window = new Window<>(title, new RacesConfigSelectionPanel(racesConfigs, current));
        window.center();

        return window;
    }
}
