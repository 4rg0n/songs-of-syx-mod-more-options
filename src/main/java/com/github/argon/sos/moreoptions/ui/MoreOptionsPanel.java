package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.api.GameUiApi;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.ui.panel.*;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.Maps;
import game.VERSION;
import init.paths.ModInfo;
import init.sprite.UI.UI;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;


/**
 * Main window containing all other UI elements.
 * Will pop up in the middle of the game and pauses the game.
 */
public class MoreOptionsPanel extends GuiSection implements
    Showable<MoreOptionsPanel>,
    Refreshable<MoreOptionsPanel>,
    Valuable<MoreOptionsV2Config, MoreOptionsPanel> {

    @Getter
    private final ConfigStore configStore;

    private final EventsPanel eventsPanel;
    private final SoundsPanel soundsPanel;
    private final WeatherPanel weatherPanel;
    private final BoostersPanel boostersPanel;
    @Getter
    private final MetricsPanel metricsPanel;
    private final RacesPanel racesPanel;

    @Getter
    private final Button cancelButton;
    @Getter
    private final Button defaultButton;
    @Getter
    private final Button applyButton;
    @Getter
    private final Button undoButton;
    @Getter
    private final Button okButton;
    @Getter
    private final Button folderButton;
    @Getter
    private final Button moreButton;
    @Getter
    private final Button reloadButton;
    @Getter
    private final Button shareButton;
    @Getter
    private final Button resetButton;
    @Getter
    private final ButtonMenu moreButtonMenu;

    private Action<MoreOptionsPanel> showAction = o -> {};
    private Action<MoreOptionsPanel> refreshAction = o -> {};

    private double updateTimerSeconds = 0d;
    private final static int UPDATE_INTERVAL_SECONDS = 1;

    /**
     * Builds the UI with given config
     */
    public MoreOptionsPanel(
        MoreOptionsV2Config config,
        ConfigStore configStore,
        List<BoostersPanel.Entry> boosterEntries,
        Map<String, List<RacesPanel.Entry>> raceEntries,
        List<String> availableStats,
        Path exportFolder,
        Path exportFile,
        @Nullable ModInfo modInfo
    ) {
        this.configStore = configStore;

        soundsPanel = new SoundsPanel(config.getSounds());
        eventsPanel = new EventsPanel(config.getEvents());
        weatherPanel = new WeatherPanel(config.getWeather());
        boostersPanel = new BoostersPanel(boosterEntries);
        metricsPanel = new MetricsPanel(config.getMetrics(), availableStats, exportFolder, exportFile);
        racesPanel = new RacesPanel(raceEntries);

        Tabulator<String, ?, GuiSection> tabulator = new Tabulator<>(Maps.ofLinked(
            UiInfo.<String>builder()
                .key("sounds")
                .title("Sounds")
                .description("Tune the volume of various sounds.")
                .build(), soundsPanel,
            UiInfo.<String>builder()
                .key("events")
                .title("Events")
                .description("Toggle and tune events.")
                .build(), eventsPanel,
            UiInfo.<String>builder()
                .key("weather")
                .title("Weather")
                .description("Influence weather effects.")
                .build(), weatherPanel,
            UiInfo.<String>builder()
                .key("boosters")
                .title("Boosters")
                .description("Increase or decrease various bonuses.")
                .build(), boostersPanel,
            UiInfo.<String>builder()
                .key("metrics")
                .title("Metrics")
                .description("Collect and export data about the game.")
                .build(), metricsPanel
            , UiInfo.<String>builder()
                .key("races")
                .title("Races")
                .description("TODO")
                .build(), racesPanel
        ), DIR.S, 30, 0, true, false);
        addDownC(0, tabulator);

        HorizontalLine horizontalLine = new HorizontalLine(body().width(), 14, 1);
        addDownC(20, horizontalLine);

        GuiSection footer = new GuiSection();
        this.cancelButton = new Button("Cancel");
        cancelButton.hoverInfoSet("Close window without applying changes");
        footer.addRight(0, cancelButton);

        this.undoButton = new Button("Undo");
        undoButton.hoverInfoSet("Undo made changes");
        undoButton.setEnabled(false);
        footer.addRight(10, undoButton);

        this.moreButton = new Button("More", COLOR.WHITE15);
        footer.addRight(50, moreButton);

        String modVersion = "NO_VER";
        if (modInfo != null) {
            modVersion = modInfo.version;
        }

        footer.addRight(50, versions(config.getVersion(), modVersion));

        this.applyButton = new Button("Apply");
        applyButton.hoverInfoSet("Apply config to game and save to file");
        applyButton.setEnabled(false);
        footer.addRight(50, applyButton);

        this.okButton = new Button("OK");
        okButton.hoverInfoSet("Apply config to game, save to file and exit");
        footer.addRight(10, okButton);
        addDownC(20, footer);

        // More Button Menu
        this.defaultButton = new Button("Default");
        defaultButton.hoverInfoSet("Reset ui to default config");
        this.reloadButton = new Button("Reload");
        reloadButton.hoverInfoSet("Reload and apply config to ui from file");
        this.shareButton = new Button("Share");
        shareButton.hoverInfoSet("Copy current config from ui into clipboard");
        this.folderButton = new Button("Folder");
        folderButton.hoverInfoSet("Open settings folder with mod config file");
        this.resetButton = new Button("Reset");
        resetButton.hoverInfoSet("Reset ui and game to default config and deletes config file");
        List<Button> buttons = Lists.of(defaultButton, shareButton, folderButton, reloadButton, resetButton);
        this.moreButtonMenu = ButtonMenu.fromList(buttons);
        this.moreButton.clickActionSet(() -> {
            GameUiApi.getInstance().popup().show(this.moreButtonMenu, this.moreButton);
        });
    }

    @Nullable
    @Override
    public MoreOptionsV2Config getValue() {
        return MoreOptionsV2Config.builder()
                .events(eventsPanel.getValue())
                .sounds(soundsPanel.getValue())
                .weather(weatherPanel.getValue())
                .boosters(boostersPanel.getValue())
                .metrics(metricsPanel.getValue())
                .races(racesPanel.getValue())
                .build();
    }

    @Override
    public void setValue(MoreOptionsV2Config config) {
        eventsPanel.setValue(config.getEvents());
        soundsPanel.setValue(config.getSounds());
        weatherPanel.setValue(config.getWeather());
        boostersPanel.setValue(config.getBoosters());
        metricsPanel.setValue(config.getMetrics());
        racesPanel.setValue(config.getRaces());
    }

    /**
     * @return whether panel configuration is different from {@link ConfigStore#getCurrentConfig()} ()}
     */
    public boolean isDirty() {
        MoreOptionsV2Config config = getValue();

        if (config == null) {
            return false;
        }

        return !configStore.getCurrentConfig().equals(config);
    }

   private GuiSection versions(int configVersionNumber, String modVersionString) {
        COLOR color = COLOR.WHITE50;
        GuiSection versions = new GuiSection();

        GText gameVersion = new GText(UI.FONT().S, "Game Version: " + VERSION.VERSION_STRING);
        gameVersion.color(color);

        GText modVersion = new GText(UI.FONT().S, "Mod Version: " + modVersionString);
        modVersion.color(color);

        GText configVersion = new GText(UI.FONT().S, "Config Version: " + configVersionNumber);
        configVersion.color(color);

        versions.addDown(0, gameVersion);
        versions.addDown(2, modVersion);
        versions.addDown(2, configVersion);

        return versions;
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
    public void onShow(Action<MoreOptionsPanel> showAction) {
        this.showAction = showAction;
    }

    @Override
    public void refresh() {
        refreshAction.accept(this);
    }

    @Override
    public void onRefresh(Action<MoreOptionsPanel> refreshAction) {
        this.refreshAction = refreshAction;
    }
}
