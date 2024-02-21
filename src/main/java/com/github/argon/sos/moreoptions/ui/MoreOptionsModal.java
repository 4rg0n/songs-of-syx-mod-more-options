package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.ConfigUtil;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Button;
import com.github.argon.sos.moreoptions.game.ui.HorizontalLine;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.EventsPanel;
import com.github.argon.sos.moreoptions.ui.panel.SoundsPanel;
import com.github.argon.sos.moreoptions.ui.panel.WeatherPanel;
import com.github.argon.sos.moreoptions.util.UiUtil;
import game.VERSION;
import init.paths.ModInfo;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sets.Tuple;
import util.gui.misc.GButt;
import util.gui.misc.GText;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Window containing all other UI elements.
 * Will pop up in the middle of the game and pauses the game.
 *
 * FIXME BUG? hovering over elements behind the window will trigger ui elements beneath e.g. showing tooltips from citizens
 */
@RequiredArgsConstructor
public class MoreOptionsModal extends GuiSection {

    @Getter
    private final ConfigStore configStore;
    private final ModInfo modInfo;

    private CLICKABLE.ClickSwitch switcher;

    private EventsPanel eventsPanel;
    private  SoundsPanel soundsPanel;
    private  WeatherPanel weatherPanel;

    private  BoostersPanel boostersPanel;

    @Getter
    private Button cancelButton;
    @Getter
    private Button resetButton;

    @Getter
    private Button applyButton;

    @Getter
    private Button undoButton;

    @Getter
    private Button okButton;

    @Getter
    private Button folderButton;

    private double updateTimerSeconds = 0d;
    private final static int UPDATE_INTERVAL_SECONDS = 1;

    /**
     * Builds the UI with given config
     */
    public void init(MoreOptionsConfig config, List<BoostersPanel.Entry> boosterEntries) {
        clear();
        soundsPanel = new SoundsPanel(config.getSoundsAmbience(), config.getSoundsSettlement(), config.getSoundsRoom());
        eventsPanel = new EventsPanel(config.getEventsSettlement(), config.getEventsWorld(), config.getEventsChance());
        weatherPanel = new WeatherPanel(config.getWeather());
        boostersPanel = new BoostersPanel(boosterEntries);

        Map<Tuple<String, String>, GuiSection> panels = new LinkedHashMap<>();
        panels.put(new Tuple.TupleImp<>("Sounds", "Tune the volume of various sounds."), soundsPanel);
        panels.put(new Tuple.TupleImp<>("Events", "Toggle and tune events."), eventsPanel);
        panels.put(new Tuple.TupleImp<>("Weather", "Influence weather effects."), weatherPanel);
        panels.put(new Tuple.TupleImp<>("Boosters", "Increase or decrease various bonuses."), boostersPanel);

        GuiSection header = header(panels, config);
        addDownC(0, header);

        switcher = new CLICKABLE.ClickSwitch(soundsPanel);
        addDownC(20, switcher);

        List<RENDEROBJ> widths = new ArrayList<>(panels.values());
        widths.add(header);
        int width = UiUtil.getMaxWidth(widths);
        HorizontalLine horizontalLine = new HorizontalLine(width, 14, 1);
        addDownC(20, horizontalLine);

        GuiSection footer = footer();
        addDownC(20, footer);
    }

    public MoreOptionsConfig getConfig() {
        MoreOptionsConfig defaultConfig = configStore.getDefaultConfig();
        MoreOptionsConfig currentConfig = configStore.getCurrentConfig()
                .orElse(defaultConfig);


        return MoreOptionsConfig.builder()
                .eventsSettlement(eventsPanel.getSettlementEventsConfig())
                .eventsWorld(eventsPanel.getWorldEventsConfig())
                .eventsChance(ConfigUtil.mergeIntoNewRange(eventsPanel.getEventsChanceConfig(), currentConfig.getEventsChance()))
                .soundsAmbience(ConfigUtil.mergeIntoNewRange(soundsPanel.getSoundsAmbienceConfig(), currentConfig.getSoundsAmbience()))
                .soundsSettlement(ConfigUtil.mergeIntoNewRange(soundsPanel.getSoundsSettlementConfig(), currentConfig.getSoundsSettlement()))
                .soundsRoom(ConfigUtil.mergeIntoNewRange(soundsPanel.getSoundsRoomConfig(), currentConfig.getSoundsRoom()))
                .weather(ConfigUtil.mergeIntoNewRange(weatherPanel.getConfig(), currentConfig.getWeather()))
                .boosters(ConfigUtil.mergeIntoNewRange(boostersPanel.getConfig(), currentConfig.getBoosters()))
                .build();
    }


    public void applyConfig(MoreOptionsConfig config) {
        eventsPanel.applyConfig(
            config.getEventsSettlement(),
            config.getEventsWorld(),
            ConfigUtil.extract(config.getEventsChance())
        );
        soundsPanel.applyConfig(
            ConfigUtil.extract(config.getSoundsAmbience()),
            ConfigUtil.extract(config.getSoundsSettlement()),
            ConfigUtil.extract(config.getSoundsRoom())
        );
        weatherPanel.applyConfig(ConfigUtil.extract(config.getWeather()));
        boostersPanel.applyConfig(ConfigUtil.extract(config.getBoosters()));
    }

    /**
     * @return whether panel configuration is different from {@link ConfigStore#getCurrentConfig()} ()}
     */
    public boolean isDirty() {
        return configStore.getCurrentConfig()
            .map(currentConfig -> !getConfig().equals(currentConfig))
            // no current config in memory
            .orElse(true);
    }

    private GuiSection header(Map<Tuple<String, String>, GuiSection> panels, MoreOptionsConfig config) {
        GuiSection section = new GuiSection();
        GuiSection versions = versions(config.getVersion());
        GuiSection space = new GuiSection();
        space.body().setWidth(versions.body().width());
        section.addRightC(0, space);

        panels.forEach((titleDesc, panel) -> {

            GButt.ButtPanel button = new GButt.ButtPanel(titleDesc.a()) {
                @Override
                protected void clickA() {
                    switcher.set(panel);
                }

                @Override
                protected void renAction() {
                    selectedSet(switcher.current() == panel);
                }
            };
            button.hoverInfoSet(titleDesc.b());
            section.addRightC(0, button.setDim(136, 32));
        });

        section.addRightC(50, versions);

        return section;
    }

    private GuiSection versions(int configVersionNumber) {
        COLOR color = COLOR.WHITE50;
        GuiSection versions = new GuiSection();

        GText gameVersion = new GText(UI.FONT().S, "Game Version: " + VERSION.VERSION_STRING);
        gameVersion.color(color);

        String modVersionString = "NO_VER";
        if (modInfo != null) {
            modVersionString = modInfo.version;
        }

        GText modVersion = new GText(UI.FONT().S, "Mod Version: " + modVersionString);
        modVersion.color(color);

        GText configVersion = new GText(UI.FONT().S, "Config Version: " + configVersionNumber);
        configVersion.color(color);

        versions.addDown(0, gameVersion);
        versions.addDown(2, modVersion);
        versions.addDown(2, configVersion);

        return versions;
    }

    private GuiSection footer() {
        GuiSection section = new GuiSection();

        this.cancelButton = new Button("Cancel", COLOR.WHITE25);
        cancelButton.hoverInfoSet("Closes window without applying changes");
        section.addRight(0, cancelButton);

        this.folderButton = new Button("Folder", COLOR.WHITE25);
        folderButton.hoverInfoSet("Opens settings folder with mod config");
        section.addRight(10, folderButton);

        this.resetButton = new Button("Default", COLOR.WHITE25);
        resetButton.hoverInfoSet("Resets to default options");
        section.addRight(10, resetButton);

        this.undoButton = new Button("Undo", COLOR.WHITE25);
        undoButton.hoverInfoSet("Undo made changes");
        undoButton.setEnabled(false);
        section.addRight(10, undoButton);

        this.applyButton = new Button("Apply", COLOR.WHITE15);
        applyButton.hoverInfoSet("Apply and save options");
        applyButton.setEnabled(false);
        section.addRight(300, applyButton);

        this.okButton = new Button("OK", COLOR.WHITE15);
        okButton.hoverInfoSet("Apply and exit");
        section.addRight(10, okButton);

        return section;
    }

    @Override
    public void render(SPRITE_RENDERER r, float seconds) {
        updateTimerSeconds += seconds;

        // check for changes in config
        if (updateTimerSeconds >= UPDATE_INTERVAL_SECONDS) {
            updateTimerSeconds = 0d;

            applyButton.setEnabled(isDirty());
            undoButton.setEnabled(isDirty());
        }
        super.render(r, seconds);
    }
}
