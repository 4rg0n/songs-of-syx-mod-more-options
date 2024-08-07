package com.github.argon.sos.moreoptions.ui;


import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV4Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.io.FileService;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersTab;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesSelectionPanel;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesTab;
import game.faction.Faction;
import init.paths.ModInfo;
import init.sprite.SPRITES;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.color.COLOR;
import util.gui.misc.GButt;
import util.save.SaveFile;
import view.ui.top.UIPanelTop;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.argon.sos.moreoptions.MoreOptionsScript.MOD_INFO;

/**
 * Produces new more complex or common UI elements by given configs or from static objects.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UiFactory {

    private static final I18n i18n = I18n.get(UiFactory.class);

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

    public FullWindow<MoreOptionsPanel> buildMoreOptionsFullScreen(String title, MoreOptionsV4Config config) {
        log.debug("Building '%s' full screen", title);
        MoreOptionsPanel moreOptionsPanel = buildMoreOptionsPanel(config)
            .availableWidth(FullWindow.FullView.WIDTH)
            .availableHeight(FullWindow.FullView.HEIGHT)
            .build();
        Switcher<String> buttonMenu = moreOptionsPanel.getTabulator().getMenu();

        return new FullWindow<>(title, moreOptionsPanel, buttonMenu);
    }

    public MoreOptionsPanel.MoreOptionsPanelBuilder buildMoreOptionsPanel(MoreOptionsV4Config config) {
        Map<Faction, List<BoostersTab.Entry>> boosterEntries = uiMapper.toBoosterPanelEntries(config.getBoosters());
        Map<String, List<RacesTab.Entry>> raceEntries = uiMapper.toRacePanelEntries(config.getRaces().getLikings());

        Set<String> availableStats = gameApis.stats().getAvailableStatKeys();
        ModInfo modInfo = gameApis.mod().getCurrentMod().orElse(null);
        Path exportFolder = MetricExporter.EXPORT_FOLDER;
        Path exportFile = metricExporter.getExportFile();
        String saveStamp = gameApis.save().getSaveStamp();

        return MoreOptionsPanel.builder()
            .config(config)
            .saveStamp(saveStamp)
            .configStore(configStore)
            .raceEntries(raceEntries)
            .availableStats(availableStats)
            .boosterEntries(boosterEntries)
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
        List<FileService.FileMeta> raceConfigMetas = configStore.readRacesConfigMetas();
        for (FileService.FileMeta configMeta : raceConfigMetas) {
            SaveFile saveFile = gameApis.save().findByPathContains(configMeta.getPath()).orElse(null);

            RacesSelectionPanel.Entry entry = RacesSelectionPanel.Entry.builder()
                .configPath(configMeta.getPath())
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

    public static ButtonMenu.ButtonMenuBuilder<Level> buildLogLevelButtonMenu() {
        double shade = 0.5;
        return ButtonMenu.<Level>builder()
            .button(Level.CRIT, new Button(
                i18n.t("log.level.crit.name"),
                i18n.t("log.level.crit.desc")
            ).bg(COLOR.RED50.shade(shade)))
            .button(Level.ERROR, new Button(
                i18n.t("log.level.error.name"),
                i18n.t("log.level.error.desc")
            ).bg(COLOR.RED200.shade(shade)))
            .button(Level.WARN, new Button(
                i18n.t("log.level.warn.name"),
                i18n.t("log.level.warn.desc")
            ).bg(COLOR.YELLOW100.shade(shade)))
            .button(Level.INFO, new Button(
                i18n.t("log.level.info.name"),
                i18n.t("log.level.info.desc")
            ).bg(COLOR.BLUE100.shade(shade)))
            .button(Level.DEBUG, new Button(
                i18n.t("log.level.debug.name"),
                i18n.t("log.level.debug.desc")
            ).bg(COLOR.GREEN100.shade(shade)))
            .button(Level.TRACE, new Button(
                i18n.t("log.level.trace.name"),
                i18n.t("log.level.trace.desc")
            ).bg(COLOR.WHITE100.shade(shade)));
    }

    public static GButt.ButtPanel buildMoreOptionsButton(FullWindow<MoreOptionsPanel> moreOptionsModal) {
        GButt.ButtPanel moreOptionsButton = new GButt.ButtPanel(SPRITES.icons().s.cog) {
            @Override
            protected void clickA() {
                moreOptionsModal.show();
            }
        };

        moreOptionsButton.hoverInfoSet(MOD_INFO.name);
        moreOptionsButton.setDim(32, UIPanelTop.HEIGHT);

        return moreOptionsButton;
    }

    public static Window<UiShowroom> buildUiShowRoom() {
        Window<UiShowroom> uiShowRoom = new Window<>("UI Showroom", new UiShowroom());
        uiShowRoom.center();
        return uiShowRoom;
    }
}
