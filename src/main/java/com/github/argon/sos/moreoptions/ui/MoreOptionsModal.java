package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.ui.panel.*;
import com.github.argon.sos.moreoptions.util.Maps;
import game.VERSION;
import init.paths.ModInfo;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;

import java.util.List;


/**
 * Main window containing all other UI elements.
 * Will pop up in the middle of the game and pauses the game.
 */
@RequiredArgsConstructor
public class MoreOptionsModal extends GuiSection implements
    Showable<MoreOptionsModal>,
    Refreshable<MoreOptionsModal>,
    Valuable<MoreOptionsConfig, MoreOptionsModal> {

    @Getter
    private final ConfigStore configStore;
    @Nullable
    private final ModInfo modInfo;

    @Nullable
    private EventsPanel eventsPanel;
    @Nullable
    private  SoundsPanel soundsPanel;
    @Nullable
    private  WeatherPanel weatherPanel;
    @Nullable
    private  BoostersPanel boostersPanel;

    @Getter
    @Nullable
    private MetricsPanel metricsPanel;

    @Getter
    @Nullable
    private Button cancelButton;
    @Getter
    @Nullable
    private Button resetButton;

    @Getter
    @Nullable
    private Button applyButton;

    @Getter
    @Nullable
    private Button undoButton;

    @Getter
    @Nullable
    private Button okButton;

    @Getter
    @Nullable
    private Button folderButton;

    private UIAction<MoreOptionsModal> showAction = o -> {};

    private UIAction<MoreOptionsModal> refreshAction = o -> {};

    private double updateTimerSeconds = 0d;
    private final static int UPDATE_INTERVAL_SECONDS = 1;

    @Nullable
    private Tabulator<String, ?, GuiSection> tabulator;

    /**
     * Builds the UI with given config
     */
    public void init(MoreOptionsConfig config, List<BoostersPanel.Entry> boosterEntries) {
        clear();
        soundsPanel = new SoundsPanel(config.getSounds());
        eventsPanel = new EventsPanel(config.getEvents());
        weatherPanel = new WeatherPanel(config.getWeather());
        boostersPanel = new BoostersPanel(boosterEntries);
        metricsPanel = new MetricsPanel(config.getMetrics());

        tabulator = new Tabulator<>(Maps.ofLinked(
            Toggler.Info.<String>builder()
                .key("sounds")
                .title("Sounds")
                .description("Tune the volume of various sounds.")
                .build(), soundsPanel,
            Toggler.Info.<String>builder()
                .key("events")
                .title("Events")
                .description("Toggle and tune events.")
                .build(), eventsPanel,
            Toggler.Info.<String>builder()
                .key("weather")
                .title("Weather")
                .description("Influence weather effects.")
                .build(), weatherPanel,
            Toggler.Info.<String>builder()
                .key("boosters")
                .title("Boosters")
                .description("Increase or decrease various bonuses.")
                .build(), boostersPanel,
            Toggler.Info.<String>builder()
                .key("metrics")
                .title("Metrics")
                .description("Collect and export data about the game.")
                .build(), metricsPanel
        ), DIR.S, 30, true, false);

        addDownC(0, tabulator);

        HorizontalLine horizontalLine = new HorizontalLine(body().width(), 14, 1);
        addDownC(20, horizontalLine);

        GuiSection footer = footer(config.getVersion());
        addDownC(20, footer);
    }

    @Nullable
    @Override
    public MoreOptionsConfig getValue() {
        if (eventsPanel == null
            || soundsPanel == null
            || weatherPanel == null
            || boostersPanel == null
            || metricsPanel == null
        ) {
            return null;
        }

        return MoreOptionsConfig.builder()
                .events(eventsPanel.getValue())
                .sounds(soundsPanel.getValue())
                .weather(weatherPanel.getValue())
                .boosters(boostersPanel.getValue())
                .metrics(metricsPanel.getValue())
                .build();
    }

    @Override
    public void setValue(MoreOptionsConfig config) {
        if (eventsPanel != null) eventsPanel.setValue(config.getEvents());
        if (soundsPanel != null) soundsPanel.setValue(config.getSounds());
        if (weatherPanel != null) weatherPanel.setValue(config.getWeather());
        if (boostersPanel != null) boostersPanel.setValue(config.getBoosters());
        if (metricsPanel != null) metricsPanel.setValue(config.getMetrics());
    }

    /**
     * @return whether panel configuration is different from {@link ConfigStore#getCurrentConfig()} ()}
     */
    public boolean isDirty() {
        MoreOptionsConfig config = getValue();

        if (config == null) {
            return false;
        }

        return configStore.getCurrentConfig()
            .map(currentConfig -> !config.equals(currentConfig))
            // no current config in memory
            .orElse(true);
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

    private GuiSection footer(int version) {
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

        section.addRight(50, versions(version));

        section.addRight(50, applyButton);
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

            if (applyButton != null) applyButton.setEnabled(isDirty());
            if (undoButton != null) undoButton.setEnabled(isDirty());
        }
        super.render(r, seconds);
    }

    @Override
    public void show() {
        showAction.accept(this);
    }

    @Override
    public void onShow(UIAction<MoreOptionsModal> showUIAction) {
        this.showAction = showUIAction;
    }

    @Override
    public void refresh() {
        refreshAction.accept(this);
        if (tabulator != null) tabulator.refresh();
    }

    @Override
    public void onRefresh(UIAction<MoreOptionsModal> refreshUIAction) {
        this.refreshAction = refreshUIAction;
    }
}
