package com.github.argon.sos.moreoptions.ui;


import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesSelectionPanel;
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
import java.util.Set;


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

    public FullWindow<MoreOptionsPanel> buildMoreOptionsFullScreen(String title, MoreOptionsV2Config config) {
        log.debug("Building '%s' full screen", title);
        MoreOptionsPanel moreOptionsPanel = buildMoreOptionsPanel(config)
            .availableWidth(FullWindow.FullView.WIDTH)
            .availableHeight(FullWindow.FullView.HEIGHT)
            .build();
        Toggler<String> buttonMenu = moreOptionsPanel.getTabulator().getMenu();

        return new FullWindow<>(title, moreOptionsPanel, buttonMenu);
    }

    public MoreOptionsPanel.MoreOptionsPanelBuilder buildMoreOptionsPanel(MoreOptionsV2Config config) {
        List<BoostersPanel.Entry> boosterEntries = uiMapper.toBoosterPanelEntries(config.getBoosters());
        Map<String, List<RacesPanel.Entry>> raceEntries = uiMapper.toRacePanelEntries(config.getRaces().getLikings());

        Set<String> availableStats = gameApis.stats().getAvailableStatKeys();
        ModInfo modInfo = gameApis.mod().getCurrentMod().orElse(null);
        Path exportFolder = MetricExporter.EXPORT_FOLDER;
        Path exportFile = metricExporter.getExportFile();

        return MoreOptionsPanel.builder()
            .config(config)
            .configStore(configStore)
            .boosterEntries(boosterEntries)
            .raceEntries(raceEntries)
            .availableStats(availableStats)
            .modInfo(modInfo)
            .exportFolder(exportFolder)
            .exportFile(exportFile);
    }

    /**
     * Generates race config selection window
     */
    public Window<RacesSelectionPanel> buildRacesConfigSelection(String title) {
        log.debug("Building '%s' ui", title);
        RacesSelectionPanel.Entry current = null;

        // prepare entries
        List<RacesSelectionPanel.Entry> racesConfigs = new ArrayList<>();
        List<ConfigStore.RaceConfigMeta> raceConfigMetas = configStore.loadRacesConfigMetas();
        for (ConfigStore.RaceConfigMeta configMeta : raceConfigMetas) {
            SaveFile saveFile = gameApis.save().findByPathContains(configMeta.getConfigPath()).orElse(null);

            RacesSelectionPanel.Entry entry = RacesSelectionPanel.Entry.builder()
                .configPath(configMeta.getConfigPath())
                .creationDate(configMeta.getCreationTime())
                .updateDate(configMeta.getUpdateTime())
                .saveFile(saveFile)
                .build();
            SaveFile currentFile = gameApis.save().getCurrentFile();

            // is the file the currently active one?
            if (current == null && (
                saveFile != null &&
                currentFile != null &&
                saveFile.fullName.equals(currentFile.fullName)
            )) {
                current = entry;
            }

            racesConfigs.add(entry);
        }

        Window<RacesSelectionPanel> window = new Window<>(title, new RacesSelectionPanel(racesConfigs, current));
        window.center();

        return window;
    }
}
